package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;

/**
 * Retrive property-value pairs
 * 
 * @author nina
 * 
 */
public class RetrieveFieldPropertyValue extends RetrieveField<PropertyValue> {
    /**
     * 
     */
    private static final long serialVersionUID = -2730646908144430273L;

    @Override
    public PropertyValue getObject(ResultSet rs) throws AmbitException {
	try {

	    LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(7), rs.getString(8), rs.getInt(2));
	    Property p = new Property(rs.getString(1), le);
	    p.setUnits(rs.getString(11));
	    p.setLabel(rs.getString(12));
	    p.setId(rs.getInt(3));
	    Object value = rs.getObject(5);
	    PropertyValue pv;
	    if (value == null)
		pv = new PropertyValue<Float>(p, rs.getFloat(6));
	    else
		pv = new PropertyValue<String>(p, rs.getString(5));
	    pv.setId(rs.getInt(10));
	    return pv;
	} catch (SQLException x) {
	    throw new AmbitException(x);
	}
    }
}
