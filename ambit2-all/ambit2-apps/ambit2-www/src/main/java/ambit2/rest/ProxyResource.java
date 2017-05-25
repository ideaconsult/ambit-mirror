package ambit2.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.ServerInfo;
import org.restlet.data.Status;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.AbstractResource;
import net.idea.restnet.c.exception.RResourceException;

public class ProxyResource<T> extends AbstractResource<List<NameValuePair>, T, RemoteStreamConvertor> {
	protected enum supported_media {
		json, img {
			@Override
			public MediaType getMediaType() {
				return MediaType.IMAGE_PNG;
			}
		};
		public MediaType getMediaType() {
			return MediaType.APPLICATION_JSON;
		}

	};

	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(
				new MediaType[] { MediaType.APPLICATION_JSON, MediaType.APPLICATION_JAVASCRIPT, MediaType.IMAGE_PNG });
	}

	protected supported_media media = supported_media.json;

	@Override
	public RemoteStreamConvertor createConvertor(Variant variant) throws AmbitException, ResourceException {
		return new RemoteStreamConvertor(getQueryService(), queryObject, getRequest().getMethod(),
				media.getMediaType());
	}

	public RemoteStreamConvertor createConvertor(Representation entity, Variant variant)
			throws AmbitException, ResourceException {
		return new RemoteStreamConvertor(getQueryService(), queryObject, getRequest().getMethod(),
				media.getMediaType());
	}

	@Override
	protected List<NameValuePair> createQuery(Context context, Request request, Response response)
			throws ResourceException {
		try {
			try {
				media = supported_media.valueOf(request.getAttributes().get("media").toString());
			} catch (Exception x) {
				media = supported_media.json;
			}
			if (Method.GET.equals(request.getMethod()))
				return form2nvp(new Form(request.getResourceRef().getQuery()));
			else
				return form2nvp(request.getEntityAsForm());
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}

	protected List<NameValuePair> form2nvp(Form form) {
		List<NameValuePair> nvp = new ArrayList<NameValuePair>();
		Iterator<Parameter> p = form.iterator();
		while (p.hasNext()) {
			Parameter prm = p.next();
			nvp.add(new BasicNameValuePair(prm.getName(), prm.getValue()));
		}
		return nvp;
	}

	@Override
	protected Representation processAndGenerateTask(Method method, Representation entity, Variant variant,
			boolean async) throws ResourceException {
		return null;
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		Form headers = (Form) getResponse().getAttributes().get("org.restlet.http.headers");
		if (headers == null) {
			headers = new Form();
			getResponse().getAttributes().put("org.restlet.http.headers", headers);
		}
		headers.removeAll("X-Frame-Options");
		headers.add("X-Frame-Options", "SAMEORIGIN");
		getResponse().getCacheDirectives().add(CacheDirective.proxyMustRevalidate());
		ServerInfo si = getResponse().getServerInfo();
		si.setAgent(getApplication().getName());
		getResponse().setServerInfo(si);
		return proxy(null, variant);
	}

	@Override
	protected Representation get() throws ResourceException {
		return get(new Variant(media.getMediaType()));
	}

	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		Form headers = (Form) getResponse().getAttributes().get("org.restlet.http.headers");
		if (headers == null) {
			headers = new Form();
			getResponse().getAttributes().put("org.restlet.http.headers", headers);
		}
		headers.removeAll("X-Frame-Options");
		headers.add("X-Frame-Options", "SAMEORIGIN");
		getResponse().getCacheDirectives().add(CacheDirective.proxyMustRevalidate());
		ServerInfo si = getResponse().getServerInfo();
		si.setAgent(getApplication().getName());
		getResponse().setServerInfo(si);
		return proxy(entity, variant);
	}

	@Override
	protected Representation post(Representation entity) throws ResourceException {
		return post(entity, new Variant(media.getMediaType()));
	}

	protected Representation proxy(Representation entity, Variant variant) throws ResourceException {
		try {
			setXHeaders();
			setCacheHeaders();
			setTokenCookies(variant, useSecureCookie(getRequest()));
			setStatus(Status.SUCCESS_OK);
			if (MediaType.APPLICATION_JAVA_OBJECT.equals(variant.getMediaType())) {
				if ((queryObject != null) && (queryObject instanceof Serializable))
					return new ObjectRepresentation((Serializable) queryObject, MediaType.APPLICATION_JAVA_OBJECT);
				else
					throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE);
			}
			if (queryObject != null) {
				IProcessor<List<NameValuePair>, Representation> convertor = null;

				try {
					getResponse().setStatus(response_status);
					convertor = createConvertor(entity, variant);
					Representation r = convertor.process(queryObject);

					return r;
				} catch (NotFoundException x) {
					Representation r = processNotFound(x, variant);
					return r;

				} catch (RResourceException x) {
					getResponse().setStatus(x.getStatus());
					return x.getRepresentation();

				} catch (ResourceException x) {
					getResponse().setStatus(x.getStatus());
					return null;
				} catch (Exception x) {

					getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, x);
					return null;
				} finally {

				}

			} else {
				getResponse().setStatus(response_status == null ? Status.CLIENT_ERROR_BAD_REQUEST : response_status,
						error);
				return null;
			}
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, x);
			return null;
		} finally {
			close();
		}
	}

	protected String getQueryService() {
		return ((AmbitApplication) getApplication()).getSolrURI();
	}
}
