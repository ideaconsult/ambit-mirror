package ambit2.db.substance.relation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;
import ambit2.db.search.structure.AbstractStructureQuery;

/**
 * Reads all structures, related to the query substance via substance_relation table
 * @author nina
 *
 */
public class ReadSubstanceRelation extends AbstractStructureQuery<STRUCTURE_RELATION,SubstanceRecord,NumberCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8329798753353233477L;
	public final static String sql = 
		"select idsubstance,idchemical,-1,1,proportion_typical_value as metric,relation as text,proportion_real_lower,proportion_real_lower_value," +
		"proportion_real_upper,proportion_real_upper_value,proportion_real_unit,proportion_typical,proportion_typical_unit\n"+
		"from substance_relation where idsubstance=? and relation=?";
	
	public ReadSubstanceRelation(SubstanceRecord structure) {
		this(STRUCTURE_RELATION.HAS_CONSTITUENT,structure);
	}
	public ReadSubstanceRelation() {
		this((SubstanceRecord)null);
	}
	public ReadSubstanceRelation(STRUCTURE_RELATION relation, SubstanceRecord id) {
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
		if (getValue()!=null && getValue().getIdsubstance()>0)
			params.add(new QueryParam<Integer>(Integer.class,getValue().getIdsubstance()));
		else throw new AmbitException("Empty ID");
		if (getFieldname()!=null)
			params.add(new QueryParam<String>(String.class,getFieldname().name()));
		else throw new AmbitException("Relation not specified");		
		return params;
	}
	@Override
	public String toString() {
		if (getValue()==null) return getFieldname()==null?"Related structures":getFieldname().name();
		else return String.format("%s of /substance/%d",getFieldname()==null?"":getFieldname().name(),getValue().getIdsubstance());
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