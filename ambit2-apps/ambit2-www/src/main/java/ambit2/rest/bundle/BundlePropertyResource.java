package ambit2.rest.bundle;

import java.awt.Dimension;
import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.update.bundle.endpoints.ReadEndpointsByBundle;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OpenTox;
import ambit2.rest.property.PropertyJSONReporter;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.query.AmbitDBResource;

public class BundlePropertyResource<Q extends IQueryRetrieval<Property>> extends AmbitDBResource<Q,Property> {

	public BundlePropertyResource() {
		super();
		setHtmlbyTemplate(true);
	}
	
	@Override
	public String getTemplateName() {
		return "feature.ftl";
	}
	
	@Override
	protected String getObjectURI(Form queryForm) throws ResourceException {
		return null;
	}
		
	@Override
	protected CallableProtectedTask<String> createCallable(Method method,	Form form, Property item) throws ResourceException {
		SubstanceEndpointsBundle bundle = null;
		Object id = getRequest().getAttributes().get(OpenTox.URI.bundle.getKey());	
		if ((id!=null)) try {
			bundle = new SubstanceEndpointsBundle(new Integer(Reference.decode(id.toString())));
		} catch (Exception x) {}
		
		Connection conn = null;
		try {
			PropertyURIReporter r = new PropertyURIReporter(getRequest());
			DBConnection dbc = new DBConnection(getApplication().getContext(),getConfigFile());
			conn = dbc.getConnection(30,true,8);
			return new CallableEndpointsBundle(bundle,r,method,form,conn,getToken());
		} catch (Exception x) {
			x.printStackTrace();
			try { conn.close(); } catch (Exception xx) {}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
	}
	@Override
	protected boolean isAllowedMediaType(MediaType mediaType)
			throws ResourceException {
		return MediaType.APPLICATION_WWW_FORM.equals(mediaType);
	}
	
	@Override
	protected Q createQuery(Context context, Request request, Response response) throws ResourceException {
		Object idbundle = request.getAttributes().get(OpenTox.URI.bundle.getKey());
		if (idbundle != null)  try {
			Integer idnum = new Integer(Reference.decode(idbundle.toString()));
			ReadEndpointsByBundle q = new ReadEndpointsByBundle();
			SubstanceEndpointsBundle b = new SubstanceEndpointsBundle();
			b.setID(idnum);
			q.setFieldname(b);
			//q.setValue(value);
			return (Q)q;
		} catch (NumberFormatException x) {
		}
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}	
	
	
	@Override
	public String getConfigFile() {
		return "ambit2/rest/config/ambit2.pref";
	}
	

	@Override
	protected Q createUpdateQuery(Method method, Context context,
			Request request, Response response) throws ResourceException {
		Object idbundle = request.getAttributes().get(OpenTox.URI.bundle.getKey());
		if (idbundle==null) throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
		
		Object idsubstance = request.getAttributes().get(OpenTox.URI.substance.getKey());
		if (Method.POST.equals(method) || Method.PUT.equals(method)) {
			if (idsubstance==null) return null;//post allowed only on /bundle/{id}/substance level, not on /bundle/id/substance/{idsubstance}
		} else {
			if (idsubstance!=null) {
				try {
						Integer idnum = new Integer(Reference.decode(idbundle.toString()));
						SubstanceEndpointsBundle dataset = new SubstanceEndpointsBundle();
						dataset.setID(idnum);
						ReadEndpointsByBundle query = new ReadEndpointsByBundle();
						query.setFieldname(dataset);
						return (Q)query;
				} catch (NumberFormatException x) {
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
				}
			}
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		Dimension d = new Dimension(250,250);
		try {
			d.width = Integer.parseInt(acceptform.getFirstValue("w").toString());
		} catch (Exception x) {}
		try {
			d.height = Integer.parseInt(acceptform.getFirstValue("h").toString());
		} catch (Exception x) {}		
		
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) variant.setMediaType(new MediaType(media));

		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			QueryURIReporter r = (QueryURIReporter)getURIReporter(getRequest());
			return new StringConvertor(
					r,MediaType.TEXT_URI_LIST,filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
			return new ImageConvertor(
					new ImageReporter(variant.getMediaType().getMainType(),variant.getMediaType().getSubType(),d),
					variant.getMediaType());
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			String jsonpcallback = getParams().getFirstValue("jsonp");
			if (jsonpcallback==null) jsonpcallback = getParams().getFirstValue("callback");
			return new OutputWriterConvertor(new PropertyJSONReporter(getRequest(),jsonpcallback),MediaType.APPLICATION_JAVASCRIPT);
		} else { //json by default
		//else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			return new OutputWriterConvertor(new PropertyJSONReporter(getRequest()),MediaType.APPLICATION_JSON);
		}
	}	
	
}