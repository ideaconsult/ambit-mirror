package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;

/**
 * Retrieves property values and assigns to structures
 * @author nina
 *
 */
public class RetrieveTupleStructure extends RetrieveTuple<IStructureRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1921203304102794953L;

	@Override
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = new StructureRecord();
			record.setIdstructure(rs.getInt(4));
			record.setIdchemical(rs.getInt(9));
			LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(7),rs.getString(8),rs.getInt(2));
			Property p = Property.getInstance(rs.getString(1),le); 
			Object value = rs.getObject(5);
			if (value == null) {
				value = rs.getObject(6);
				record.setProperty(p,value==null?Double.NaN:rs.getFloat(6));
			}
			else {
				if (NaN.equals(value.toString())) {
					record.setProperty(p,Double.NaN);
					p.setClazz(Number.class);
				} else {
					record.setProperty(p,rs.getString(5));
					p.setClazz(String.class);
				}				
			}
			
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
		
	}
}
