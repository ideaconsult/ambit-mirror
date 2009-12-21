package ambit2.db.update.property;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;
import ambit2.db.search.property.AbstractPropertyRetrieval;

/**
 * Reads {@link Property} by id
 * @author nina
 *
 */
public class ReadProperty extends AbstractPropertyRetrieval<IStructureRecord, Integer, EQCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6247086921731939782L;
	public static String sqlPerStructure = 
		"select idproperty,name,units,title,url,idreference,comments,null from properties join catalog_references using(idreference)\n"+
		"where idproperty in (select idproperty from property_values where idstructure = ?) \n";

	public static String sqlPerChemical = 
		"select idproperty,name,units,title,url,idreference,comments,null from properties join catalog_references using(idreference)\n"+
		"where idproperty in (select idproperty from structure join property_values using(idstructure) where idchemical = ?) \n";

	public ReadProperty(Integer id) {
		setValue(id);
	}
	public ReadProperty() {
		this(null);
	}
		
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()!=null)
			params.add(new QueryParam<Integer>(Integer.class, isChemicalsOnly()?getFieldname().getIdchemical():getFieldname().getIdstructure()));				

		if (getValue()!=null) {
			params.add(new QueryParam<Integer>(Integer.class, getValue()));
			return params;
		} 
		return params.size()==0?null:params;
	}
	public String getSQL() throws AmbitException {
		if (getFieldname() == null)
			return getValue()==null?base_sql:String.format("%s where idproperty=?", base_sql);
		else {
			String sql = isChemicalsOnly()?ReadProperty.sqlPerChemical:ReadProperty.sqlPerStructure;
			return getValue()==null?sql:(sql + "where idproperty=?");
		}	
	}

	@Override
	public String toString() {
		return getValue()==null?"Properties":String.format("Property id=%s",getValue());
	}

}
