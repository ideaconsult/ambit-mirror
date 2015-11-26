package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.property.AbstractPropertyRetrieval.SearchMode;

public abstract class QueryFieldAbstract<T, C extends IQueryCondition, NC extends IQueryCondition> extends
	AbstractStructureQuery<Property, T, C> {
    /**
     * 
     */
    private static final long serialVersionUID = -8153311734801503300L;
    protected boolean retrieveProperties = false;
    protected boolean caseSensitive = true;

    public boolean isCaseSensitive() {
	return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
	this.caseSensitive = caseSensitive;
    }

    public boolean isRetrieveProperties() {
	return retrieveProperties;
    }

    public void setRetrieveProperties(boolean retrieveProperties) {
	this.retrieveProperties = retrieveProperties;
    }

    protected static String queryField = "%s %s ? and"; // name namecondition
							// value
    // protected static String queryValueCaseSensitive =
    // "value %s convert(? using utf8 ) collate utf8_bin ";
    // "value %s ?";
    // protected static String queryValueCaseInsensitive =
    // "value %s convert(? using utf8 ) collate utf8_general_ci ";

    protected static String queryValueCaseSensitive = "value %s ? ";
    // "value %s ?";
    protected static String queryValueCaseInsensitive = "value_ci %s ? ";

    // "lower(value) %s lower(?)";

    public final static String sqlField = "select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text\n"
	    + "FROM structure s1\n"
	    + "LEFT JOIN structure s2 ON s1.idchemical = s2.idchemical  AND (1E10*s1.preference+s1.idstructure) > (1E10*s2.preference+s2.idstructure)\n"
	    + "join (\n"
	    + "select distinct(structure.idchemical)\n"
	    + "from structure\n"
	    + "join  property_values using(idstructure)\n"
	    + "join property_string using (idvalue_string)\n"
	    + "join properties using(idproperty)\n"
	    + "where %s %s\n"
	    + ") a on a.idchemical=s1.idchemical\n"
	    + "where s2.idchemical is null\n";

    public final static String sqlField_ci = "select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text\n"
	    + "FROM structure s1\n"
	    + "LEFT JOIN structure s2 ON s1.idchemical = s2.idchemical  AND (1E10*s1.preference+s1.idstructure) > (1E10*s2.preference+s2.idstructure)\n"
	    + "join (\n"
	    + "select distinct(idchemical)\n"
	    + "from summary_property_chemicals\n"
	    + "join property_ci using (id_ci)\n"
	    + "join properties using(idproperty)\n"
	    + "where %s %s\n"
	    + ") a on a.idchemical=s1.idchemical\n" + "where s2.idchemical is null\n";

    /**
     * This is faster than the one below, but will work if the summary table
     * exists and for case insensitive searches only
     */
    public final static String sqlFieldProperties = "select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text,-1 as idproperty,name,comments,value\n"
	    + "FROM structure s1\n"
	    + "LEFT JOIN structure s2 ON s1.idchemical = s2.idchemical  AND (1E10*s1.preference+s1.idstructure) > (1E10*s2.preference+s2.idstructure)\n"
	    + "join (\n"
	    + "select idchemical,group_concat(distinct value_ci SEPARATOR ';') as value,group_concat(distinct name) as name,group_concat(distinct comments) as comments\n"
	    + "from summary_property_chemicals\n"
	    + "join properties using(idproperty)\n"
	    + "join property_ci using (id_ci)\n"
	    + "where %s %s \n"
	    + "group by idchemical\n"
	    + ") a on a.idchemical=s1.idchemical\n" + "where s2.idchemical is null\n";

    /*
     * public final static String sqlFieldProperties =
     * "select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text,-1 as idproperty,name,comments,value\n"
     * + "FROM structure s1\n"+
     * "LEFT JOIN structure s2 ON s1.idchemical = s2.idchemical  AND (1E10*s1.preference+s1.idstructure) > (1E10*s2.preference+s2.idstructure)\n"
     * + "join (\n"+
     * "select idchemical,group_concat(distinct value SEPARATOR ';') as value,group_concat(distinct name) as name,group_concat(distinct comments) as comments\n"
     * + "from structure\n"+ "join  property_values using(idstructure)\n"+
     * //"join property_string using (idvalue_string)\n"+
     * "join properties using(idproperty)\n"+
     * "join (select value,idvalue_string from property_string where %s %s) x using (idvalue_string)\n"
     * + //"where %s %s\n"+ "group by idchemical\n"+
     * ") a on a.idchemical=s1.idchemical\n"+ "where s2.idchemical is null\n";
     */

    protected NC nameCondition;
    protected SearchMode searchMode = SearchMode.name;

    public NC getNameCondition() {
	return nameCondition;
    }

    public void setNameCondition(NC nameCondition) {
	this.nameCondition = nameCondition;
    }

    public boolean isSearchByAlias() {
	return searchMode == SearchMode.alias;
    }

    public void setSearchByAlias(boolean value) {
	searchMode = value ? SearchMode.alias : SearchMode.name;
    }

    // idproperty,name,comments,value
    // 7,8,9,10
    public IStructureRecord getObject(ResultSet rs) throws AmbitException {

	try {
	    IStructureRecord record = super.getObject(rs);
	    if (isRetrieveProperties() && (rs.getMetaData().getColumnCount() > 6)) {
		Property p = Property.getInstance(rs.getString(8), "", "");
		p.setId(rs.getInt(7));
		p.setUnits(rs.getString(3));
		p.setLabel(rs.getString(9));
		record.setRecordProperty(p, rs.getString(10));
	    }
	    return record;
	} catch (SQLException x) {
	    throw new AmbitException(x);
	}
    }
}
