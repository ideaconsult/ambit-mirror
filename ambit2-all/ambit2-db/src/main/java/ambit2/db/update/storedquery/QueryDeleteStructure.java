package ambit2.db.update.storedquery;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.IStoredQuery;

public class QueryDeleteStructure extends AbstractUpdate<IStoredQuery,IStructureRecord> {

	
	public static final String[] delete_sql = {
		"delete d from query_results d where d.idstructure=? and idquery=?"
	};
	public static final String[] delete_sql_chemical = {
		"delete d from query_results d , structure t where idchemical=? and idquery=? and d.idstructure=t.idstructure"
	};	

	public QueryDeleteStructure(IStoredQuery dataset,IStructureRecord record) {
		super();
		setGroup(dataset);
		setObject(record);
	}
	public QueryDeleteStructure() {
		this(null,null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getGroup()==null || getGroup().getId()<=0) throw new AmbitException("Dataset not defined!");

		if (getObject()==null) throw new AmbitException("Structure not defined!");
		
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getObject().getIdstructure()<=0) {
			if (getObject().getIdchemical()<=0) 
				throw new AmbitException("Compound not defined!");
			else params1.add(new QueryParam<Integer>(Integer.class, getObject().getIdchemical()));
		} else params1.add(new QueryParam<Integer>(Integer.class, getObject().getIdstructure()));
		params1.add(new QueryParam<Integer>(Integer.class, getGroup().getId()));		
	
		return params1;
		
	}
	public void setID(int index, int id) {
	
	}

	public String[] getSQL() throws AmbitException {
		if (getObject().getIdstructure()<=0) 
			if (getObject().getIdchemical()<=0)
				throw new AmbitException("Compound not defined!");
			else return delete_sql_chemical;
		else return delete_sql;
	}	

}
