package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;

/**
 * Retrieves all property-value pairs for a given chemical and a given tuple
 * @author nina
 *
 */
public class RetrieveTuplePropertyValue extends RetrieveTuple<PropertyValue> {
	@Override
	public PropertyValue getObject(ResultSet rs) throws AmbitException {
		try {
			//"select name,idreference,idproperty,idstructure,ifnull(text,value) as value_string,value_num,title,url from property_values \n"+
			LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(7),rs.getString(8),rs.getInt(2));
			Property p = Property.getInstance(rs.getString(1),le); 
			Object value = rs.getObject(5);
			PropertyValue pv ;
			if (value == null) pv = new PropertyValue<Float>(p,rs.getFloat(6));
			else pv =  new PropertyValue<String>(p,rs.getString(5));
			pv.setId(rs.getInt(10));
			return pv;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}	
}
