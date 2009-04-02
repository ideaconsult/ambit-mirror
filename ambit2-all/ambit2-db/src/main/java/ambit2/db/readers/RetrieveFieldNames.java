package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

public class RetrieveFieldNames extends AbstractQuery<String, Property, StringCondition,Property> implements IQueryRetrieval<Property>{
	public static String sql = "select idproperty,name,units,title,url,idreference,comments from properties join catalog_references using(idreference)";
	public static String where = " where %s %s ? and title %s ?";
		/**
	 * 
	 */
	private static final long serialVersionUID = 8369867048140756850L;
	public RetrieveFieldNames() {
		super();
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
	}
		public List<QueryParam> getParameters() throws AmbitException {
			if ((getFieldname()!=null) && (getValue()!=null)) {
				List<QueryParam> params = new ArrayList<QueryParam>();
				params.add(new QueryParam<String>(String.class, getValue().getName()));				
				params.add(new QueryParam<String>(String.class, getValue().getReference().getTitle()));	
				return params;
			} else return null;
			
		}
		public String getSQL() throws AmbitException {
			if ((getFieldname()!=null) && (getValue()!=null)) {
				return sql + String.format(where,getFieldname(),getCondition().getSQL(),getCondition().getSQL());
			}
			return sql;
		}
		/**
		 * returns field name
		 */
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
}
