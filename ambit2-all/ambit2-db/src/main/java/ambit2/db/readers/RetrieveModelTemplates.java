package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;

/**
 * Retrieves property values given a model
 * @author nina
 *
 * @param <ResultType>
 */
public class RetrieveModelTemplates<ResultType> extends AbstractQuery<ModelQueryResults,IStructureRecord,EQCondition,ResultType> 
	implements IQueryRetrieval<ResultType> {	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7375755539942986234L;
	protected boolean chemicalsOnly = true;
	public boolean isChemicalsOnly() {
		return chemicalsOnly;
	}

	public void setChemicalsOnly(boolean chemicalsOnly) {
		this.chemicalsOnly = chemicalsOnly;
	}

	protected String sql_chemicals = 
		"select properties.name,idreference,idproperty,idstructure,ifnull(text,value) as value_string,avg(value_num) as value_num,title,url,idchemical,id,units,idtemplate from property_values \n"+
		"left join property_string using(idvalue_string)\n"+
		"join properties using(idproperty)\n"+
		"join template_def using(idproperty)\n"+
		"join template using(idtemplate)\n"+		
		"join models on template.idtemplate=models.dependent\n"+
		"join catalog_references using(idreference)\n"+
		"where idchemical=? %s group by idproperty";
	protected String sql_structure = 
		"select properties.name,idreference,idproperty,idstructure,ifnull(text,value) as value_string,value_num,title,url,-1,id,units,idtemplate from property_values \n"+
		"left join property_string using(idvalue_string)\n"+
		"join template_def using(idproperty)\n"+
		"join template using(idtemplate)\n"+		
		"join models on template.idtemplate=models.dependent\n"+
		"join properties using(idproperty)\n"+
		"join catalog_references using(idreference)\n"+
		"where idstructure=? %s";
	protected String sql_where = "and idmodel=?";
	protected String sql_templateall = "";
	public double calculateMetric(ResultType object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) throw new AmbitException("Structure not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, isChemicalsOnly()?getValue().getIdchemical():getValue().getIdstructure()));

		if (getFieldname()!=null)
			params.add(new QueryParam<Integer>(Integer.class, getFieldname().getId()));		
		return params;	
	}

	public String getSQL() throws AmbitException {
		if (getValue()==null) throw new AmbitException("Structure not defined");
		return String.format(
				isChemicalsOnly()?sql_chemicals:sql_structure,
				getFieldname()==null?sql_templateall:sql_where
				);
	}

	public ResultType getObject(ResultSet rs) throws AmbitException {
		try {
			Object value = rs.getObject(5);
			if (value == null) value = rs.getFloat(6);
			return (ResultType)value;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
}
