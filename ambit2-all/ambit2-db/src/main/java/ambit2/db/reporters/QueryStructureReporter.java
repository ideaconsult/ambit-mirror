package ambit2.db.reporters;

import ambit2.base.config.AMBITConfigProperties;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.DbReader;
import ambit2.db.DbReaderStructure;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import net.idea.modbcum.r.QueryReporter;

/**
 * Parent class for all reporters working with structure queries. Retrieves
 * structure if pre-screening is necessary.
 * 
 * @author nina
 *
 * @param <IStructureRecord>
 * @param <Q>
 * @param <Output>
 */
public abstract class QueryStructureReporter<Q extends IQueryRetrieval<IStructureRecord>, Output>
		extends QueryReporter<IStructureRecord, Q, Output> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7383576401402360892L;

	protected AbstractBatchProcessor<IQueryRetrieval<IStructureRecord>, IStructureRecord> createBatch(Q query) {
		DbReader<IStructureRecord> reader = new DbReaderStructure();
		reader.setHandlePrescreen(true);
		return reader;
	}

	public synchronized boolean isIncludeLicenseInTextFiles() {
		try {
			AMBITConfigProperties properties = new AMBITConfigProperties();
			return properties.getBooleanPropertyWithDefault("license.intextfiles",
					AMBITConfigProperties.ambitProperties, false);
		} catch (Exception x) {
			return false;
		}
	}
}
