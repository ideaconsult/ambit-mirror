package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.SetCondition;
import ambit2.db.search.StringCondition;

public class QueryFieldMultiple extends QueryFieldAbstract<List<String>,SetCondition,StringCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -731666271442110526L;
	public QueryFieldMultiple() {
		setFieldname(null);
		setNameCondition(StringCondition.getInstance("="));
		setCondition(new SetCondition(SetCondition.conditions.in));
	}
	
	protected static String queryValueCaseSensitive = "value %s (%s)"; 
	protected static String queryValueCaseInsensitive = "value_ci %s (%s)"; 
	
	@Override
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = false;
	}
	public String getSQL() throws AmbitException {
		
		String whereName = ((getFieldname() ==null) || "".equals(getFieldname().getName()))?"":
				String.format(queryField,searchMode.getSQL(),getNameCondition().getSQL().toString());
		//no property retrieval for case sensitive queries, because it is slow
		if (isCaseSensitive()) 
			return String.format(
					sqlField,
					whereName,
					String.format(queryValueCaseSensitive,getCondition().getSQL(),getWhereQuery())
					);
		else
			return String.format(
					isRetrieveProperties()?sqlFieldProperties:sqlField_ci,
					whereName,
					String.format(queryValueCaseInsensitive,getCondition().getSQL(),getWhereQuery())
					);
		
	}
	/*
	public String getSQL() throws AmbitException {
		
		String whereName = ((getFieldname() ==null) || "".equals(getFieldname().getName()))?"":
				String.format(queryField,searchMode.getSQL(),getNameCondition().getSQL().toString());
		return String.format(
				isRetrieveProperties()?sqlFieldProperties:sqlField,
				whereName,
				getWhereQuery()
				);

	}
	*/
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue() == null) throw new AmbitException("Parameter not defined!");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		if ((getFieldname() ==null) || "".equals(getFieldname().getName()))
			;
		else
			params.add(new QueryParam<String>(String.class, isSearchByAlias()?getFieldname().getLabel():getFieldname().getName()));

		for (String value: getValue()) {
			String search = isCaseSensitive()?value:value.toLowerCase();
			params.add(new QueryParam<String>(String.class, search.length()>255?search.substring(1,255):search));
		}
		return params;
	}
	@Override
	public String toString() {
		if ((getFieldname()==null) && (getValue()==null)) return "Search by multiple text properties";
		else return super.toString();

	}	
	
	protected String getWhereQuery() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < value.size(); i++) {
			b.append(i==0?" ":",");
			b.append("?");
		}
		return b.toString();
	}
}
