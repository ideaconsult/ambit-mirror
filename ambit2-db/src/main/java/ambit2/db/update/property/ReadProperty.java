package ambit2.db.update.property;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.EQCondition;
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
		"select properties.idproperty,idproperty,name,units,title,url,idreference,comments,ptype as idtype,islocal,type,rdf_type,predicate,object from properties join catalog_references using(idreference)\n"+
		"left join (select idproperty,rdf_type,predicate,object from property_annotation where predicate regexp \"confidenceOf$\") b using(idproperty)\n"+
		"where idproperty in (select idproperty from property_values where idstructure = ?) \n";
	/*
	public static String sqlPerChemical = 
		"select idproperty,name,units,title,url,idreference,comments,null,islocal,type from properties join catalog_references using(idreference)\n"+
		"where idproperty in (select idproperty from structure join property_values using(idstructure) where idchemical = ?) \n";
	*/
	public static String sqlPerChemical = 
	"select idproperty,properties.name,units,title,url,idreference,comments,ptype as idtype,islocal,type,rdf_type,predicate,object from properties\n"+
	"join catalog_references using(idreference)\n"+
	"left join (select idproperty,rdf_type,predicate,object from property_annotation where predicate regexp \"confidenceOf$\") b using(idproperty)\n"+
	"join (\n"+
	"select idproperty from summary_property_chemicals where idchemical=? group by idchemical,idproperty\n"+
	") a using(idproperty)\n";
	
	public static String propertyWithType = 
		/*
		"select idproperty,properties.name,units,title,url,idreference,comments,idtype,islocal,type from properties\n"+
		"join\n"+
		"(\n"+
		"select idproperty,idtype from property_values\n"+
		"where idproperty= ?\n"+
		"group by idproperty,idtype order by idtype desc limit 1\n"+
		") p using(idproperty)\n"+
		"join catalog_references using(idreference)\n";
		*/
		"select p.idproperty,p.name,units,title,url,idreference,comments,ptype as idtype,islocal,type,rdf_type,predicate,object from properties p\n"+
		"join catalog_references using(idreference)\n"+
		"left join (select idproperty,rdf_type,predicate,object from property_annotation where predicate regexp \"confidenceOf$\") a using(idproperty)\n"+		
		"where p.idproperty=?\n";
		
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
		else if (getValue()!=null)
			params.add(new QueryParam<Integer>(Integer.class, getValue()));
		
		/*
		if (getValue()!=null) {
			params.add(new QueryParam<Integer>(Integer.class, getValue()));
			return params;
		} 
		*/
		return params.size()==0?null:params;
	}
	public String getSQL() throws AmbitException {
		if (getFieldname() == null)
			return getValue()==null?base_sql:propertyWithType;
		else {
			String sql = isChemicalsOnly()?ReadProperty.sqlPerChemical:ReadProperty.sqlPerStructure;
			return getValue()==null?sql:String.format("%s where idproperty=?",sql);
		}	
	}

	@Override
	public String toString() {
		return getValue()==null?"Properties":String.format("Property id=%s",getValue());
	}

}
