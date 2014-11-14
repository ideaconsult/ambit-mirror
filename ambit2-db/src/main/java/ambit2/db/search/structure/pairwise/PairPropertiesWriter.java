package ambit2.db.search.structure.pairwise;

import java.sql.Connection;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.AbstractRepositoryWriter;
import ambit2.db.processors.PropertyValuesWriter;

public class PairPropertiesWriter extends AbstractRepositoryWriter<IStructureRecord[],IStructureRecord[]>{
	protected PropertyValuesWriter writer = new PropertyValuesWriter();
	/**
	 * 
	 */
	private static final long serialVersionUID = 941417486755990227L;

	@Override
	public synchronized void setConnection(Connection connection)
			throws DbAmbitException {
		super.setConnection(connection);
		writer.setConnection(connection);
	}
	@Override
	public void setCloseConnection(boolean closeConnection) {
		super.setCloseConnection(closeConnection);
		writer.setCloseConnection(closeConnection);
	}
	@Override
	public void close() throws Exception {
		try {writer.close(); } catch (Exception x) {}
		super.close();
	}
	@Override
	public IStructureRecord[] process(IStructureRecord[] target)
			throws AmbitException {
		try {writer.process(target[0]);} catch (Exception x) {}
		try {writer.process(target[1]);} catch (Exception x) {}
		return target;
	}
	public void setDataset(SourceDataset dataset) {
		writer.setDataset(dataset);
	}
}
