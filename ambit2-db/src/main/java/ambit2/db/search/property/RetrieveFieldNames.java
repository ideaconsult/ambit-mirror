package ambit2.db.search.property;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.db.search.StringCondition;

public class RetrieveFieldNames extends AbstractPropertyRetrieval<String, Property, StringCondition>{

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
				return base_sql + String.format(where,getFieldname(),getCondition().getSQL(),getCondition().getSQL());
			}
			return base_sql;
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
}
