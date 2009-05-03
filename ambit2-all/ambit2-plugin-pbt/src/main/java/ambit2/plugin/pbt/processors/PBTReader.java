package ambit2.plugin.pbt.processors;

import java.sql.Connection;
import java.sql.SQLException;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.ValuesByTemplateReader;
import ambit2.db.search.property.TemplateQuery;
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
		TemplateQuery query = new TemplateQuery();
		query.setValue(PBTWorkBook.PBT_TITLE);
		setPropertyQuery(query);	
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
	protected PBTWorkBook createResult(IStructureRecord target) throws AmbitException{
		try {
		return new PBTWorkBook();
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	@Override
	protected void set(PBTWorkBook result, Property fieldname, Object value)
			throws AmbitException {
		result.set(fieldname.getName(), value);
		
	}


	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}


}
