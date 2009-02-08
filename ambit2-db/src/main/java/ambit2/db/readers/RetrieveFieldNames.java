package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ambit2.core.data.LiteratureEntry;
import ambit2.core.data.Property;
import ambit2.core.exceptions.AmbitException;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

public class RetrieveFieldNames extends AbstractQuery<String, String, StringCondition,Property> implements IQueryRetrieval<Property>{
	public static String sql = "select idproperty,name,units,title,url,idreference from properties join catalog_references using(idreference)";
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
		public Property getObject(ResultSet rs) throws AmbitException {
			try {
				Property p = new Property(rs.getString(2));
				p.setId(rs.getInt(1));
				p.setUnits(rs.getString(3));
				LiteratureEntry reference = new LiteratureEntry(rs.getString(4));
				reference.setId(rs.getInt(6));
				reference.setURL(rs.getString(5));
				p.setReference(reference);
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
