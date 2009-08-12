package ambit2.rest.query;

import java.sql.Connection;
import java.sql.SQLException;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.Reporter;
import ambit2.db.IDBProcessor;
import ambit2.db.UpdateExecutor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.AbstractObjectUpdate;
import ambit2.rest.AbstractResource;
import ambit2.rest.AmbitApplication;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StatusException;


public abstract class QueryResource<Q extends IQueryRetrieval<T>,T>  extends AbstractResource<Q,T,IProcessor<Q,Representation>> {
	public final static String query_resource = "/query";	
	
	
	public QueryResource(Context context, Request request, Response response) {
		super(context,request,response);

		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));		
	}
	protected Connection getConnection() throws SQLException , AmbitException {
		Connection connection = ((AmbitApplication)getApplication()).getConnection();
		if (connection.isClosed()) connection = ((AmbitApplication)getApplication()).getConnection();
		return connection;
	}
	public Representation getRepresentation(Variant variant) {

		try {
			int maxRetry=3;
	        if (query != null) {
	        	IProcessor<Q, Representation>  convertor = null;
	        	Connection connection = null;
	        	int retry=0;
	        	while (retry <maxRetry) {
		        	try {
		        		convertor = createConvertor(variant);
		        		connection = getConnection();
		        		Reporter reporter = ((RepresentationConvertor)convertor).getReporter();
			        	if (reporter instanceof IDBProcessor)
			        		((IDBProcessor)reporter).setConnection(connection);
			        	Representation r = convertor.process(query);
			        	return r;
		        	} catch (StatusException x) {
		    			getResponse().setStatus(x.getStatus());
		    			return null;			        	
		        	} catch (NotFoundException x) {
		        		//x.printStackTrace();
		    			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND,String.format("Query returns no results! %s",x.getMessage()));
		    			return null;
		        	} catch (SQLException x) {
		        		x.printStackTrace();
		        		if (retry <maxRetry) {
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
	
	/**
	 * POST - create entity based on parameters in http header, creates a new entry in the databaseand returns an url to it
	 */
	public void createNewObject(Representation entity) throws ResourceException {
		Form requestHeaders = (Form) getRequest().getAttributes().get("org.restlet.http.headers");  
		T entry = createObjectFromHeaders(requestHeaders);
		Connection c = null;
		//TODO it is inefficient to instantiate executor in all classes
		UpdateExecutor executor = new UpdateExecutor();
		try {

			executor.setConnection(getConnection());
			executor.open();
			executor.process(createUpdateObject(entry));
			QueryURIReporter<T,Q> uriReporter = getURUReporter(getRequest().getRootRef());
			getResponse().setLocationRef(uriReporter.getURI(entry));
			getResponse().setStatus(Status.SUCCESS_OK);
			getResponse().setEntity(null);
		} catch (Exception x) {
			x.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);			
			getResponse().setEntity(null);
		} finally {
			try {executor.close();} catch (Exception x) {}
			try {if(c != null) c.close();} catch (Exception x) {}
		}
	}
	protected QueryURIReporter<T, Q>  getURUReporter(Reference baseReference) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
	}
	protected  AbstractObjectUpdate<T> createUpdateObject(T entry) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
	}
	
	protected T createObjectFromHeaders(Form requestHeaders) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
	}
	
}
