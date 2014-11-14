package ambit2.rest.task.weka;

import net.idea.modbcum.p.batch.AbstractBatchProcessor;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import weka.core.Instance;
import ambit2.rest.dataset.RDFInstancesParser;
import ambit2.rest.model.predictor.ModelPredictor;
import ambit2.rest.task.CallableModelPredictor;

public class CallableWekaPredictor<T,USERID> extends CallableModelPredictor<Instance,ModelPredictor<T,Instance>,USERID> {
	//protected String[] targetURI;
	public CallableWekaPredictor(Form form, Reference appReference,
			Context context, ModelPredictor<T,Instance> predictor,USERID token) {
		super(form, appReference, context,predictor,token);
	}

	protected AbstractBatchProcessor createBatch(Object target) throws Exception{
		if (target == null) throw new Exception("");
		//use RDFObject parser and create instances on the fly
		
		//need tomake sure all descriptors are read here
		return new RDFInstancesParser(applicationRootReference.toString()) ;
	}	
	
	@Override
	protected Object createTarget(Reference reference) throws Exception {
		prepareForeignProcessing(reference);
		return reference;
	}
}
