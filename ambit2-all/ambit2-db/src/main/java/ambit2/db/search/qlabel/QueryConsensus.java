package ambit2.db.search.qlabel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.QLabel;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.StringCondition;

public class QueryConsensus extends AbstractQuery<IStructureRecord, String, StringCondition, String> implements IQueryRetrieval<String> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6574002693997936309L;
	protected static String sql_structure = "select q.label,text from structure left join quality_chemicals q using(idstructure) where idstructure = ? %s";
	protected static String sql_chemical = "select q.label,text from quality_chemicals q where idchemical = ? %s";
	protected static String sql_where = "and quality_chemicals.label %s ?";
	
	public QueryConsensus() {
		this(null,null);
	}
	public QueryConsensus(IStructureRecord record, String label) {
		setFieldname(record);
		setValue(label);
		setCondition(StringCondition.getInstance("="));
	}
	
	public String getSQL() throws AmbitException {
		if (getFieldname()==null) throw new AmbitException("Structure not defined");
		if (getFieldname().getIdchemical()>0) 
			return String.format(sql_chemical,getValue()==null?"":sql_where,getCondition().getSQL());
		else if (getFieldname().getIdstructure()>0) return String.format(sql_structure,getValue()==null?"":sql_where,getCondition().getSQL());
		else throw new AmbitException("Structure not defined");
	}
	
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null) throw new AmbitException("Structure not defined");
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname().getIdchemical()>0) params.add(new QueryParam<Integer>(Integer.class, getFieldname().getIdchemical()));
		else if (getFieldname().getIdstructure()>0) params.add(new QueryParam<Integer>(Integer.class, getFieldname().getIdstructure()));
		else throw new AmbitException("Structure not defined");

		if (getValue() != null) 
			params.add(new QueryParam<String>(String.class, getValue()));		
		return params;		
	}



	public double calculateMetric(QLabel object) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isPrescreen() {
		return false;
	}

	public String getObject(ResultSet rs) throws AmbitException {
		try {
			StringBuilder b = new StringBuilder();
			if (rs.getString(1)!=null) b.append(rs.getString(1));
			if (rs.getString(2)!=null) { b.append('['); b.append(rs.getString(2)); b.append(']');}
			return b.toString();
		} catch (SQLException x) {
			return null;
		}
	}
	@Override
	public String toString() {
		return "Quality labels";
	}
	public double calculateMetric(String object) {
		return 1;
	}
}
