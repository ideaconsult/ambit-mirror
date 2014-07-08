package ambit2.db.chemrelation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.AbstractStructureQuery;

public class ReadStructureRelation extends AbstractStructureQuery<String,Integer,NumberCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8329798753353233477L;
	public final static String sql = 
		"select idchemical1,idchemical2,-1,1,metric,relation as text from chem_relation where idchemical1=? and relation=?";
	
	public ReadStructureRelation(IStructureRecord structure) {
		this(STRUCTURE_RELATION.HAS_TAUTOMER.name(),
				structure==null?null:structure.getIdchemical());
	}
	public ReadStructureRelation() {
		this((IStructureRecord)null);
	}
	public ReadStructureRelation(String relation, Integer id) {
		super();
		setFieldname(relation);
		setValue(id);
		setCondition(NumberCondition.getInstance("="));
	}	
	public String getSQL() throws AmbitException {
		return 	sql;
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getValue()!=null)
			params.add(new QueryParam<Integer>(Integer.class,getValue()));
		else throw new AmbitException("Empty ID");
		if (getFieldname()!=null)
			params.add(new QueryParam<String>(String.class,getFieldname()));
		else throw new AmbitException("Relation not specified");		
		return params;
	}
	@Override
	public String toString() {
		return String.format("%s of /compound/%d",getFieldname()==null?"":getFieldname(),getValue());
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}
	@Override
	public double calculateMetric(IStructureRecord object) {
		return 1;
	}
	@Override
	protected void retrieveMetric(IStructureRecord record, ResultSet rs) throws SQLException {
		record.setProperty(Property.getInstance("metric",toString(),"http://ambit.sourceforge.net"), retrieveValue(rs));
	}	
}