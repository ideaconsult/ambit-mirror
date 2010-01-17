package ambit2.rest.task;

import java.sql.Connection;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import weka.core.Instance;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.processors.PropertyValuesWriter;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.RDFInstancesParser;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.WekaPredictor;

public class CallableWekaPredictor extends CallableModelPredictor<Instance> {
	protected String[] targetURI;
	public CallableWekaPredictor(Form form, Reference appReference,
			Context context, ModelQueryResults model,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter) {
		super(form, appReference, context, model, reporter);
		targetURI = form.getValuesArray(OpenTox.params.target.toString());
	}
	@Override
	protected IProcessor<Instance, IStructureRecord> createPredictor(
			ModelQueryResults model) throws Exception {
		try {
			WekaPredictor weka = new WekaPredictor(modelUriReporter,targetURI);
			weka.setWekaModel(model);
			return weka;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
	}

	protected AbstractBatchProcessor createBatch(Object target) throws Exception{
		if (target == null) throw new Exception("");
		//use RDFObject parser and create instances on the fly
		return new RDFInstancesParser(applicationRootReference.toString()) ;
	}	
	
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors() throws Exception {
		createProfileFromReference(new Reference(modelUriReporter.getURI(model)+"/dependent"),null,model.getDependent());
		createProfileFromReference(new Reference(modelUriReporter.getURI(model)+"/independent"),null,model.getPredictors());
		createProfileFromReference(new Reference(modelUriReporter.getURI(model)+"/predicted"),null,model.getPredicted());
		
		IProcessor<Instance,IStructureRecord> calculator = createPredictor(model);

		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();

		p1.add(calculator);
		p1.setAbortOnError(true);
		
		PropertyValuesWriter writer = new PropertyValuesWriter();
		p1.add(writer);
		
		return p1;
	}
	@Override
	protected Object createTarget(Reference reference) throws Exception {
		return reference;
	}
}
