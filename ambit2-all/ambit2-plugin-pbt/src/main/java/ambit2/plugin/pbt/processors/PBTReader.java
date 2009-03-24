package ambit2.plugin.pbt.processors;

import java.sql.Connection;
import java.sql.SQLException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.readers.ValuesByTemplateReader;
import ambit2.plugin.pbt.PBTWorkBook;

/**
 * Reads values for PBT template
 * @author nina
 *
 */
public class PBTReader extends ValuesByTemplateReader<PBTWorkBook> {
	protected ProcessorStructureRetrieval reader;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8438218671883237423L;
	public PBTReader() {
		reader = new ProcessorStructureRetrieval();
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		reader.setConnection(connection);
		
	}
	@Override
	public PBTWorkBook process(IStructureRecord target) throws AmbitException {
		return super.process(reader.process(target));
	}
	@Override
	public void close() throws SQLException {

		super.close();
		reader.close();
	}
	
	@Override
	protected PBTWorkBook createResult()throws AmbitException{
		try {
		return new PBTWorkBook();
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	@Override
	protected void set(PBTWorkBook result, String fieldname, String value)
			throws AmbitException {
		result.set(fieldname, value);
		
	}
	@Override
	protected String getTemplateName() {
		return PBTWorkBook.PBT_TITLE;
	}
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}


}
