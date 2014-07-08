package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.EQCondition;
import ambit2.db.search.IStoredQuery;

/**
 * Retrieve structure collections from query table by name and folder (sessions table) 
 * @author nina
 *
 */
public class QueryStoredResultsByName extends AbstractStructureQuery<IStoredQuery, String, EQCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4597129739347497000L;
	public static final String sqlField="select idquery,idchemical,idstructure,selected, %s,text from query_results join query using(idquery) join sessions using(idsessions) where name=? and title=? %s";

	public QueryStoredResultsByName() {
		setCondition(EQCondition.getInstance());
		setId(-1);
	}
	public QueryStoredResultsByName(IStoredQuery query) {
		this();
		setFieldname(query);
		setId(-1);
	}	
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getId() > 0)
			params.add(new QueryParam<Integer>(Integer.class, getId()));		
		if (getFieldname()!=null)
			params.add(new QueryParam<String>(String.class, getFieldname().getName()));
		if (getValue()!=null)
			params.add(new QueryParam<String>(String.class, getValue()));	
		if (params.size()==0) throw new AmbitException("No query or name defined!");
		else return params;
	}

	public String getSQL() throws AmbitException {
		return String.format(sqlField,
				chemicalsOnly?"max(metric) as metric":"metric",
				chemicalsOnly?"group by idchemical":"");
	}
	@Override
	public String toString() {
		return String.format("%s:%s",getValue()==null?"tmp":getValue(),fieldname==null?"":fieldname.getName());
	}
}
