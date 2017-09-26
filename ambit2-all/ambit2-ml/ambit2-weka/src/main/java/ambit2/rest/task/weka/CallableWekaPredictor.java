package ambit2.rest.task.weka;

import org.restlet.Context;
import org.restlet.data.ClientInfo;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.rest.dataset.RDFInstancesParser;
import ambit2.rest.model.predictor.ModelPredictor;
import ambit2.rest.model.task.CallableModelPredictor;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import weka.core.Instance;

public class CallableWekaPredictor<T,USERID> extends CallableModelPredictor<Instance,ModelPredictor<T,Instance>,USERID> {
	//protected String[] targetURI;
	public CallableWekaPredictor(Form form, Reference appReference,
			Context context, ModelPredictor<T,Instance> predictor,USERID token,String referer, ClientInfo clientInfo) {
		super(form, appReference, context,predictor,token,referer,clientInfo);
	}

	protected AbstractBatchProcessor createBatch(Object target) throws Exception{
		if (target == null) throw new Exception("");
		//use RDFObject parser and create instances on the fly
		
		//need tomake sure all descriptors are read here
		return new RDFInstancesParser(applicationRootReference.toString(),referer) ;
	}	
	
	@Override
	protected Object createTarget(Reference reference) throws Exception {
		prepareForeignProcessing(reference);
		return reference;
	}
}
