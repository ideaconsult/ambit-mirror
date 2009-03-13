package ambit2.rest.query;

import java.sql.Connection;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import ambit2.core.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.AmbitApplication;
import ambit2.rest.RepresentationConvertor;

public abstract class QueryResource<Q extends IQueryRetrieval<T>,T>  extends Resource {
	protected Q query;
	protected AmbitException error = null;	
	
	public QueryResource(Context context, Request request, Response response) {
		super(context,request,response);
		try {
			query = createQuery(context, request, response);
			error = null;
		} catch (AmbitException x) {
			query = null;
			error = x;
		}
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));		
	}
	protected  abstract Q createQuery(Context context, Request request, Response response) throws AmbitException;
	public abstract RepresentationConvertor createConvertor(Variant variant) throws AmbitException;

	public Representation getRepresentation(Variant variant) {
		System.out.println(variant.getMediaType());
		try {
	        if (query != null) {
	        	RepresentationConvertor convertor = null;
	        	Connection connection = null;
	        	try {
	        		convertor = createConvertor(variant);
	        		connection = ((AmbitApplication)getApplication()).getConnection();
		        	convertor.getReporter().setConnection(connection);
		        	Representation r = convertor.process(query);
		        	return r;
	        	} catch (Exception x) {
	    			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
	    			return new StringRepresentation("there was an error retrieving the data "+x.getMessage(),
	    			MediaType.TEXT_PLAIN);		        		
	        	} finally {
	        		//try { if (connection !=null) connection.close(); } catch (Exception x) {};
	        		//try { if ((convertor !=null) && (convertor.getReporter() !=null)) convertor.getReporter().close(); } catch (Exception x) {}
	        	}
	        		
	        	
	        } else {
	        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        	return new StringRepresentation(error.getMessage());	        	
	        }
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation("there was an error retrieving the data "+x.getMessage(),
			MediaType.TEXT_PLAIN);			
		}
	}		
		

}
