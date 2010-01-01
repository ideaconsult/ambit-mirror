package ambit2.rest.task;


import java.util.List;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.data.model.Algorithm;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.AmbitApplication;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;

public class CallableSimpleModelCreator< Result> extends	CallableModelCreator<Result> {

	public CallableSimpleModelCreator(Reference datasetUri,
				Reference applicationRootReference,
				AmbitApplication application,
				Algorithm algorithm,
				ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter,
				AlgorithmURIReporter alg_reporter
				) {
		super(datasetUri,applicationRootReference,application,algorithm,reporter,alg_reporter);

	}	
	@Override
	protected ProcessorsChain<Result, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		return null;
	}
	@Override
	protected Reference createTarget(Reference reference) throws Exception {
		return reference;
	}
	


	protected ModelQueryResults createModel() throws Exception {
		try {
			List<Property> p = DescriptorsFactory.createDescriptor2Properties(algorithm.getContent().toString());
			if ((p == null)||(p.size()==0)) throw new ResourceException(Status.SERVER_ERROR_INTERNAL,"Can't create a model from "+algorithm);

			//String dataset_uri = getParameter(requestHeaders,headers.dataset_id.toString(),headers.dataset_id.getDescription(),headers.dataset_id.isMandatory());
			//String params = getParameter(requestHeaders,headers.algorithm_parameters.toString(),headers.algorithm_parameters.getDescription(),headers.algorithm_parameters.isMandatory());  	
			ModelQueryResults mr = new ModelQueryResults();
			mr.setName(algorithm.getName());
			mr.setContent(algorithm.getContent().toString());
			mr.setAlgorithm(alg_reporter.getURI(algorithm));
			mr.setPredictors(algorithm.getInput());

			Template dependent = new Template();
			dependent.setName(String.format("Model-%s",algorithm.getName()));		
			mr.setDependent(dependent);
			
			for (Property property:p) dependent.add(property);

			return mr;			
		
		} catch (Exception x) {
				 throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		}
	}
	@Override
	protected AbstractBatchProcessor createBatch(Object target)
			throws Exception {
		return null;
	}

}
