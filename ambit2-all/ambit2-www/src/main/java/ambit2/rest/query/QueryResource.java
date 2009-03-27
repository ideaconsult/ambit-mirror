package ambit2.rest.query;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
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
	        	int retry=0;
	        	while (retry <2) {
		        	try {
		        		convertor = createConvertor(variant);
		        		connection = ((AmbitApplication)getApplication()).getConnection();
		        		if (connection.isClosed()) connection = ((AmbitApplication)getApplication()).getConnection();
			        	convertor.getReporter().setConnection(connection);
			        	Representation r = convertor.process(query);
			        	return r;
		        	} catch (NotFoundException x) {
		        		x.printStackTrace();
		    			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		    			return new StringRepresentation("<error>Query returns no results! "+x.getMessage()+"</error>",
		    					variant.getMediaType());	
		    			
		        	} catch (SQLException x) {
		        		x.printStackTrace();
		        		if (retry <2) {
		        			retry++;
		        			continue;
		        		}
		        		else {
			    			getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
			    			return new StringRepresentation("<error>Error connecting to database "+x.getMessage()+"</error>",
			    					variant.getMediaType());
		        		}
		        	} catch (Exception x) {
		        		x.printStackTrace();
		    			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
		    			return new StringRepresentation("<error>there was an error retrieving the data "+x.getMessage()+"</error>",
		    					variant.getMediaType());		        		
		        	} finally {
		        		//try { if (connection !=null) connection.close(); } catch (Exception x) {};
		        		//try { if ((convertor !=null) && (convertor.getReporter() !=null)) convertor.getReporter().close(); } catch (Exception x) {}
		        	}
	        	}
    			return new StringRepresentation("<error>Error</error>",
    					variant.getMediaType());	
	        	
	        } else {
	        	getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        	return new StringRepresentation(error.getMessage(),variant.getMediaType());	        	
	        }
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation("there was an error retrieving the data "+x.getMessage(),
					variant.getMediaType());			
		}
	}		
		

}
