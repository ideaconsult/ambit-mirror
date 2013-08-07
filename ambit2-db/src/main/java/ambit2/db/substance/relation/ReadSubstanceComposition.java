package ambit2.db.substance.relation;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

public class ReadSubstanceComposition extends AbstractQuery<STRUCTURE_RELATION,SubstanceRecord, EQCondition, CompositionRelation> implements IQueryRetrieval<CompositionRelation>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1980335091441168568L;
	protected CompositionRelation record = new CompositionRelation(new SubstanceRecord(), new StructureRecord(), new Proportion());
	public final static String sql = 
		"select idsubstance,idchemical,relation,`function`,proportion_typical,proportion_typical_value,proportion_typical_unit,proportion_real_lower,proportion_real_lower_value,proportion_real_upper,proportion_real_upper_value,proportion_real_unit from substance_relation where idsubstance=?";
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getValue()!=null && getValue().getIdsubstance()>0)
			params.add(new QueryParam<Integer>(Integer.class,getValue().getIdsubstance()));
		else throw new AmbitException("Empty ID");
		/*
		if (getFieldname()!=null)
			params.add(new QueryParam<String>(String.class,getFieldname().name()));
		else throw new AmbitException("Relation not specified");
		*/		
		return params;
	}

	@Override
	public CompositionRelation getObject(ResultSet rs) throws AmbitException {
		record.clear();
		try {
			record.getFirstStructure().setIdsubstance(rs.getInt("idsubstance"));
			record.getSecondStructure().setIdchemical(rs.getInt("idchemical"));
			record.setRelationType(STRUCTURE_RELATION.valueOf(rs.getString("relation")));
			record.getRelation().setFunction(rs.getString("function"));
			record.getRelation().setTypical(rs.getString("proportion_typical"));
			record.getRelation().setTypical_unit(rs.getString("proportion_typical_unit"));
			record.getRelation().setTypical_value(rs.getDouble("proportion_typical_value"));
			record.getRelation().setReal_lower(rs.getString("proportion_real_lower"));
			record.getRelation().setReal_upper(rs.getString("proportion_real_upper"));
			record.getRelation().setReal_unit(rs.getString("proportion_real_unit"));
			record.getRelation().setReal_lowervalue(rs.getDouble("proportion_real_lower_value"));
			record.getRelation().setReal_uppervalue(rs.getDouble("proportion_real_upper_value"));
		} catch (Exception x) {
			x.printStackTrace();
		}
		return record;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(CompositionRelation object) {
		return 1;
	}

	

}
