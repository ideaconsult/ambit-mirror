package ambit2.db.search.property;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;
import ambit2.db.update.property.ReadProperty;

/**
 * Finds properties by alias
 * @author nina
 *
 */
public class RetrieveFieldNamesByAlias extends AbstractPropertyRetrieval<IStructureRecord, String, StringCondition> {


	
	public static String sql = 
			"select idproperty,name,units,title,url,idreference,comments,null,islocal,type,rdf_type,predicate,object from properties join catalog_references using(idreference)\n"+
			"left join (select idproperty,rdf_type,predicate,object from property_annotation where predicate regexp \"confidenceOf$\") a using(idproperty)\n";
	public static String where = " %s %s %s ?"; // COLLATE utf8_general_ci for case insensitive

		/**
	 * 
	 */
	private static final long serialVersionUID = 8369867048140756850L;
	protected SearchMode searchMode = SearchMode.alias;
	
	public boolean isSearchByAlias() {
		return searchMode==SearchMode.alias;
	}

	public void setSearchByAlias(boolean value) {
		searchMode = value?SearchMode.alias:SearchMode.name;
	}		
	public RetrieveFieldNamesByAlias() {
		this(Property.opentox_Name);
	}
	public RetrieveFieldNamesByAlias(String alias) {
		super();
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setValue(alias);
	}
		public List<QueryParam> getParameters() throws AmbitException {
			List<QueryParam> params = new ArrayList<QueryParam>();
			if (getFieldname() != null)
				params.add(new QueryParam<Integer>(Integer.class, 
						isChemicalsOnly()?getFieldname().getIdchemical():getFieldname().getIdstructure()));				

			if (getValue()!=null) {
				params.add(new QueryParam<String>(String.class, getValue()));				
				return params;
			} 
			return (params.size()==0)?null:params;
			
		}
		public String getSQL() throws AmbitException {
			if (getFieldname() == null)
				return getValue()==null?sql:sql + String.format(where,"where",searchMode.getSQL(),getCondition().getSQL());
			else {
				String sql = isChemicalsOnly()?ReadProperty.sqlPerChemical:ReadProperty.sqlPerStructure;
				return getValue()==null?sql:(sql + String.format(where,"and",searchMode.getSQL(),getCondition().getSQL()));
			}	
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