package ambit2.db.reporters;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.DbReader;
import ambit2.db.DbReaderStructure;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;

/**
 * Parent class for all reporters working with structure queries. Retrieves structure if pre-screening is necessary.
 * @author nina
 *
 * @param <IStructureRecord>
 * @param <Q>
 * @param <Output>
 */
public abstract class QueryStructureReporter<Q extends IQueryRetrieval<IStructureRecord>,Output>  
														extends  QueryReporter<IStructureRecord,Q,Output> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7383576401402360892L;

	protected AbstractBatchProcessor<IQueryRetrieval<IStructureRecord>, IStructureRecord> createBatch() {
		DbReader<IStructureRecord> reader = new DbReaderStructure();
		reader.setHandlePrescreen(true);
		return reader;
	}
}
