package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;

/**
 * Retrieve property value pairs and assign to a structure record
 * @author nina
 *
 */
public class RetrieveFieldStructure extends RetrieveField<IStructureRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8144364027565901933L;
	@Override
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = new StructureRecord();
			record.setIdstructure(4);
			record.setIdchemical(9);
			LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(7),rs.getString(8),rs.getInt(2));
			Property p = Property.getInstance(rs.getString(1),le); 
			p.setUnits(rs.getString(11));
			p.setLabel(rs.getString(12));
			Object value = rs.getObject(5);
			if (value == null) {
				value = rs.getObject(6);
				record.setRecordProperty(p,value==null?Double.NaN:rs.getFloat(6));
				p.setClazz(Number.class);
			}
			else 
				if (NaN.equals(value.toString())) {
					record.setRecordProperty(p,Double.NaN);
					p.setClazz(Number.class);
				} else {
					record.setRecordProperty(p,rs.getString(5));
					p.setClazz(String.class);
				}
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
		
	}
}
