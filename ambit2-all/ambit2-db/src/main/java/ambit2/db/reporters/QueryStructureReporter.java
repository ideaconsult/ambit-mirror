package ambit2.db.reporters;

import java.io.InputStream;
import java.util.Properties;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import net.idea.modbcum.r.QueryReporter;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.DbReader;
import ambit2.db.DbReaderStructure;

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

	protected AbstractBatchProcessor<IQueryRetrieval<IStructureRecord>, IStructureRecord> createBatch(Q query) {
		DbReader<IStructureRecord> reader = new DbReaderStructure();
		reader.setHandlePrescreen(true);
		return reader;
	}
	
	public synchronized boolean isIncludeLicenseInTextFiles()  {
		try {
			Properties properties = new Properties();
			InputStream in = QueryStructureReporter.class.getClassLoader().getResourceAsStream("ambit2/rest/config/ambit2.pref");
			properties.load(in);
			in.close();	
			return Boolean.parseBoolean(properties.getProperty("license.intextfiles"));
		} catch (Exception x) {
			return false;
		}
	}	
}
