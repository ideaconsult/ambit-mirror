package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;

/**
<pre>
select idchemical,metric from
(
(select idchemical,2 as metric  from properties join property_values using(idproperty)
join property_string using(idvalue_string)  join structure using(idstructure)
    where name like "MA%" and value like "Reaction%"
)
union
(select idchemical,2 as metric  from properties join property_values using(idproperty)
join property_string using(idvalue_string)  join structure using(idstructure)
    where value like "MA%" and name like "Reaction%"
)
union
(select idchemical,1 as metric from properties
join property_values using(idproperty) join structure using(idstructure) where name like "MA%")
union
(select idchemical,1 as metric  from property_values
join property_string using(idvalue_string) join structure using(idstructure) where value like "MA%")
union
(select idchemical,1 as metric  from properties
join property_values using(idproperty)  join structure using(idstructure)  where name like "Reaction%")
union
(select idchemical,1 as metric  from property_values
join property_string using(idvalue_string)  join structure using(idstructure)  where value like "Reaction%")
) a


</pre>
 * @author nina
 *
 */
public class FreeTextQuery extends AbstractStructureQuery<String[], String[], StringCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4221828356692625890L;
	protected static final String sql = "%s";
	
	protected static final String pairSQL = 
		"(select idchemical,1 as metric  from properties join property_values using(idproperty)\n"+
		"join property_string using(idvalue_string)\n"+
		"    where name like ? and `value` like ?\n"+
		")\n"+
		"union\n"+
		"(select idchemical,1 as metric  from properties join property_values using(idproperty)\n"+
		"join property_string using(idvalue_string)\n"+
		"    where `value` like ? and name like ?\n"+
		")\n";
	protected static final String singleSQLName = 
		"(select idchemical,1 as metric from properties\n"+
		"join property_values using(idproperty) where name like ?)\n";
	protected static final String singleSQLValue =
		"(select idchemical,1 as metric  from property_values\n"+
		"join property_string using(idvalue_string)  where `value` like ?)\n";



	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> param = new ArrayList<QueryParam>();
		for (String name: getFieldname())
			for (String value: getValue()) 
				if (!name.equals(value)) {
					param.add(new QueryParam<String>(String.class,name));
					param.add(new QueryParam<String>(String.class,value));
					param.add(new QueryParam<String>(String.class,value));
					param.add(new QueryParam<String>(String.class,name));
				}
		for (String value: getValue()) 
			param.add(new QueryParam<String>(String.class,value));
		
		for (String name: getFieldname()) 
			param.add(new QueryParam<String>(String.class,name));
		return param;
	}

	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		String union = "";
		for (String name: getFieldname())
			for (String value: getValue()) 
				if (!name.equals(value)) {
					b.append(union);
					b.append(pairSQL);
					union = "\nunion\n";
				}
		for (String value: getValue()) {
			b.append(union);
			b.append(singleSQLValue);
			union = "\nunion\n";
		}
		for (String name: getFieldname()) {
			b.append(union);
			b.append(singleSQLName);
			union = "\nunion\n";
		}			
		return String.format(sql, b.toString());
	}
	@Override
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = new StructureRecord();
			record.setIdchemical(rs.getInt(1));
			return record;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (String s : getFieldname()) {
			b.append(s);
			b.append(" ");
		}
		return b.toString();
	}
}