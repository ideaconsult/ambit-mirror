package ambit2.rest.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
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
import ambit2.db.search.property.ValuesReader;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.task.TaskResource;
import ambit2.rest.template.OntologyResource;

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
	protected Reference reference = null;
	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

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
			//p1.add(new ProcessorStructureRetrieval());	
			
			ValuesReader readProfile = new ValuesReader();
			Template template = new Template();
			template.setName("DailyIntake");
			Property di = new Property("DailyIntake");
			di.setEnabled(true);
			template.add(di); // this is a hack for TTC application, TODO make it generic!!!
			readProfile.setProfile(template);
			
			p1.add(readProfile);
			
			//p1.add(new ValuesByTemplateReader());todo read predictor values
			p1.add(calculator);
			p1.setAbortOnError(true);
			
			DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>();
			batch.setProcessorChain(p1);
			
    		batch.setConnection(getConnection());
    		
			batch.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					//System.out.println(evt.getNewValue());
					
				}
			});
			
			//TODO this is to reflect values entered by user which will affect calculation result. Think of more efficient way.
			/*
    		ProcessorMissingDescriptorsQuery mq = new ProcessorMissingDescriptorsQuery();
    		QueryCombinedStructure q = (QueryCombinedStructure)mq.process(p);
    		q.setCombine_as_and(true);
    		q.setScope(model.getTestInstances());
    		q.setId(1);
    		*/
    		IBatchStatistics stats = batch.process(model.getTestInstances());
    		//System.out.println(stats);
    		if (reference ==null)	
    			return new Reference(String.format("%s/All/%s",OntologyResource.resource,model.getDependent().getName()));
    		else return reference;
		} catch (Exception x) {
			x.printStackTrace();
			throw new AmbitException(x);
		} finally {
			
		}
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
