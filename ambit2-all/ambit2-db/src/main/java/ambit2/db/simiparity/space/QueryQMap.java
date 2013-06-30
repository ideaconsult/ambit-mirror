package ambit2.db.simiparity.space;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;
import ambit2.db.search.structure.AbstractStructureQuery;

public class QueryQMap extends AbstractStructureQuery<String,Integer,NumberCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8329798753353233477L;
	public final static String sql = 
		"select idsasmap,idchemical,-1,1,g2 as metric,null as text,a,b,c,d,fisher from qsasmap4 where idsasmap=? order by g2 desc ";
	public QueryQMap(SourceDataset dataset) {
		this(dataset==null?null:dataset.getId());
	}
	public QueryQMap() {
		this((SourceDataset)null);
	}
	public QueryQMap(Integer id) {
		super();
		setFieldname("metric");
		setValue(id);
		setCondition(NumberCondition.getInstance("="));
	}	
	public String getSQL() throws AmbitException {
		return 	String.format(sql,getCondition().getSQL(),getValue()==null?"":"?");
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getValue()!=null)
			params.add(new QueryParam<Integer>(Integer.class,getValue()));
		else throw new AmbitException("Empty ID");
		return params;
	}
	@Override
	public String toString() {
		if (getValue()==null) return "QSASMap";
		return String.format("QSASMap %d",getValue());
	}

	@Override
	public boolean isPrescreen() {
		return true;
	}
	@Override
	public double calculateMetric(IStructureRecord object) {

		return 1;
	}
	@Override
	protected void retrieveMetric(IStructureRecord record, ResultSet rs) throws SQLException {
		record.setProperty(Property.getInstance("metric",toString(),"http://ambit.sourceforge.net"), retrieveValue(rs));
		record.setProperty(Property.getInstance("a",toString(),"http://ambit.sourceforge.net"), rs.getFloat(7));
		record.setProperty(Property.getInstance("b",toString(),"http://ambit.sourceforge.net"), rs.getFloat(8));
		record.setProperty(Property.getInstance("c",toString(),"http://ambit.sourceforge.net"), rs.getFloat(9));
		record.setProperty(Property.getInstance("d",toString(),"http://ambit.sourceforge.net"), rs.getFloat(10));
		record.setProperty(Property.getInstance("fisher",toString(),"http://ambit.sourceforge.net"), rs.getFloat(11));
	}	
}