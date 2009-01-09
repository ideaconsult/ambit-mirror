package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ambit2.core.exceptions.AmbitException;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

public class RetrieveFieldNames extends AbstractQuery<String, String, StringCondition> implements IRetrieval<String>{
	public static String sql = "select idproperty,name from properties";
		/**
	 * 
	 */
	private static final long serialVersionUID = 8369867048140756850L;
	
		public List<QueryParam> getParameters() throws AmbitException {
			return null;
		}
		public String getSQL() throws AmbitException {
			return sql;
		}
		/**
		 * returns field name
		 */
		public String getObject(ResultSet rs) throws AmbitException {
			try {
				return rs.getString("name");
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
