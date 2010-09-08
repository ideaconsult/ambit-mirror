package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
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
	
	protected static String queryValueCaseSensitive = "value in (?,..?)"; 
	protected static String queryValueCaseInsensitive = "lower(value) %s (?...?)"; 
	
	public final static String sqlField = 
		"select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text\n" +	
		"FROM structure s1\n"+
		"LEFT JOIN structure s2 ON s1.idchemical = s2.idchemical  AND (1E10*s1.preference+s1.idstructure) > (1E10*s2.preference+s2.idstructure)\n"+
		"join (\n"+
		"select distinct(structure.idchemical)\n"+
		"from structure\n"+
		"join  property_values using(idstructure)\n"+
		"join property_string using (idvalue_string)\n"+
		"join properties using(idproperty)\n"+
		"where %s %s\n"+
		") a on a.idchemical=s1.idchemical\n"+
		"where s2.idchemical is null\n";
	public String getSQL() throws AmbitException {
		
		String whereName = ((getFieldname() ==null) || "".equals(getFieldname().getName()))?"":
				String.format(queryField,searchMode.getSQL(),getNameCondition().getSQL().toString());
		return String.format(
				isRetrieveProperties()?sqlFieldProperties:sqlField,
				whereName,
				getWhereQuery()
				);

	}
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
		b.append(isCaseSensitive()?" value ":" lower(value) ");
		b.append(getCondition().getSQL());
		for (int i = 0; i < value.size(); i++) {
			b.append(i==0?" (":",");
			b.append("?");
		}
		b.append(")");
		return b.toString();
	}
}
