package ambit2.plugin.pbt.processors;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.openscience.cdk.CDKConstants;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.ValuesByTemplateReader;
import ambit2.db.search.property.RetrieveFieldNamesByAlias;
import ambit2.db.search.property.TemplateQuery;
import ambit2.plugin.pbt.PBTWorkBook;
import ambit2.plugin.pbt.PBTWorkBook.WORKSHEET_INDEX;

/**
 * Reads values for PBT template
 * @author nina
 *
 */
public class PBTReader extends ValuesByTemplateReader<PBTWorkBook> {
	protected ProcessorStructureRetrieval reader;
	protected FieldsReader fieldsReader = new FieldsReader();
	/**
	 * 
	 */
	private static final long serialVersionUID = -8438218671883237423L;
	public PBTReader() {
		reader = new ProcessorStructureRetrieval();
		TemplateQuery templateQuery = new TemplateQuery();
		templateQuery.setValue(PBTWorkBook.PBT_TITLE);
		setPropertyQuery(templateQuery);
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		reader.setConnection(connection);
		fieldsReader.setConnection(connection);
	}
	@Override
	public PBTWorkBook process(IStructureRecord target) throws AmbitException {
		PBTWorkBook book = super.process(reader.process(target));
		try {
			if (book == null) book = new PBTWorkBook();
			fieldsReader.setBook(book);
			fieldsReader.setRow(10);fieldsReader.setCol(2);			
			fieldsReader.setAlias(CDKConstants.NAMES);
			fieldsReader.process(target);
			
			fieldsReader.setRow(11);fieldsReader.setCol(2);			
			fieldsReader.setAlias(CDKConstants.CASRN);
			fieldsReader.process(target);			


		} catch (Exception x) {x.printStackTrace();}
		return book;
	}
	@Override
	public void close() throws SQLException {

		super.close();
		reader.close();
		fieldsReader.close();
	}
	
	@Override
	protected PBTWorkBook createResult(IStructureRecord target) throws AmbitException{
		try {
			PBTWorkBook book = new PBTWorkBook();
			//book.setNotify(false);
			return book;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	@Override
	protected void set(PBTWorkBook result, Property fieldname, Object value)
			throws AmbitException {
		if ((value==null) || "".equals(value))return;
		result.set(fieldname.getName(), value);

	}


	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}


}

class FieldsReader extends ValuesByTemplateReader<PBTWorkBook> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4700887598265181513L;
	protected PBTWorkBook book;
	protected int row;
	protected int col;

	public FieldsReader() {
		setPropertyQuery(new RetrieveFieldNamesByAlias());
	}
	public FieldsReader(PBTWorkBook book, int row, int col, String alias) {
		this();
		setBook(book);
		setRow(row);
		setCol(col);
	}
	@Override
	protected PBTWorkBook createResult(IStructureRecord target)
			throws AmbitException {
		return book;
	}

	@Override
	protected void set(PBTWorkBook book, Property fieldname, Object value)
			throws AmbitException {
		if (book == null) return;
		Object o = book.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).getExtendedCellValue(getRow(),getCol());
		if (o instanceof List) {
			if (((List)o).indexOf(value)==-1)
			((List)o).add(value);
		}
	}
	public String getAlias() {
		return ((RetrieveFieldNamesByAlias)getPropertyQuery()).getValue();
	}
	public void setAlias(String alias) {
		((RetrieveFieldNamesByAlias)getPropertyQuery()).setValue(alias);
	}
	public PBTWorkBook getBook() {
		return book;
	}
	public void setBook(PBTWorkBook book) {
		this.book = book;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}	
}
