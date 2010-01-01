package ambit2.rest.task;

import org.restlet.data.Reference;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.processors.DescriptorsCalculator;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.AmbitApplication;
import ambit2.rest.dataset.RDFInstancesParser;
import ambit2.rest.model.ModelURIReporter;

public class CallableWekaPredictor extends CallableModelPredictor {

	public CallableWekaPredictor(Reference target, Reference appReference,
			AmbitApplication application, ModelQueryResults model,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter) {
		super(target, appReference, application, model, reporter);
		// TODO Auto-generated constructor stub
	}
	protected IProcessor<IStructureRecord,IStructureRecord> createPredictor(ModelQueryResults model) throws Exception {
		Profile<Property> p = new Profile<Property>();
		Property property = DescriptorsFactory.createDescriptor2Property(model.getContent());
		property.setEnabled(true);
		p.add(property);
		//TODO same code as in wekmodelcreator
		DescriptorsCalculator calculator = new DescriptorsCalculator();
		calculator.setDescriptors(p);	
		return calculator;
	}
	
	protected AbstractBatchProcessor createBatch(Object target) throws Exception{
		if (target == null) throw new Exception("");
		//use RDFObject parser and create instances on the fly
		return new RDFInstancesParser(applicationRootReference.toString());
	}	
}
