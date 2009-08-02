package ambit2.rest;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IProcessor;

public abstract class AbstractResource<Q,T,P extends IProcessor<Q, Representation>> extends Resource {
	protected Q query;
	protected Exception error = null;	
	
	public String[] URI_to_handle() {
		return null;
	}
	public AbstractResource(Context context, Request request, Response response) {
		super(context,request,response);
	}
	public abstract P createConvertor(Variant variant) throws AmbitException;
	
	public Representation getRepresentation(Variant variant) {

		try {
	        if (query != null) {
	        	IProcessor<Q, Representation> convertor = null;

		        	try {
		        		convertor = createConvertor(variant);
			        	Representation r = convertor.process(query);
			        	return r;
		        	} catch (NotFoundException x) {

		    			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND, new NotFoundException(x.getMessage()));
		    			return null;
		    			
		        	} catch (Exception x) {

		    			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
		    			return null;
		        	} finally {
		        		
		        	}

	        	
	        } else {
	        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,error);
	        	return null;	        	
	        }
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
			return null;
		}
	}				
}
