package ambit2.rest.task;

import java.sql.Connection;

import org.restlet.data.Reference;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.data.model.Algorithm;
import ambit2.db.UpdateExecutor;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.model.CreateModel;
import ambit2.rest.AmbitApplication;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;

public abstract class CallableModelCreator<Item>  extends	CallableQueryProcessor<Object, Item> {
	protected ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter;
	protected AlgorithmURIReporter alg_reporter;
	protected Algorithm algorithm;
	
	public CallableModelCreator(Reference datasetUri,
			Reference applicationRootReference, AmbitApplication application,
			Algorithm algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter,
			AlgorithmURIReporter alg_reporter) {
		super(datasetUri, applicationRootReference, application);
		this.reporter = reporter;
		this.algorithm = algorithm;
		this.alg_reporter = alg_reporter;
		 
	}
	/**
	 * Writes the model into database and returns a reference
	 */
	@Override
	protected Reference createReference(Connection connection) throws Exception {
		UpdateExecutor<CreateModel> x = new UpdateExecutor<CreateModel>();
		try {
			ModelQueryResults model = createModel();
			CreateModel update = new CreateModel(model);
			
			x.setConnection(connection);
			x.process(update);
			return new Reference(reporter.getURI(model));
		} catch (Exception e) {
			logger.error(e);
			throw e;
		} finally {
			try {x.close();} catch (Exception xx){}
		}
	}
	
	protected abstract ModelQueryResults createModel() throws Exception;
	
	@Override
	protected ProcessorsChain<Item, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		return null;
	}	
}
