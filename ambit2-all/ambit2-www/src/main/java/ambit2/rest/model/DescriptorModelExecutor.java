package ambit2.rest.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.restlet.data.Reference;
import org.restlet.data.Status;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.DbReader;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.DescriptorsCalculator;
import ambit2.db.processors.ProcessorMissingDescriptorsQuery;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.StatusException;
import ambit2.rest.task.TaskResource;

/**
 * Executes a model and returns {@link Reference} for a {@link TaskResource}
 * Assumes the model content is a IMolecularDescriptor class
 * @author nina
 *
 */
public class DescriptorModelExecutor extends AbstractDBProcessor<ModelQueryResults, Reference> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4602261055183051841L;

	public Reference process(ModelQueryResults model)
			throws AmbitException {
		try {
			Profile<Property> p = new Profile<Property>();
			Property property = DescriptorsFactory.createDescriptor2Property(model.getContent());
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
			
    		batch.setConnection(getConnection());
    		
			batch.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					System.out.println(evt.getNewValue());
					
				}
			});
			
    		ProcessorMissingDescriptorsQuery mq = new ProcessorMissingDescriptorsQuery();
    		QueryCombinedStructure q = (QueryCombinedStructure)mq.process(p);
    		q.setScope(model.getTestInstances());
    		q.setId(1);
    		IBatchStatistics stats = batch.process(q);
    		System.out.println(stats);
    		for(int i=0; i < 10000000; i++) System.out.print(i);
			return new Reference(String.format("/template/%s",model.getDependent().getName()));
		} catch (Exception x) {
			x.printStackTrace();
			throw new StatusException(new Status(Status.SERVER_ERROR_INTERNAL,x.getMessage()));
		} finally {
			
		}
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
