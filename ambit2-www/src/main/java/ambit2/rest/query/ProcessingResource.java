package ambit2.rest.query;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.AmbitApplication;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.task.CallableQueryProcessor;

/**
 * Parent class for resources generating async tasks on POST
 * @author nina
 *
 * @param <Q>
 * @param <T>
 */
public abstract class ProcessingResource<Q extends IQueryRetrieval<T>,T extends Serializable> extends QueryResource<Q, T> {

	protected abstract CallableQueryProcessor createCallable(Form form,T item);
	
	protected Reference getSourceReference(Form form) {

		Object datasetURI = OpenTox.params.dataset_uri.getFirstValue(form);
		if (datasetURI==null) throw new ResourceException(
				Status.CLIENT_ERROR_BAD_REQUEST,String.format("Empty %s [%s]", OpenTox.params.dataset_uri.toString(), OpenTox.params.dataset_uri.getDescription()));
		return new Reference(Reference.decode(datasetURI.toString()));
	}
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		synchronized (this) {
			final Form form = new Form(entity);
			final Reference reference = getSourceReference(form);
			//models
			IQueryRetrieval<T> query = createQuery(getContext(),getRequest(),getResponse());
			if (query==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			
			
			Connection conn = null;
			
			QueryReporter<T,IQueryRetrieval<T>,Object> readModels = new QueryReporter<T,IQueryRetrieval<T>,Object>() {
				@Override
				public Object processItem(T model) throws AmbitException {
					try {
						Reference ref =  ((AmbitApplication)getApplication()).addTask(
								String.format("Apply %s to %s",model.toString(),reference),
								createCallable(form,model),
								getRequest().getRootRef());		
						getResponse().setLocationRef(ref);
						//getResponse().setStatus(Status.SUCCESS_CREATED);
						getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
						getResponse().setEntity(null);
					} catch (Exception x) {
						if (x.getCause() instanceof ResourceException)
							getResponse().setStatus( ((ResourceException)x.getCause()).getStatus());
						else
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

}
