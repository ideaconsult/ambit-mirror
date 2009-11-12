package ambit2.db.reporters;

import java.sql.SQLException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.base.processors.Reporter;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.DbReader;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;

import com.sun.org.apache.xerces.internal.parsers.AbstractDOMParser;

/**
 * Executes a query {@link IQueryRetrieval} and reports the results via {@link Reporter} interface
 * @author nina
 *
 * @param <T>
 * @param <Q>
 * @param <Output>
 */
public abstract class QueryReporter<T,Q extends IQueryRetrieval<T>,Output> 
						extends AbstractDBProcessor<Q,Output>
						implements Reporter<Q,Output> {

	/**
	 * 
	 */

	protected Output output = null;	
	protected int maxRecords = 0;
	protected AbstractBatchProcessor batch;
	protected boolean showHeader = true;
	protected boolean autoCommit = true;

	public boolean isAutoCommit() {
		return autoCommit;
	}
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}
	public boolean isShowHeader() {
		return showHeader;
	}
	public void setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
	}
	public boolean isShowFooter() {
		return showFooter;
	}
	public void setShowFooter(boolean showFooter) {
		this.showFooter = showFooter;
	}

	protected boolean showFooter = true;
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
				processItem(target);
				return target;
			};
		});
		
	}
	public abstract void header(Output output, Q query);
	public abstract void footer(Output output, Q query);
	
	public Output process(Q query) throws AmbitException {
		output = getOutput();
		if (isShowHeader()) header(output,query);

		batch = createBatch();
		try {
			if (connection != null) {
				batch.setProcessorChain(processors);
				batch.setConnection(connection);			
				IBatchStatistics stats = batch.process(query);
				if (stats.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_READ)==0)
					throw new NotFoundException(query.toString());
			} 
			return output;
		} catch (AmbitException x) {
			try { if (isAutoCommit()) connection.rollback(); } catch (Exception xx) {} 
			throw x;
		} catch (Exception x ) {
			//TODO smth reasonable for java.io.IOException: An established connection was aborted by the software in your host machine
			try { if (isAutoCommit()) connection.rollback(); } catch (Exception xx) {} 
			throw new AmbitException(x);
			
		} finally {
			if (isShowFooter()) footer(output, query);
			try { if (isAutoCommit()) connection.commit(); } catch (Exception x) {} 
			try {batch.close();} catch (Exception x) {}
			try {connection.close();} catch (Exception x) {}
		}
	}	
	protected AbstractBatchProcessor<IQueryRetrieval<T>, T> createBatch() {
		DbReader<T> reader = new DbReader<T>();
		reader.setHandlePrescreen(true);
		return reader;
	}
	public abstract void processItem(T item) throws AmbitException;
	

	
}
