package ambit2.rest.model;

import java.util.concurrent.Callable;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.AmbitApplication;
import ambit2.rest.StatusException;
import ambit2.rest.query.QueryResource;

/**
 * pka model
 * @author nina
 *
 */
public class ModelResource_pka<Q extends IQueryRetrieval<IStructureRecord>> extends QueryResource<Q,IStructureRecord>  {
	
	public ModelResource_pka(Context context, Request request, Response response) {
		super(context, request, response);
		this.getVariants().add(new Variant(MediaType.TEXT_PLAIN));	
	}

	@Override
	public boolean allowPost() {
		return true;
	}
	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws StatusException {

		return null;
	}
	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException {
		return null;
	}
	@Override
	public Representation getRepresentation(Variant variant)  {
		Callable<Reference> c = new Callable<Reference>() {
			public Reference call() throws Exception {
				long now = System.currentTimeMillis();
				for(int i=0; i < 10000;i++) {
					System.out.print(i);
				}
				return new Reference("new uri" + (System.currentTimeMillis()-now));
			}
		};

		synchronized (this) {
			Reference ref =  ((AmbitApplication)getApplication()).addTask(c,	getRequest().getRootRef());		
			return new 	StringRepresentation(
					ref.toString(),
					MediaType.TEXT_PLAIN);	
		}
	}
	/*
	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws AmbitException {
		SourceDataset dataset = new SourceDataset();
		dataset.setId(new Integer(Reference.decode(id.toString())));
		QueryDataset query = new QueryDataset();
		query.setValue(dataset);
		query.setMaxRecords(100);
	}
	@Override
	public void acceptRepresentation(Representation entity)
			throws ResourceException {
		Form requestHeaders = (Form) getRequest().getAttributes().get("org.restlet.http.headers");  
		String id = requestHeaders.getFirstValue("dataset-id");  
		System.out.println(id);
		//retrieve dataset; calculate value, assign to feature, return uri to feature
		//redirect 
		DatasetURIReporter reporter = new DatasetURIReporter(getRequest().getRootRef());
		SourceDataset dataset = new SourceDataset();
		dataset.setId(Integer.parseInt(id));
		getResponse().setLocationRef(new Reference(reporter.getURI(dataset)));
		getResponse().setStatus(Status.SUCCESS_CREATED);
		
		 try {
			 
		  if (entity.getMediaType().equals(MediaType.APPLICATION_WWW_FORM,true)) {
		   Form form = new Form(entity);
		  // User u = new User();
		   //u.setName(form.getFirstValue("user[name]"));
		   u.setName(form.getFirstValue("search"));
		   // :TODO {save the new user to the database}
		   getResponse().setStatus(Status.SUCCESS_OK);
		   // We are setting the representation in the example always to
		   // JSON.
		   // You could support multiple representation by using a
		   // parameter
		   // in the request like "?response_format=xml"
		  // Representation rep = new JsonRepresentation(u.toJSON());
		  // getResponse().setEntity(rep);
		  } else {
		   getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		  }
		 } catch (Exception e) {
		  getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
		 }
		 
	}	
	
	protected void process() throws AmbitException {
		final DescriptorsCalculator calculator = new DescriptorsCalculator();
		calculator.setDescriptors((Profile)o);		
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		p1.add(new ProcessorStructureRetrieval());		
		p1.add(calculator);
		
		DbReader<IStructureRecord> batch1 = new DbReader<IStructureRecord>();
		batch1.setProcessorChain(p1);
		
		QueryReporter batch = new QueryReporter() {
			@Override
			public void close() throws SQLException {
				// TODO Auto-generated method stub
				super.close();
			}
			@Override
			public void footer(Object output, IQueryRetrieval query) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void header(Object output, IQueryRetrieval query) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public long getID() {
				// TODO Auto-generated method stub
				return super.getID();
			
		}
		
	}
		*/
}
