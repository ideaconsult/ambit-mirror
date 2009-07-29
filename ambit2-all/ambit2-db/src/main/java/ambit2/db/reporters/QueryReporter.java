package ambit2.db.reporters;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.processors.Reporter;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.DbReader;
import ambit2.db.readers.IQueryRetrieval;

public abstract class QueryReporter<T,Q extends IQueryRetrieval<T>,Output> 
						extends AbstractDBProcessor<Q,Output>
						implements Reporter<Q,Output> {

	/**
	 * 
	 */

	protected Output output = null;	
	protected int maxRecords = 0;
	public int getMaxRecords() {
		return maxRecords;
	}
	public void setMaxRecords(int maxRecords) {
		this.maxRecords = maxRecords;
	}
	public Output getOutput() throws AmbitException {
		return output;
	}
	public void setOutput(Output output) throws AmbitException {
		this.output = output;
	}
	
	protected ProcessorsChain<T,IBatchStatistics,IProcessor> processors;
	public ProcessorsChain<T, IBatchStatistics, IProcessor> getProcessors() {
		return processors;
	}
	public void setProcessors(
			ProcessorsChain<T, IBatchStatistics, IProcessor> processors) {
		this.processors = processors;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -859832577309432246L;

	public QueryReporter() {
		super();
		processors = new ProcessorsChain<T,IBatchStatistics,IProcessor>();
		processors.add(new DefaultAmbitProcessor<T,T>() {
			public T process(T target) throws AmbitException {
				processItem(target,output);
				return target;
			};
		});
		
	}
	public abstract void header(Output output, Q query);
	public abstract void footer(Output output, Q query);
	
	public Output process(Q query) throws AmbitException {
		output = getOutput();
		header(output,query);
		DbReader<T> batch = new DbReader<T>();
		try {
			//batch.setMaxRecords(maxRecords);
			batch.setProcessorChain(processors);
			batch.setConnection(connection);			
			IBatchStatistics stats = batch.process(query);
			if (stats.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_READ)==0)
				throw new NotFoundException(query.toString());
			return output;
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x ) {
			throw new AmbitException(x);
		} finally {
			footer(output, query);
			try {batch.close();} catch (Exception x) {}
		}
	}	
	public abstract void processItem(T item, Output output);
}
