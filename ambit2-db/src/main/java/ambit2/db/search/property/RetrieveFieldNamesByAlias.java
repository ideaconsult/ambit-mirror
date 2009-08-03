package ambit2.db.search.property;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.CDKConstants;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

/**
 * Finds properties by alias
 * @author nina
 *
 */
public class RetrieveFieldNamesByAlias extends AbstractPropertyRetrieval<String, String, StringCondition> {

	public static String sql = "select idproperty,name,units,title,url,idreference,comments from properties join catalog_references using(idreference)";
	public static String where = " where comments %s ?"; // COLLATE utf8_general_ci for case insensitive
		/**
	 * 
	 */
	private static final long serialVersionUID = 8369867048140756850L;
	public RetrieveFieldNamesByAlias() {
		this(CDKConstants.NAMES);
	}
	public RetrieveFieldNamesByAlias(String alias) {
		super();
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setValue(alias);
	}
		public List<QueryParam> getParameters() throws AmbitException {
			if (getValue()!=null) {
				List<QueryParam> params = new ArrayList<QueryParam>();
				params.add(new QueryParam<String>(String.class, getValue()));				
				return params;
			} else return null;
			
		}
		public String getSQL() throws AmbitException {
			if (getValue()!=null) {
				return sql + String.format(where,getCondition().getSQL());
			}
			return sql;
		}
		/*

		public Property getObject(ResultSet rs) throws AmbitException {
			try {
				Property p = Property.getInstance(rs.getString(2),rs.getString(4),rs.getString(5));
				p.setId(rs.getInt(1));
				p.setUnits(rs.getString(3));
				p.setLabel(rs.getString(7));
				p.getReference().setId(rs.getInt(6));
				return p;
			} catch (SQLException x) {
				throw new AmbitException(x);
			}
		}
		*/
		public String getFieldID() {
			return "idproperty";
		}
		public String getValueID() {
			return "name";
		}
		public Class getFieldType() {
			return String.class;
		}
		public Class getValueType() {
			return String.class;
		}
		@Override
		public String toString() {

			return String.format("Property %s %s", getCondition()==null?"":getCondition(),getValue()==null?"":getValue());
		}
}