package ambit2.db.search.property;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.FreeTextQuery;

/**
 * Same as {@link FreeTextQuery} , but retrieves properties
 * @author nina
 *
 */
public class FreeTextPropertyQuery extends AbstractPropertyRetrieval<String[], String[], StringCondition>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7694873254695069097L;

	protected static final String sql = "%s";
	
	protected static final String pairSQL = 
		"(select idproperty,name,units,title,url,idreference,comments,null,islocal,type  from catalog_references join properties using(idreference) join property_values using(idproperty)\n"+
		"join property_string using(idvalue_string)\n  join structure using(idstructure)\n"+
		"    where name like ? and `value` like ?\n"+
		")\n"+
		"union\n"+
		"(select idproperty,name,units,title,url,idreference,comments,null,islocal,type  from catalog_references join properties using(idreference) join property_values using(idproperty)\n"+
		"join property_string using(idvalue_string)\n join structure using(idstructure)\n"+
		"    where `value` like ? and name like ?\n"+
		")\n";
	protected static final String singleSQLName = 
		"(select idproperty,name,units,title,url,idreference,comments,null,islocal,type  from catalog_references join properties using(idreference)\n"+
		"join property_values using(idproperty) join structure using(idstructure) where name like ?)\n";
	protected static final String singleSQLValue =
		"(select idproperty,name,units,title,url,idreference,comments,null,islocal,type  from catalog_references join properties using(idreference) join property_values using(idproperty)\n"+
		"join property_string using(idvalue_string)  join structure using(idstructure)  where `value` like ?)\n";



	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> param = new ArrayList<QueryParam>();
		for (String name: getFieldname())
			for (String value: getValue()) 
				if (!name.equals(value)) {
					param.add(new QueryParam<String>(String.class,name==null?null:String.format("%s%%",name)));
					param.add(new QueryParam<String>(String.class,value==null?null:String.format("%s%%",value)));
					param.add(new QueryParam<String>(String.class,value==null?null:String.format("%s%%",value)));
					param.add(new QueryParam<String>(String.class,name==null?null:String.format("%s%%",name)));
				}
		for (String value: getValue()) 
			param.add(new QueryParam<String>(String.class,value==null?null:String.format("%s%%",value)));
		
		for (String name: getFieldname()) 
			param.add(new QueryParam<String>(String.class,name==null?null:String.format("%s%%",name)));
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
	public Property getObject(ResultSet rs) throws AmbitException {
		try {
			LiteratureEntry ref = new LiteratureEntry(rs.getString(4),rs.getString(5));
			Property p = new Property(rs.getString(2),ref);
			p.setId(rs.getInt(1));
			p.setUnits(rs.getString(3));
			return p;
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
