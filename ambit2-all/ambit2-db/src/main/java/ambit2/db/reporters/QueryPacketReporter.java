package ambit2.db.reporters;



import java.sql.Connection;
import java.sql.ResultSet;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.r.QueryAbstractReporter;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.RetrieveProfileValuesAsRow;
import ambit2.db.search.QueryExecutor;

public abstract class QueryPacketReporter<Q extends IQueryRetrieval<IStructureRecord>,Output> extends QueryAbstractReporter<IStructureRecord,Q,Output>	 {
	protected QueryExecutor exec;
	protected RetrieveProfileValuesAsRow chunkQuery;

	protected Profile<Property> template;
	protected int index = 0;
	protected int[] idstructure = new int[10];
	protected int[] idcompound = new int[10];
	
	public int getChunkSize() {
		return idstructure.length;
	}

	public void setChunkSize(int chunkSize) {
		idstructure  = new int[chunkSize];
		idcompound  = new int[chunkSize];
		for (int i=0; i < idstructure.length;i++) idstructure[i] = 0;
		for (int i=0; i < idcompound.length;i++) idcompound[i] = 0;
	}
	
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		exec.setConnection(connection);
	}
	
	@Override
	public void close() throws Exception {
		exec.close();
		super.close();
	}
	public QueryPacketReporter(Profile<Property> template,int chunkSize) {
		super();
		setChunkSize(chunkSize);
		this.template = template;
		exec = new QueryExecutor();
		exec.setCloseConnection(false);

		chunkQuery = new RetrieveProfileValuesAsRow(template,false);
		chunkQuery.setRecord(new StructureRecord());
		
		processors = new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		processors.add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				
				idstructure[index] = target.getIdstructure();
				idcompound[index] = target.getIdchemical();
				
				index++;
				if (index>=idstructure.length) { 
					
					processChunks();

					index = 0;
				}
				return target;
			};
		});		
	}

	protected void processChunks() throws AmbitException {
		if ((index==0)&& idstructure[index]<=0) return;
		ResultSet rs = null ;
		try {
			if (chunkQuery.getChemicalsOnly())  {
				chunkQuery.setValue(idcompound);
				chunkQuery.setChemicalsOnly(true);
			} else {
				chunkQuery.setValue(idstructure);
				chunkQuery.setChemicalsOnly(false);
			}
		
			rs = exec.process(chunkQuery);
			while (rs.next()) {
				IStructureRecord record = chunkQuery.getObject(rs);
				//Arrays.binarySearch(idstructure, record.getIdstructure());
				processItem(record);
			}
		} catch (Exception x) {
			
		} finally {
			try { rs.close();} catch (Exception x) {}
		}
		for (int i=0; i < idstructure.length;i++) idstructure[i] = 0; 
	}
	
	@Override
	protected void wrapup() throws AmbitException {
		
		processChunks();
		super.wrapup();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7517219316610459335L;

	@Override
	public void footer(Output output, Q query) {
	
	}

	@Override
	public void header(Output output, Q query) {
	
	}

	@Override
	public void open() throws DbAmbitException {
		
	}

}

