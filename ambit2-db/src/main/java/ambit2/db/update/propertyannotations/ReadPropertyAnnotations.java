package ambit2.db.update.propertyannotations;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;

/**
 * 
 * @author nina
 *
 */
public class ReadPropertyAnnotations extends AbstractQuery<Property, Integer, EQCondition, PropertyAnnotation> 
																implements IQueryRetrieval<PropertyAnnotation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4470430491847500096L;
	public enum _fields  {
		idproperty,
		rdf_type,
		predicate,
		object
	}
	protected final String sql = "SELECT idproperty,rdf_type,predicate,object FROM property_annotation where idproperty=?"; 
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if ((getFieldname()==null) || (getFieldname().getId()<=0)) throw new AmbitException("No property!");
		
		List<QueryParam> param = new ArrayList<QueryParam>();
		param.add(new QueryParam<Integer>(Integer.class,getFieldname().getId()));
		return param;
	}

	@Override
	public PropertyAnnotation getObject(ResultSet rs) throws AmbitException {
		PropertyAnnotation a = new PropertyAnnotation();
		try {
			a.setIdproperty(rs.getInt(_fields.idproperty.ordinal()+1));
			a.setType(rs.getString(_fields.rdf_type.ordinal()+1));
			a.setObject(rs.getString(_fields.object.ordinal()+1));
			a.setPredicate(rs.getString(_fields.predicate.ordinal()+1));
			return a;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(PropertyAnnotation object) {
		return 1;
	}

}
