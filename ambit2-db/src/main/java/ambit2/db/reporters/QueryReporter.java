package ambit2.db.reporters;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.base.processors.Reporter;
import ambit2.db.readers.IQueryRetrieval;

/**
 * Executes a query {@link IQueryRetrieval} and reports the results via {@link Reporter} interface
 * @author nina
 *
 * @param <T>
 * @param <Q>
 * @param <Output>
 */
public abstract class QueryReporter<T,Q extends IQueryRetrieval<T>,Output> 
						extends QueryAbstractReporter<T,Q,Output> {

	/**
	 * 
	 */
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

	
}
