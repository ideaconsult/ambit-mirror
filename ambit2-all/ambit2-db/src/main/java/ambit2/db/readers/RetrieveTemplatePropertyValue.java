package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;

/**
 * Retrieves properties by given template
 * @author nina
 *
 */
public class RetrieveTemplatePropertyValue extends RetrieveTemplate<PropertyValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -743370966710931692L;
	@Override
	public PropertyValue getObject(ResultSet rs) throws AmbitException {
		try {
			//"select name,idreference,idproperty,idstructure,ifnull(text,value) as value_string,value_num,title,url from property_values \n"+
			LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(7),rs.getString(8),rs.getInt(2));
			Property p = Property.getInstance(rs.getString(1),le);
			p.setUnits(rs.getString(11));
			p.setId(rs.getInt(3));
			Object value = rs.getObject(5);
			PropertyValue pv ;
			if (value == null) {
				pv = new PropertyValue<Float>(p,rs.getFloat(6));
				p.setClazz(Number.class);
			}
			else {
				pv =  new PropertyValue<String>(p,rs.getString(5));
				p.setClazz(String.class);
			}

			pv.setId(rs.getInt(10));
			return pv;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}	
}
