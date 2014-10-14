package ambit2.i5rest.resource;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.types.Types.QueryParameter;
import org.ideaconsult.iuclidws.types.Types.QueryParameterChoice_type0;
import org.ideaconsult.iuclidws.types.Types.QueryParameterList;
import org.ideaconsult.iuclidws.types.Types.TextParameterValue;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.i5rest.convertors.I5EngineReporter;
import ambit2.i5rest.convertors.I5OutputStreamConvertor;
import ambit2.rest.StringConvertor;

public class SubstanceResource<T extends Serializable> extends I5Resource<QueryParameterList, T> {

	private final static Logger LOGGER = Logger.getLogger(I5OutputStreamConvertor.class);
	public static final String resourceKey = "key";
	public static final String queryParamName = "queryParamName";
	public static final String resource = "/compound";
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML
		});		

	}
	public static String getKey(Request request) {
		Form form = request.getResourceRef().getQueryAsForm();
		return form.getFirstValue("search");
	}
	public static String getServiceURI(Request request) {
		Form form = request.getResourceRef().getQueryAsForm();
		return form.getFirstValue("target");
	}
	public static String getUser(Request request) {
		Form form = request.getResourceRef().getQueryAsForm();
		return form.getFirstValue("user");
	}	
	public static String getPass(Request request) {
		Form form = request.getResourceRef().getQueryAsForm();
		return form.getFirstValue("pass");
	}		
	@Override
	protected QueryParameterList createQuery(Context context, Request request, Response response)
			throws ResourceException {
		try {
			Form form = getRequest().getResourceRef().getQueryAsForm();
			Object key = request.getAttributes().get(resourceKey);
			if (key==null) key= getKey(request);
			if (key==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			
            TextParameterValue t = new TextParameterValue();
            t.setValue(Reference.decode(key.toString()));
            QueryParameterChoice_type0 v = new QueryParameterChoice_type0();
            v.setTextParameterValue(t);
            
			
			String qparam = form.getFirstValue(queryParamName);	            
            if (qparam==null) qparam="ecinventory_cas";
			
            QueryParameter param = new QueryParameter();
            param.setQueryParameterChoice_type0(v);
            param.setQueryParameterName(qparam);
            QueryParameterList params = new QueryParameterList();
            params.addQueryParameter(param);
            
			
			return params;
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		} finally {
			LOGGER.info("test looking for all query definition - end\n");
		}
	}
	@Override
	public IProcessor<QueryParameterList, Representation> createConvertor(Variant variant) throws AmbitException,
			ResourceException {
		return new StringConvertor(new I5EngineReporter(getRequest(),getSession()),MediaType.APPLICATION_XML);
	}

}
