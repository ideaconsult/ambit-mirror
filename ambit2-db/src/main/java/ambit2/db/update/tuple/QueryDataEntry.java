package ambit2.db.update.tuple;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.AbstractStructureQuery;

public class QueryDataEntry extends AbstractStructureQuery<IStructureRecord,SourceDataset,NumberCondition>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8296523661478128987L;
	protected static String sql = 
		"select ? as idquery,idchemical,idstructure,1 as selected,idtuple as metric,id_srcdataset as text\n"+
		"FROM tuples\n"+
		"join property_tuples using(idtuple)\n"+
		"join property_values using(id)\n"+
		//"left join property_string using(idvalue_string)\n"+
		"%s\n"+
		"group by idtuple\n"+
		"order by idtuple";	
	/*
	protected static String sql_values = 
		"select ? as idquery,idchemical,idstructure,1 as selected,idtuple as metric,id_srcdataset as text\n"+
		",idproperty\n"+
		"FROM tuples\n"+
		"join property_tuples using(idtuple)\n"+
		"join property_values using(id)\n"+
		"left join property_string using(idvalue_string)\n"+
		"where\n"+
		"%s\n"+
		"%s\n"+
		"%s\n"+
		"%s"+
		"order by idtuple";	
	*/
	protected enum where {
		idtuple,
		idchemical,
		idstructure,
		id_srcdataset;

		public String toSQL(String condition,String values) {
			return String.format("%s %s %s\n",toString(),condition,values);
		}		
		public String toSQL() {
			return String.format("%s = ?\n",toString());
		}
	};

	
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		//structure
		if (getFieldname()!=null) {
			//idtuple
			if (getFieldname().getDataEntryID()>0) 
				params.add(new QueryParam<Integer>(Integer.class, getFieldname().getDataEntryID()));
			//idchemical
			if (getFieldname().getIdchemical()>0) 
				params.add(new QueryParam<Integer>(Integer.class, getFieldname().getIdchemical()));
			//idstructure
			if (getFieldname().getIdstructure()>0) 
				params.add(new QueryParam<Integer>(Integer.class, getFieldname().getIdstructure()));			
		}
		//dataset
		if ((getValue()!=null) && (getValue().getId()>0))
			params.add(new QueryParam<Integer>(Integer.class, getValue().getId()));

		if (params.size()>1)	return params;
		else throw new AmbitException("No search criteria set!");
	}

	@Override
	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		String condition = "where ";
		//structure
		if (getFieldname()!=null) {
			//idtuple
			if (getFieldname().getDataEntryID()>0) { 
				b.append(condition);
				b.append(where.idtuple.toSQL());
				condition = "and ";
			}	
			//idchemical
			if (getFieldname().getIdchemical()>0) {
				b.append(condition);
				b.append(where.idchemical.toSQL());		
				condition = "and ";
			}
			//idstructure
			if (getFieldname().getIdstructure()>0) {
				b.append(condition);
				b.append(where.idstructure.toSQL());	
				condition = "and ";
			}
		}
		//dataset
		if ((getValue()!=null) && (getValue().getId()>0)) {
			b.append(condition);
			b.append(where.id_srcdataset.toSQL());	
		}
		
		return String.format(sql,b.toString());
				
	}
	@Override
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		return super.getObject(rs);
	}
	@Override
	protected void retrieveMetric(IStructureRecord record, ResultSet rs)
			throws SQLException {
		try {
			record.setDataEntryID(rs.getInt(5));
		} catch (Exception x) {
			record.setDataEntryID(-1);
		}
	}

}
