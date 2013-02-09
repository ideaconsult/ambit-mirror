package ambit2.db.update.tuple;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;

public class QueryDataEntryValues extends QueryDataEntry {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2684142729469038438L;
	protected enum findex {
		idquery,
		idchemical,
		idstructure,
		selected,
		metric,
		text,
		name,
		idreference,
		idproperty,
		value_string,
		value_num,
		title,
		url,
		id,
		units
	}
	protected static String sql_values = 
		"select ? as idquery,idchemical,idstructure,1 as selected,idtuple as metric,id_srcdataset as text,\"+" +
		"name,idreference,idproperty,ifnull(text,value) as value_string,value_num,title,url,id,units\n"+
		"FROM tuples\n"+
		"join property_tuples using(idtuple)\n"+
		"join property_values using(id)\n"+
		"left join property_string using(idvalue_string)\n"+
		"join properties using(idproperty)\n"+
		"join catalog_references using(idreference)\n"+
		"%s\n"+
		"order by idtuple";		
	
	
	@Override
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		IStructureRecord record = super.getObject(rs);
		try {
			while (record.getDataEntryID()==rs.getInt(findex.metric.ordinal()+1)) {
				retrieveValues(rs, record);
				rs.next();
			}
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return record;
	}
	public void retrieveValues(ResultSet rs,IStructureRecord record) throws AmbitException {
		try {
			LiteratureEntry le = LiteratureEntry.getInstance(
					rs.getString(findex.title.ordinal()+1),
					rs.getString(findex.url.ordinal()+1),
					rs.getInt(findex.id.ordinal()+1)
					);
			Property p = Property.getInstance(rs.getString(findex.name.ordinal()+1),le); 
			p.setUnits(rs.getString(findex.units.ordinal()+1));
			Object value = rs.getObject(findex.value_string.ordinal()+1);
			
			if (value == null) {
				value = rs.getObject(findex.value_num.ordinal()+1);
				record.setProperty(p,value==null?Double.NaN:rs.getFloat(findex.value_num.ordinal()+1));
				p.setClazz(Number.class);
			}
			else {
				if (NaN.equals(value.toString())) {
					record.setProperty(p,Double.NaN);
					p.setClazz(Number.class);
				} else {
					record.setProperty(p,rs.getString(findex.value_string.ordinal()+1));
					p.setClazz(String.class);
				}
			}
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
		
	}
}
