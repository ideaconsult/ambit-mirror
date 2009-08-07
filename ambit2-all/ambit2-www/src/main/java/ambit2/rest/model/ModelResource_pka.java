package ambit2.rest.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.util.concurrent.Callable;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.DbReader;
import ambit2.db.SourceDataset;
import ambit2.db.processors.DescriptorsCalculator;
import ambit2.db.processors.ProcessorMissingDescriptorsQuery;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.AmbitApplication;
import ambit2.rest.StatusException;
import ambit2.rest.dataset.DatasetURIReporter;
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
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException {
		return null;
	}

	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws StatusException {
		return null;
	}
	@Override
	public boolean allowGet() {
		return false;
	}
	
	@Override
	public void acceptRepresentation(Representation entity)
			throws ResourceException {
		//retrieve dataset; calculate value, assign to feature, return uri to feature

		synchronized (this) {
			try {
				Form requestHeaders = (Form) getRequest().getAttributes().get("org.restlet.http.headers");  
				String id = requestHeaders.getFirstValue("dataset-id");  				
				QueryDatasetByID q = new QueryDatasetByID();
				q.setValue(Integer.parseInt(id));				
				Reference ref =  ((AmbitApplication)getApplication()).addTask(process(q),	getRequest().getRootRef());		
				getResponse().setLocationRef(ref);
				getResponse().setStatus(Status.SUCCESS_CREATED);
			} catch (AmbitException x) {
				getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
				
			}
		}
	}	
	
	protected Callable<Reference> process(final QueryDatasetByID query) throws AmbitException {
		Callable<Reference> callable = new Callable<Reference>() {
			
			public Reference call() throws Exception {

				Connection connection = null;
				try {
					Profile<Property> p = new Profile<Property>();
					Property property = DescriptorsFactory.createDescriptor2Property("ambit2.descriptors.PKASmartsDescriptor");
					property.setEnabled(true);
					p.add(property);
					
					DescriptorsCalculator calculator = new DescriptorsCalculator();
					calculator.setDescriptors(p);		
					ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
						new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
					p1.add(new ProcessorStructureRetrieval());		
					p1.add(calculator);
					p1.setAbortOnError(true);
					
					DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>();
					batch.setProcessorChain(p1);
					
	        		connection = ((AmbitApplication)getApplication()).getConnection();
	        		if (connection.isClosed()) connection = ((AmbitApplication)getApplication()).getConnection();
	        		batch.setConnection(connection);
	        		
					batch.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent evt) {
							System.out.println(evt.getNewValue());
							
						}
					});
					
	        		ProcessorMissingDescriptorsQuery mq = new ProcessorMissingDescriptorsQuery();
	        		QueryCombinedStructure q = (QueryCombinedStructure)mq.process(p);
	        		q.setScope(query);
	        		q.setId(1);
	        		IBatchStatistics stats = batch.process(q);
				
					SourceDataset dataset = new SourceDataset();
					dataset.setId(query.getValue());					
					DatasetURIReporter reporter = new DatasetURIReporter(getRequest().getRootRef());
					return new Reference("/dataset/"+query.getId() + " " + stats);
				} catch (Exception x) {
					x.printStackTrace();
					getLogger().severe(x.getMessage());
					return new Reference(x.getMessage());
				} finally {
					try { connection.close(); } catch (Exception x) {}
				}
			}
		};
		return callable;
	}
		
}
