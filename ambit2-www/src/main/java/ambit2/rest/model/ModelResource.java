package ambit2.rest.model;

import java.sql.Connection;
import java.sql.SQLException;

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
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.db.update.model.ReadModel;
import ambit2.rest.AmbitApplication;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;
import ambit2.rest.task.CallableModelPredictor;

/**
 * Model as in http://opentox.org/development/wiki/Model
 * Supported REST operation:
 * GET 	 /model<br>
 * GET 	 /model/{id}
 * @author nina
 *
 */
public class ModelResource extends QueryResource<IQueryRetrieval<ModelQueryResults>, ModelQueryResults> {
	public final static String dataset_uri = "dataset_uri";
	
	public final static String resource = OpenTox.URI.model.getURI();
	public final static String resourceKey =  OpenTox.URI.model.getKey();
	public final static String resourceID = OpenTox.URI.model.getResourceID();
	protected boolean collapsed = true;
	
	public enum modeltypes  {
		pka,toxtree
	};
	
	protected String category = "";

	protected Object getModelID(Object id) throws ResourceException {
		
		if (id != null) try {
			id = Reference.decode(id.toString());
			collapsed = false;
			return new Integer(id.toString());
			
		} catch (NumberFormatException x) {
			return id;
		} catch (Exception x) {
			return null;
		} else return null;

	}
	@Override
	protected IQueryRetrieval<ModelQueryResults> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		ReadModel query = getModelQuery(getModelID(getRequest().getAttributes().get(resourceKey)));
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String name = form.getFirstValue(QueryResource.search_param);
		if (name!=null) query.setFieldname(name);
		collapsed = query.getValue()!=null;
		return query;
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
/*
	if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
		return new DocumentConvertor(new ModelXMLReporter(getRequest().getRootRef()));	
	} else 
	*/
	if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
		return new OutputWriterConvertor(
				new ModelHTMLReporter(getRequest(),collapsed),MediaType.TEXT_HTML);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		return new StringConvertor(	new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()) {
			@Override
			public Object processItem(ModelQueryResults dataset) throws AmbitException  {
				super.processItem(dataset);
				try {
				output.write('\n');
				} catch (Exception x) {}
				return null;
			}
		},MediaType.TEXT_URI_LIST);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
			variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
			variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
			) {
		return new RDFJenaConvertor<ModelQueryResults,IQueryRetrieval<ModelQueryResults>>(
				new ModelRDFReporter<IQueryRetrieval<ModelQueryResults>>(getRequest(),variant.getMediaType())
				,variant.getMediaType());			
	} else //html 	
		return new OutputWriterConvertor(
				new ModelHTMLReporter(getRequest(),collapsed),MediaType.TEXT_HTML);
	}
	
	@Override
	protected QueryURIReporter<ModelQueryResults, IQueryRetrieval<ModelQueryResults>> getURUReporter(
			Request baseReference) throws ResourceException {
		return new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest());
	}
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		synchronized (this) {
			Form form = getRequest().getResourceRef().getQueryAsForm();
			Object datasetURI = form.getFirstValue(dataset_uri);
			if (datasetURI==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Empty %s", dataset_uri));
			final Reference reference = new Reference(Reference.decode(datasetURI.toString()));
			
			//models
			IQueryRetrieval<ModelQueryResults> query = createQuery(getContext(),getRequest(),getResponse());
			if (query==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			
			
			Connection conn = null;
			
			QueryReporter<ModelQueryResults,IQueryRetrieval<ModelQueryResults>,Object> readModels = new QueryReporter<ModelQueryResults,IQueryRetrieval<ModelQueryResults>,Object>() {
				@Override
				public Object processItem(ModelQueryResults model) throws AmbitException {
					try {
						Reference ref =  ((AmbitApplication)getApplication()).addTask(
								String.format("Apply model %s to %s",model.toString(),reference),
								new CallableModelPredictor(
										reference,
										getRequest().getRootRef(),
										(AmbitApplication)getApplication(),
										model,
										new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest())),	
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
				public void header(Object output, IQueryRetrieval<ModelQueryResults> query) {};
				@Override
				public void footer(Object output, IQueryRetrieval<ModelQueryResults> query) {};
					
			};
			try {
	    		conn = ((AmbitApplication)getApplication()).getConnection(getRequest());
	    		if (conn.isClosed()) conn = ((AmbitApplication)getApplication()).getConnection(getRequest());
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
	/*
	@Override
	protected Representation post(Representation entity)
			throws ResourceException {

		//retrieve dataset; calculate value, assign to feature, return uri to feature

		synchronized (this) {
			Connection conn = null;
			try {
				final String resultRef ;
				final IQueryRetrieval<IStructureRecord> query;
				Object idmodel = getModelID(getRequest().getAttributes().get(resourceKey));
				final ModelQueryResults themodel = new ModelQueryResults();
				if ((entity != null) && MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType(),true)) {
					Form form = new Form(entity);
					if (idmodel == null) idmodel = getModelID(form.getFirstValue("idmodel"));
					
					if (idmodel == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
					
					themodel.setQueryID(idmodel);
					
					IStructureRecord record = new StructureRecord(
							Integer.parseInt(form.getFirstValue("idchemical")),
							Integer.parseInt(form.getFirstValue("idstructure")),null,null);
					query = new QueryStructureByID(record);
					CompoundURIReporter<IQueryRetrieval<IStructureRecord>> r = 
							new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(getRequest());
					resultRef = String.format("%s/model/%s",r.getURI(record),idmodel==null?"":idmodel);
				} else {
					if (idmodel == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
					themodel.setQueryID(idmodel);
					Form requestHeaders = (Form) getRequest().getAttributes().get("org.restlet.http.headers");  
					String id = requestHeaders.getFirstValue("dataset-id");  				
					query = new QueryDatasetByID();
					((QueryDatasetByID)query).setValue(Integer.parseInt(id));						
					resultRef = getURUReporter(getRequest()).getURI(themodel);
				}
				
				QueryReporter<ModelQueryResults,ReadModel,Object> r = new QueryReporter<ModelQueryResults,ReadModel,Object>() {
					@Override
					public void processItem(ModelQueryResults model) throws AmbitException {
						try {
							themodel.setId(model.getId());
							model.setTestInstances(query);
							Reference ref =  ((AmbitApplication)getApplication()).addTask(
									String.format("Apply model %s to %s",model.toString(),query.toString()),
									runModel(model),	
									getRequest().getRootRef());		
							getResponse().setLocationRef(ref);
							//getResponse().setStatus(Status.SUCCESS_CREATED);
							getResponse().setStatus(Status.REDIRECTION_SEE_OTHER);
							getResponse().setEntity(null);
						} catch (AmbitException x) {
							if (x.getCause() instanceof ResourceException)
								getResponse().setStatus( ((ResourceException)x.getCause()).getStatus());
							else
								getResponse().setStatus(new Status(Status.SERVER_ERROR_INTERNAL,x.getMessage()));
						}
						
					}
					public void open() throws DbAmbitException {};
					@Override
					public void header(Object output, ReadModel query) {};
					@Override
					public void footer(Object output, ReadModel query) {};
					protected Callable<Reference> runModel(final ModelQueryResults model) throws AmbitException {
						Callable<Reference> callable = new Callable<Reference>() {
							
							public Reference call() throws Exception {

								Connection connection = null;
								try {
									DescriptorModelExecutor modelExecutor = new DescriptorModelExecutor();
									modelExecutor.setReference(new Reference(resultRef));
					        		connection = ((AmbitApplication)getApplication()).getConnection(getRequest());
					        		if (connection.isClosed()) connection = ((AmbitApplication)getApplication()).getConnection(getRequest());
					        		modelExecutor.setConnection(connection);
					        		return modelExecutor.process(model);
								} catch (AmbitException x) {
									throw x;
								} catch (Exception x) {
									throw new AmbitException(x);
								} finally {
									try { connection.close(); } catch (Exception x) {}
								}
							}
						};
						return callable;
					}						
				};
				
        		conn = ((AmbitApplication)getApplication()).getConnection(getRequest());
        		if (conn.isClosed()) conn = ((AmbitApplication)getApplication()).getConnection(getRequest());
        		r.setConnection(conn);
				r.process(getModelQuery(idmodel));

	
			} catch (Exception x) {
				throw new ResourceException(new Status(Status.SERVER_ERROR_INTERNAL,x.getMessage()));
			} finally {
				try { conn.close(); } catch (Exception x) {}
			}
		}
		return getResponse().getEntity();
	}	
	*/
	protected ReadModel getModelQuery(Object idmodel) throws ResourceException {
		if (idmodel == null) return new ReadModel();
		else if (idmodel instanceof Integer)
			return new ReadModel((Integer)idmodel);
		else {
			ReadModel query = new ReadModel(null);
			query.setFieldname(idmodel.toString());
			return query;
		}
	}
	
}

