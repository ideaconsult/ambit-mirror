package ambit2.rest.task;


import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.data.model.Algorithm;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.builder.SimpleModelBuilder;

public class CallableSimpleModelCreator<Result> extends CallableModelCreator<Object,Result,SimpleModelBuilder> {
	
	public CallableSimpleModelCreator(Form form,
				Reference applicationRootReference,
				Context context,
				Algorithm algorithm,
				ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter,
				AlgorithmURIReporter alg_reporter,
				boolean hidden
				) {
		super(form,context,algorithm,
				new SimpleModelBuilder(applicationRootReference,reporter,alg_reporter,hidden)
		);
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

	@Override
	protected AbstractBatchProcessor createBatch(Object target)
			throws Exception {
		return null;
	}

}
