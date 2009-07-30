package ambit2.db.update.property;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;
import ambit2.db.search.property.AbstractPropertyRetrieval;

/**
 * Reads {@link Property} by id
 * @author nina
 *
 */
public class ReadProperty extends AbstractPropertyRetrieval<String, Integer, EQCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6247086921731939782L;
	public ReadProperty(Integer id) {
		setValue(id);
	}
	public ReadProperty() {
		this(null);
	}
		
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()!=null) {
			List<QueryParam> params = new ArrayList<QueryParam>();
			params.add(new QueryParam<Integer>(Integer.class, getValue()));				
			return params;
		} else return null;
	}

	public String getSQL() throws AmbitException {
		return getValue()==null?base_sql:String.format("%s where idproperty=?", base_sql);
	}


}
