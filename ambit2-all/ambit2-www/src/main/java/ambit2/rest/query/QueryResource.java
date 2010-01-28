package ambit2.rest.query;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorException;
import ambit2.base.processors.Reporter;
import ambit2.db.IDBProcessor;
import ambit2.db.UpdateExecutor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.db.update.AbstractUpdate;
import ambit2.rest.AbstractResource;
import ambit2.rest.AmbitApplication;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.rdf.RDFObjectIterator;
import ambit2.rest.task.CallableQueryProcessor;

/**
 * Abstract parent class for all resources , which retrieves something from the database
 * @author nina
 *
 * @param <Q>
 * @param <T>
 */
public abstract class QueryResource<Q extends IQueryRetrieval<T>,T extends Serializable>  extends AbstractResource<Q,T,IProcessor<Q,Representation>> {
	public final static String query_resource = "/query";
	


	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
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
					queryObject.setMaxRecords(Long.parseLong(form.getFirstValue(max_hits).toString()));
				} catch (Exception x) {
					
				}
	}	
	/*
	protected Connection getConnection() throws SQLException , AmbitException {
		Connection connection = ((AmbitApplication)getApplication()).getConnection(getRequest());
		if (connection.isClosed()) connection = ((AmbitApplication)getApplication()).getConnection(getRequest());
		return connection;
	}
	*/
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
		        		
		        		DBConnection dbc = new DBConnection(getContext());
		        		connection = dbc.getConnection(getRequest());
		        		Reporter reporter = ((RepresentationConvertor)convertor).getReporter();
			        	if (reporter instanceof IDBProcessor)
			        		((IDBProcessor)reporter).setConnection(connection);
			        	Representation r = convertor.process(queryObject);
			        	r.setCharacterSet(CharacterSet.UTF_8);
			        	return r;
		        	} catch (ResourceException x) {
		    			throw x;			        	
		        	} catch (NotFoundException x) {
		    			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,String.format("Query returns no results! %s",x.getMessage()));
		    			
		        	} catch (SQLException x) {
		        		Context.getCurrentLogger().severe(x.getMessage());
		        		if (retry <maxRetry) {
		        			retry++;
		        			getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE,x,String.format("Retry %d ",retry));
		        			continue;
		        		}
		        		else {
		        			throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE,x);
		        		}
		        	} catch (Exception x) {
		        		Context.getCurrentLogger().severe(x.getMessage());
		    			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
	
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
	    		} catch (Exception x) { 
	    			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x); 
	    		}  else {
	    			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,error);
	    		}
    	
	        }
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
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
    		DBConnection dbc = new DBConnection(getContext());
    		c = dbc.getConnection(getRequest());			

			executor.setConnection(c);
			executor.open();
			executor.process(updateObject);
			
			QueryURIReporter<T,Q> uriReporter = getURUReporter(getRequest());
			if (uriReporter!=null) {
				getResponse().setLocationRef(uriReporter.getURI(entry));
				getResponse().setEntity(uriReporter.getURI(entry),MediaType.TEXT_HTML);
			}
			getResponse().setStatus(Status.SUCCESS_OK);
			
		} catch (SQLException x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN,x,x.getMessage());			
			getResponse().setEntity(null);			
		} catch (ProcessorException x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus((x.getCause() instanceof SQLException)?Status.CLIENT_ERROR_FORBIDDEN:Status.SERVER_ERROR_INTERNAL,
					x,x.getMessage());			
			getResponse().setEntity(null);			
		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x,x.getMessage());			
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
		T entry = createObjectFromHeaders(null, entity);
		executeUpdate(entity, 
				entry,
				createUpdateObject(entry));
	
	}
	
	
	/**
	 * DELETE - create entity based on parameters in the query, creates a new entry in the database and returns an url to it
	 
	public void deleteObject(Representation entity) throws ResourceException {
		Form queryForm = getRequest().getResourceRef().getQueryAsForm();
		T entry = createObjectFromHeaders(queryForm, entity);
		executeUpdate(entity, 
				entry,
				createDeleteObject(entry));
	
	}	
	*/
	
	protected Representation delete(Variant variant) throws ResourceException {
		Representation entity = getRequestEntity();
		Form queryForm = null;
		if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType()))
			queryForm = new Form(entity);
		T entry = createObjectFromHeaders(queryForm, entity);
		executeUpdate(entity, 
				entry,
				createDeleteObject(entry));
		getResponse().setStatus(Status.SUCCESS_OK);
		return new EmptyRepresentation();
	};
	protected QueryURIReporter<T, Q>  getURUReporter(Request baseReference) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	protected  AbstractUpdate createUpdateObject(T entry) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	protected  AbstractUpdate createDeleteObject(T entry) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
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
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty content");
		} else 
			if (MediaType.TEXT_URI_LIST.equals(entity.getMediaType())) {
				return createObjectFromURIlist(entity);
			} else if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
				return createObjectFromWWWForm(entity);
			} else if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType())) {
				return createObjectFromMultiPartForm(entity);				
			
			} else // assume RDF
			try {
				iterator = createObjectIterator(entity);
				iterator.setCloseModel(true);
				iterator.setBaseReference(getRequest().getRootRef());
				
				while (iterator.hasNext()) {
					return iterator.next();
				}
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Nothing to write! "+getRequest().getRootRef() );	
			} catch (ResourceException x)  {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);	
			} finally {
				try { iterator.close(); } catch (Exception x) {}
			}
		
	}
	protected String getObjectURI(Form queryForm) throws ResourceException {
		return getParameter(queryForm,
				OpenTox.params.source_uri.toString(),
				OpenTox.params.source_uri.getDescription(),
				true);		
	}
	protected T createObjectFromWWWForm(Representation entity) throws ResourceException {
		Form queryForm = new Form(entity);
		String sourceURI = getObjectURI(queryForm);
		RDFObjectIterator<T> iterator = null;
		try {
			iterator = createObjectIterator(new Reference(sourceURI),entity.getMediaType()==null?MediaType.APPLICATION_RDF_XML:entity.getMediaType());
			iterator.setCloseModel(true);
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
	}
	protected T createObjectFromMultiPartForm(Representation entity) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
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
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		} finally {
			try { reader.close();} catch (Exception x) {}
		}
		
	}
	
	protected Representation process(Representation entity, Variant variant, final boolean async)
			throws ResourceException {
		synchronized (this) {
			final Form form = new Form(entity);
			final Reference reference = new Reference(getObjectURI(form));
			//models
			IQueryRetrieval<T> query = createQuery(getContext(),getRequest(),getResponse());
			if (query==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			
			
			Connection conn = null;
			
			QueryReporter<T,IQueryRetrieval<T>,Object> readModels = new QueryReporter<T,IQueryRetrieval<T>,Object>() {
				@Override
				public Object processItem(T model) throws AmbitException {
					try {
						Callable<Reference> task = createCallable(form,model);
						if (async) {
							Reference ref =  ((AmbitApplication)getApplication()).addTask(
									String.format("Apply %s to %s",model.toString(),reference),
									task,
									getRequest().getRootRef());		
							getResponse().setLocationRef(ref);
							//getResponse().setStatus(Status.SUCCESS_CREATED);
							getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
							getResponse().setEntity(null);
						} else {
							Reference ref = task.call();
							getResponse().setLocationRef(ref);
							getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
							getResponse().setEntity(null);
						}
					} catch (ResourceException x) {
						getResponse().setStatus( ((ResourceException)x.getCause()).getStatus());
					} catch (Exception x) {
						getResponse().setStatus(new Status(Status.SERVER_ERROR_INTERNAL,x.getMessage()));
					}
					return null;
					
				}
				public void open() throws DbAmbitException {};
				@Override
				public void header(Object output, IQueryRetrieval<T> query) {};
				@Override
				public void footer(Object output, IQueryRetrieval<T> query) {};
					
			};
			try {
				DBConnection dbc = new DBConnection(getApplication().getContext());
				conn = dbc.getConnection(getRequest());	
	    		readModels.setConnection(conn);
				readModels.process(query);		
				return getResponse().getEntity();
			} catch (AmbitException x) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
			} catch (SQLException x) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
			} finally {
				try { conn.close();} catch  (Exception x) {}
			}
		}
	}
	
	
	protected CallableQueryProcessor createCallable(Form form,T item) throws ResourceException  {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
}
