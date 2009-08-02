package ambit2.rest.query;

import java.sql.Connection;
import java.sql.SQLException;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.Reporter;
import ambit2.db.IDBProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.AbstractResource;
import ambit2.rest.AmbitApplication;
import ambit2.rest.RepresentationConvertor;


public abstract class QueryResource<Q extends IQueryRetrieval<T>,T>  extends AbstractResource<Q,T,IProcessor<Q,Representation>> {
	public final static String query_resource = "/query";	
	protected Q query;
	
	
	public QueryResource(Context context, Request request, Response response) {
		super(context,request,response);
		try {
			query = createQuery(context, request, response);
			error = null;
		} catch (AmbitException x) {
			query = null;
			error = x;
		}
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));		
	}
	protected  abstract Q createQuery(Context context, Request request, Response response) throws AmbitException;

	public Representation getRepresentation(Variant variant) {

		try {
	        if (query != null) {
	        	IProcessor<Q, Representation>  convertor = null;
	        	Connection connection = null;
	        	int retry=0;
	        	while (retry <2) {
		        	try {
		        		convertor = createConvertor(variant);
		        		connection = ((AmbitApplication)getApplication()).getConnection();
		        		if (connection.isClosed()) connection = ((AmbitApplication)getApplication()).getConnection();
			        	Reporter reporter = ((RepresentationConvertor)convertor).getReporter();
			        	if (reporter instanceof IDBProcessor)
			        		((IDBProcessor)reporter).setConnection(connection);
			        	Representation r = convertor.process(query);
			        	return r;
		        	} catch (NotFoundException x) {
		        		//x.printStackTrace();
		    			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND,String.format("Query returns no results! %s",x.getMessage()));
		    			return null;
		        	} catch (SQLException x) {
		        		x.printStackTrace();
		        		if (retry <2) {
		        			retry++;
		        			continue;
		        		}
		        		else {
			    			getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE,x);
			    			return null;
		        		}
		        	} catch (Exception x) {
		        		x.printStackTrace();
		    			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
		    			return null;	        		
		        	} finally {
		        		//try { if (connection !=null) connection.close(); } catch (Exception x) {};
		        		//try { if ((convertor !=null) && (convertor.getReporter() !=null)) convertor.getReporter().close(); } catch (Exception x) {}
		        	}
	        	}
    			return new StringRepresentation("<error>Error</error>",
    					variant.getMediaType());	
	        	
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
