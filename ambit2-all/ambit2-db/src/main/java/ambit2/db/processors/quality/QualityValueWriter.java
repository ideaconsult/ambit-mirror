package ambit2.db.processors.quality;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.AbstractRepositoryWriter;
import ambit2.db.readers.RetrieveField;
import ambit2.db.update.qlabel.ValuesQualityCheck;

public class QualityValueWriter extends	AbstractRepositoryWriter<IStructureRecord, Property> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3205301967748956995L;
	protected ValuesQualityCheck query = new ValuesQualityCheck();
	protected RetrieveField field = new RetrieveField();
	
	public Property getProperty() {
		return query.getProperty();
	}

	public void setProperty(Property property) {
		query.setProperty(property);		
	}

	public QualityValueWriter() {
		super();
		field.setSearchByAlias(true);
		setProperty(Property.getCASInstance());
		field.setFieldname(getProperty());
	}

	@Override
	public Property create(IStructureRecord record) throws SQLException,
			OperationNotSupportedException, AmbitException {
		ResultSet rs = null;
		
		try {
			field.setValue(record);
			rs = queryexec.process(field);
			while (rs.next()) {

				query.setObject(field.getObject(rs).toString());
				query.setGroup(record);
				exec.process(query);
			}

		} catch (Exception x) {
			
		} finally {
			try {
			queryexec.closeResults(rs);
			} catch (Exception x) {}
		}
		return query.getProperty();
	}
}
