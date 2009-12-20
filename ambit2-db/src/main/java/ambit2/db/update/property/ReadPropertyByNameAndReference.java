package ambit2.db.update.property;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.AbstractPropertyRetrieval;

/**
 * Reads properties by concatenated name+title  (this is unique id)
 * @author nina
 *
 */
public class ReadPropertyByNameAndReference extends AbstractPropertyRetrieval<String, String, StringCondition>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5168058984570810049L;
	public ReadPropertyByNameAndReference(String value) {
		super();
		setValue(value);
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class,getValue()));				
		return params;
	}

	public String getSQL() throws AmbitException {
		return String.format("%s where concat(name,title)=?",base_sql);
	}

}
