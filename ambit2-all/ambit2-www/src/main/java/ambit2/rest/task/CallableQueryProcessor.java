package ambit2.rest.task;

import java.sql.Connection;
import java.util.concurrent.Callable;

import org.restlet.Application;
import org.restlet.data.Reference;
import org.restlet.data.Request;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.DbReaderStructure;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.rest.AmbitApplication;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.dataset.RDFStructuresReader;

public abstract class CallableQueryProcessor<Target,Result> implements Callable<Reference> {
	protected AbstractBatchProcessor batch; 
	protected Target target;
	protected Reference sourceReference;
	protected AmbitApplication application;
	protected Reference applicationRootReference;

	public CallableQueryProcessor(Reference uri,Reference applicationRootReference,AmbitApplication application) {
		this.sourceReference = uri;
		this.application = application;
		this.applicationRootReference = applicationRootReference;
	}
	
	public Reference call() throws Exception {
		Connection connection = null;
		try {
			target = createTarget(sourceReference);
			batch = createBatch(target);
			batch.setProcessorChain(createProcessors());
			
    		connection = application.getConnection((Request)null);
    		if (connection.isClosed()) connection = application.getConnection((Request)null);			
			batch.setConnection(connection);
			batch.process(target);
			return createReference();
		} catch (Exception x) {
			x.printStackTrace();
			throw x;
		} finally {
			try { connection.close(); } catch (Exception x) {}
		}
	}
	
	protected AbstractBatchProcessor createBatch(Target target) throws Exception{
		if (target == null) throw new Exception("");
		if (target instanceof AbstractStructureQuery)
			return new DbReaderStructure();
		else
			return new RDFStructuresReader(applicationRootReference.toString());
	}
	protected abstract Target createTarget(Reference reference) throws Exception;
	protected abstract Reference createReference() throws Exception ;
	protected abstract ProcessorsChain<Result, IBatchStatistics, IProcessor> createProcessors() throws Exception;
	protected abstract QueryURIReporter createURIReporter(Request request); 
}
