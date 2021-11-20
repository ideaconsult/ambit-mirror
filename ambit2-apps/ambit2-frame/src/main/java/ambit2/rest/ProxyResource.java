package ambit2.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.ServerInfo;
import org.restlet.data.Status;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import com.google.common.io.ByteStreams;

import ambit2.rest.study.BucketJson2CSVConvertor;
import ambit2.rest.wrapper.WrappedService;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.AbstractResource;
import net.idea.restnet.c.exception.RResourceException;

public class ProxyResource<T> extends AbstractResource<ByteArrayOutputStream, T, RemoteStreamConvertor> {
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

	public static final String resourcekey = "handler";
	public static final String servicekey = "servicekey";
	public static final String idkey = "idkey";
	public static final String propertykey = "propertykey";
	protected Map<String, WrappedService<UsernamePasswordCredentials>> solrServices;
	protected WrappedService<UsernamePasswordCredentials> solrService;
	protected boolean json2tsv = false;
	protected enum _sep  {
			tab {
				public String toString() {
					return "\t";
				}				
			},
			comma {
				@Override
				public String toString() {
					return ",";
				}
				@Override
				public String getExt() {
					return "csv";
				}
			};
			public String toString() {
				return "\t";
			}
			public String getExt() {
				return "txt";
			}
	}
	protected _sep sep=_sep.tab;

	public ProxyResource() {
		super();
		solrServices = ((AmbitFreeMarkerApplication) getApplication()).getProperties().getSolrServices();
		setHtmlbyTemplate(true);
	}

	@Override
	public String getTemplateName() {
		return "jsonplaceholder.ftl";
	}

	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] { MediaType.APPLICATION_JSON, MediaType.APPLICATION_JAVASCRIPT,
				MediaType.IMAGE_PNG, MediaType.TEXT_CSV, MediaType.TEXT_TSV, MediaType.TEXT_XML });
	}

	// protected supported_media media = supported_media.json;

	@Override
	public RemoteStreamConvertor createConvertor(Variant variant) throws AmbitException, ResourceException {
		return createConvertor(getRequestEntity(), variant);
	}

	public RemoteStreamConvertor createConvertor(Representation entity, Variant variant)
			throws AmbitException, ResourceException {
		return new RemoteStreamConvertor(solrService, queryObject, entity == null ? null : entity.getMediaType(),
				getRequest().getMethod(), (json2tsv) ? MediaType.TEXT_TSV : variant.getMediaType()) {
			@Override
			public String getFileExtension() {
				return json2tsv ? sep.getExt() : super.getFileExtension();
			}

			@Override
			public String getFileNamePrefix() {
				return json2tsv ? "study" : super.getFileNamePrefix();
			}

			@Override
			protected void processStream(InputStream in, OutputStream stream) throws IOException {
				if (json2tsv) {
					BucketJson2CSVConvertor bj = new BucketJson2CSVConvertor(sep.toString());
					bj.setOut(stream);
					try {
						bj.process(in);
					} catch (Exception x) {
						//x.printStackTrace();
					}

				} else
					super.processStream(in, stream);
			}
		};
	}

	@Override
	protected ByteArrayOutputStream createQuery(Context context, Request request, Response response)
			throws ResourceException {

		try {
			try {
				Object key = request.getAttributes().get(servicekey);
				solrService = solrServices.get(key.toString());
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
			if (solrService == null)
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			try {
				Object handler = request.getAttributes().get(resourcekey);
				solrService.setHandler(handler.toString());
			} catch (Exception x) {
				solrService.setHandler(null);
			}
			
			try {
				Object handler = request.getAttributes().get(idkey);
				solrService.setIdkey(handler.toString());
			} catch (Exception x) {
				solrService.setIdkey(null);
			}
			try {
				Object handler = request.getAttributes().get(propertykey);
				solrService.setPropertykey(handler.toString());
			} catch (Exception x) {
				solrService.setPropertykey(null);
			}			
			
			Form query = request.getResourceRef().getQueryAsForm();
			try {
				String prm = query.getFirstValue("json2tsv");
				if (prm != null) {
					json2tsv = Boolean.parseBoolean(prm);
					query.remove("json2tsv");
				}
			} catch (Exception x) {
				json2tsv = false;
			}

			try {
				sep=_sep.tab;
				String prm = query.getFirstValue("sep");
				if (prm != null) {
					if (",".equals(prm))
						sep = _sep.comma;
					query.remove("sep");
				}
			} catch (Exception x) {
				sep=_sep.tab;
			}
			
			solrService.setQuery(query.encode());

			ByteArrayOutputStream bout = null;
			if (!Method.GET.equals(request.getMethod())) {
				bout = new ByteArrayOutputStream();
				ByteStreams.copy(getRequest().getEntity().getStream(), bout);
				getRequest().getEntity().exhaust();
				getRequest().getEntity().release();
			}
			return bout;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}

	@Override
	protected Representation processAndGenerateTask(Method method, Representation entity, Variant variant,
			boolean async) throws ResourceException {
		return null;
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		/*
		 * Form headers = (Form)
		 * getResponse().getAttributes().get("org.restlet.http.headers"); if
		 * (headers == null) { headers = new Form();
		 * getResponse().getAttributes().put("org.restlet.http.headers",
		 * headers); } headers.removeAll("X-Frame-Options");
		 * headers.add("X-Frame-Options", "SAMEORIGIN");
		 * getResponse().getCacheDirectives().add(CacheDirective.
		 * proxyMustRevalidate());
		 */
		ServerInfo si = getResponse().getServerInfo();
		si.setAgent(getApplication().getName());
		getResponse().setServerInfo(si);
		return proxy(null, variant);
	}

	@Override
	protected Representation get() throws ResourceException {
		return get(new Variant(MediaType.APPLICATION_JSON));
	}

	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		/*
		 * Form headers = (Form)
		 * getResponse().getAttributes().get("org.restlet.http.headers"); if
		 * (headers == null) { headers = new Form();
		 * getResponse().getAttributes().put("org.restlet.http.headers",
		 * headers); } headers.removeAll("X-Frame-Options");
		 * headers.add("X-Frame-Options", "SAMEORIGIN");
		 * getResponse().getCacheDirectives().add(CacheDirective.
		 * proxyMustRevalidate());
		 */
		ServerInfo si = getResponse().getServerInfo();
		si.setAgent(getApplication().getName());
		getResponse().setServerInfo(si);
		return proxy(entity, variant);
	}

	@Override
	protected Representation post(Representation entity) throws ResourceException {
		return post(entity, new Variant(MediaType.APPLICATION_JSON));
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

			IProcessor<ByteArrayOutputStream, Representation> convertor = null;

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

		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, x);
			return null;
		} finally {
			close();
		}
	}

}
