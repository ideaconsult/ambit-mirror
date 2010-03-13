package ambit2.rest.task;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import weka.core.Instance;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.rest.dataset.RDFInstancesParser;
import ambit2.rest.model.predictor.ModelPredictor;

public class CallableWekaPredictor<T> extends CallableModelPredictor<Instance,ModelPredictor<T,Instance>> {
	//protected String[] targetURI;
	public CallableWekaPredictor(Form form, Reference appReference,
			Context context, ModelPredictor<T,Instance> predictor) {
		super(form, appReference, context,predictor);
	}

	protected AbstractBatchProcessor createBatch(Object target) throws Exception{
		if (target == null) throw new Exception("");
		//use RDFObject parser and create instances on the fly
		
		//need tomake sure all descriptors are read here
		return new RDFInstancesParser(applicationRootReference.toString()) ;
	}	
	
	@Override
	protected Object createTarget(Reference reference) throws Exception {
		return reference;
	}
}
