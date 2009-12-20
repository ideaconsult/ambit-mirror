package ambit2.rest.query;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import org.jmol.util.Logger;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.Reporter;
import ambit2.db.IDBProcessor;
import ambit2.db.UpdateExecutor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.AbstractUpdate;
import ambit2.db.update.property.ReadProperty;
import ambit2.rest.AbstractResource;
import ambit2.rest.AmbitApplication;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.rdf.RDFObjectIterator;

/**
 * Abstract parent class for all resources , which retrieves something from the database
 * @author nina
 *
 * @param <Q>
 * @param <T>
 */
public abstract class QueryResource<Q extends IQueryRetrieval<T>,T extends Serializable>  extends AbstractResource<Q,T,IProcessor<Q,Representation>> {
	public final static String query_resource = "/query";	
	/**
	 * Parameters, expected in URL query
	 * @author nina
	 *
	 */
	public enum headers  {
			source_uri {
			}; 
			public boolean isMandatory() {
				return true;
			}
			public String getDescription() {
				return "either use ?source_uri=URI, or POST with text/uri-list or RDF representation of the object to be created";
			}
	};
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {MediaType.TEXT_HTML,
				MediaType.TEXT_XML,
				MediaType.TEXT_URI_LIST,
				MediaType.TEXT_PLAIN,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JAVA_OBJECT
		});		
		if (queryObject!=null)
				try {
					Form form = getRequest().getResourceRef().getQueryAsForm();
					queryObject.setMaxRecords(Long.parseLong(form.getFirstValue("max").toString()));
				} catch (Exception x) {
					
				}
	}	
	protected Connection getConnection() throws SQLException , AmbitException {
		Connection connection = ((AmbitApplication)getApplication()).getConnection(getRequest());
		if (connection.isClosed()) connection = ((AmbitApplication)getApplication()).getConnection(getRequest());
		return connection;
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		try {
			int maxRetry=3;
        	if (MediaType.APPLICATION_JAVA_OBJECT.equals(variant.getMediaType())) {
        		if ((queryObject!=null) && (queryObject instanceof Serializable))
        		return new ObjectRepresentation((Serializable)queryObject,MediaType.APPLICATION_JAVA_OBJECT);
        		else throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE);        		
        	}				
	        if (queryObject != null) {
        	
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
			        	Representation r = convertor.process(queryObject);
			        	r.setCharacterSet(CharacterSet.UTF_8);
			        	return r;
		        	} catch (ResourceException x) {
		    			getResponse().setStatus(x.getStatus());
		    			return null;			        	
		        	} catch (NotFoundException x) {
		    			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND,String.format("Query returns no results! %s",x.getMessage()));
		    			return null;
		    			
		        	} catch (SQLException x) {
		        		java.util.logging.Logger.getLogger(getClass().getName()).severe(x.getMessage());
		        		if (retry <maxRetry) {
		        			retry++;
		        			getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE,x,String.format("Retry %d ",retry));
		        			continue;
		        		}
		        		else {
			    			getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE,x);
			    			return null;
		        		}
		        	} catch (Exception x) {
		        		java.util.logging.Logger.getLogger(getClass().getName()).severe(x.getMessage());
		    			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
		    			return null;	        		
		        	} finally {
		        		//try { if (connection !=null) connection.close(); } catch (Exception x) {};
		        		//try { if ((convertor !=null) && (convertor.getReporter() !=null)) convertor.getReporter().close(); } catch (Exception x) {}
		        	}
	        	}
    			return null;	        	
	        	
	        } else {
	        	if (variant.getMediaType().equals(MediaType.TEXT_HTML)) try {
	    			IProcessor<Q, Representation>  convertor = createConvertor(variant);
	    			Representation r = convertor.process(null);
	            	return r;			
	    		} catch (Exception x) { getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,x); return null;}	        	
	    		else {
	    			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,error);
	    			return null;
	    		}
    	
	        }
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
			return null;	
		}
	}		
	
	/**
	 * POST - create entity based on parameters in http header, creates a new entry in the databaseand returns an url to it
	 */
	public void executeUpdate(Representation entity, T entry, AbstractUpdate updateObject) throws ResourceException {

		Connection c = null;
		//TODO it is inefficient to instantiate executor in all classes
		UpdateExecutor executor = new UpdateExecutor();
		try {

			executor.setConnection(getConnection());
			executor.open();
			executor.process(updateObject);
			
			QueryURIReporter<T,Q> uriReporter = getURUReporter(getRequest());
			getResponse().setLocationRef(uriReporter.getURI(entry));
			getResponse().setStatus(Status.SUCCESS_OK);
			getResponse().setEntity(uriReporter.getURI(entry),MediaType.TEXT_HTML);
			
		} catch (Exception x) {
			java.util.logging.Logger.getLogger(getClass().getName()).severe(x.toString());
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);			
			getResponse().setEntity(null);
		} finally {
			try {executor.close();} catch (Exception x) {}
			try {if(c != null) c.close();} catch (Exception x) {}
		}
	}	
	/**
	 * POST - create entity based on parameters in the query, creates a new entry in the databaseand returns an url to it
	 * TODO Refactor to allow multiple objects 
	 */
	public void createNewObject(Representation entity) throws ResourceException {
		Form queryForm = getRequest().getResourceRef().getQueryAsForm();
		T entry = createObjectFromHeaders(queryForm, entity);
		executeUpdate(entity, 
				entry,
				createUpdateObject(entry));
	
	}
	/**
	 * DELETE - create entity based on parameters in the query, creates a new entry in the database and returns an url to it
	 */
	public void deleteObject(Representation entity) throws ResourceException {
		Form queryForm = getRequest().getResourceRef().getQueryAsForm();
		T entry = createObjectFromHeaders(queryForm, entity);
		executeUpdate(entity, 
				entry,
				createDeleteObject(entry));
	
	}	
	protected QueryURIReporter<T, Q>  getURUReporter(Request baseReference) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
	}
	protected  AbstractUpdate createUpdateObject(T entry) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
	}
	protected  AbstractUpdate createDeleteObject(T entry) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
	}	
	protected RDFObjectIterator<T> createObjectIterator(Reference reference, MediaType mediaType) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
	}
	
	protected RDFObjectIterator<T> createObjectIterator(Representation entity) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
	}
	/**
	 * Return this object if can't parse source_uri
	 * @param uri
	 * @return
	 */
	protected T onError(String uri) {
		return null;
	}
	/**
	 * either entity in RDF/XML or ?source_uri=URI
	 */
	protected T createObjectFromHeaders(Form queryForm, Representation entity) throws ResourceException {
		RDFObjectIterator<T> iterator = null;
		
		if (!entity.isAvailable()) { //using URI
			String sourceURI = getParameter(queryForm,headers.source_uri.toString(),headers.source_uri.getDescription(),headers.source_uri.isMandatory());
			try {
				iterator = createObjectIterator(new Reference(sourceURI),entity.getMediaType()==null?MediaType.APPLICATION_RDF_XML:entity.getMediaType());
				iterator.setBaseReference(getRequest().getRootRef());
				while (iterator.hasNext()) {
					return iterator.next();
				}		
				//if none
				return onError(sourceURI);
			} catch (Exception x) {
				return onError(sourceURI);
			} finally {
				try { iterator.close(); } catch (Exception x) {}
			}
		} else 
			if (MediaType.TEXT_URI_LIST.equals(entity.getMediaType())) {
				return createObjectFromURIlist(entity);
			} else // assume RDF
			try {
				iterator = createObjectIterator(entity);
				iterator.setBaseReference(getRequest().getRootRef());
				
				while (iterator.hasNext()) {
					return iterator.next();
				}
				System.out.println(entity.getMediaType());
				System.out.println(entity.toString());
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Nothing to write! "+getRequest().getRootRef() );	
			} catch (ResourceException x)  {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);	
			} finally {
				try { iterator.close(); } catch (Exception x) {}
			}
		
	}
	
	protected T createObjectFromURIlist(Representation entity) throws ResourceException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(entity.getStream()));
			String line = null;
			while ((line = reader.readLine())!= null)
				return onError(line);
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		} finally {
			try { reader.close();} catch (Exception x) {}
		}
		
	}
	
}
