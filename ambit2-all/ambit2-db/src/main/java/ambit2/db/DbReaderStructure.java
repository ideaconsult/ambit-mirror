package ambit2.db;

import java.sql.Connection;
import java.sql.SQLException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveStructure;

/**
 * Reads structures given a query.
 * @author nina
 *
 */
public class DbReaderStructure extends DbReader<IStructureRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7593295052807885237L;
	protected ProcessorStructureRetrieval retriever;
	public DbReaderStructure() {
		super();
		RetrieveStructure q = new RetrieveStructure();
		q.setMaxRecords(1);
		retriever = new ProcessorStructureRetrieval(q);
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		retriever.setConnection(connection);
	}
	@Override
	public void close() throws SQLException {
		retriever.close();
		super.close();
	}
	@Override
	protected boolean prescreen(IQueryRetrieval<IStructureRecord> query,
			IStructureRecord object) throws AmbitException {
		IStructureRecord record = retriever.process(object);
		object.setIdchemical(record.getIdchemical());
		object.setIdstructure(record.getIdstructure());
		object.setContent(record.getContent());
		object.setFormat(record.getFormat());
		return super.prescreen(query, record);
	}
}
