package ambit2.db.reporters;



import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveProfileValuesAsRow;
import ambit2.db.search.QueryExecutor;

public abstract class QueryPacketReporter<Q extends IQueryRetrieval<IStructureRecord>,Output> extends QueryAbstractReporter<IStructureRecord,Q,Output>	 {
	protected QueryExecutor exec;
	protected RetrieveProfileValuesAsRow chunkQuery;

	protected Profile<Property> template;
	protected int index = 0;
	protected int[] chunks = new int[10];
	
	public int getChunkSize() {
		return chunks.length;
	}

	public void setChunkSize(int chunkSize) {
		chunks  = new int[chunkSize];
		for (int i=0; i < chunks.length;i++) chunks[i] = 0;
	}
	
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		exec.setConnection(connection);
	}
	
	@Override
	public void close() throws SQLException {
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
				
				chunks[index] = target.getIdstructure();
				index++;
				if (index>=chunks.length) { 
					
					processChunks();

					index = 0;
				}
				return target;
			};
		});		
	}

	protected void processChunks() throws AmbitException {
		if ((index==0)&& chunks[index]<=0) return;
		ResultSet rs = null ;
		try {
			chunkQuery.setValue(chunks);
			rs = exec.process(chunkQuery);
			while (rs.next()) {
				IStructureRecord record = chunkQuery.getObject(rs);
				processItem(record);
			}
		} catch (Exception x) {
			
		} finally {
			try { rs.close();} catch (Exception x) {}
		}
		for (int i=0; i < chunks.length;i++) chunks[i] = 0; 
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

