package ambit2.db.substance.relation;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.I5Utils;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;

public class ReadSubstanceComposition extends AbstractQuery<CompositionRelation,SubstanceRecord, EQCondition, CompositionRelation> implements IQueryRetrieval<CompositionRelation>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1980335091441168568L;
	protected CompositionRelation record = new CompositionRelation(new SubstanceRecord(), new StructureRecord(), new Proportion());
	private final static String sql = 
		"select cmp_prefix,hex(cmp_uuid) cmp_huuid,r.name as compositionname,idsubstance,idchemical,relation,`function`,proportion_typical,proportion_typical_value,proportion_typical_unit,proportion_real_lower,proportion_real_lower_value,proportion_real_upper,proportion_real_upper_value,proportion_real_unit,r.rs_prefix as refstruc,hex(r.rs_uuid) as refstrucuuid from substance_relation r ";
	
	private static String  q_idsubstance = "idsubstance=?";
	private static String  q_uuid = "prefix=? and uuid=unhex(?)";
	private static String  q_compound = "idcompound=?";
	
	private final static String sql_id = sql + " where " + q_idsubstance;
	private final static String sql_uuid =sql + " join substance using(idsubstance) where " + q_uuid;
	


	
	@Override
	public String getSQL() throws AmbitException {
		if (getValue()!=null) {
			if (getValue().getIdsubstance()>0) {
				return sql_id;
			} else if (getValue().getCompanyUUID()!= null) {
				return sql_uuid;
			}
		}	
		throw new AmbitException("Unspecified substance");
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;
		if (getValue()!=null) {
			if (getValue().getIdsubstance()>0) {
				params = new ArrayList<QueryParam>();
				params.add(new QueryParam<Integer>(Integer.class,getValue().getIdsubstance()));
			} else if (getValue().getCompanyUUID()!= null) {
				String o_uuid = getValue().getCompanyUUID();
				if (o_uuid==null) throw new AmbitException("Empty substance id");
				String[] uuid = new String[]{null,o_uuid==null?null:o_uuid.toString()};
				if (o_uuid!=null) 
					uuid = I5Utils.splitI5UUID(o_uuid.toString());
				params = new ArrayList<QueryParam>();				
				params.add(new QueryParam<String>(String.class, uuid[0]));
				params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
			}
		}
		/*
		if ((params!=null) && (getFieldname()!=null) && getFieldname().)
			
		}
		*/
		if (params==null)
			throw new AmbitException("Empty ID");
		else return params;
	}

	@Override
	public CompositionRelation getObject(ResultSet rs) throws AmbitException {
		record.clear();
		return readCompositionRelation(record,rs);
	}
	

	public static CompositionRelation readCompositionRelation(CompositionRelation record,ResultSet rs) throws AmbitException {
		record.clear();
		try {
            try {
	            String uuid = rs.getString("cmp_prefix") + "-" + 
	            		I5Utils.addDashes(rs.getString("cmp_huuid")).toLowerCase();
	            record.setCompositionUUID(uuid);
            } catch (Exception xx) {
            	record.setCompositionUUID(null);
            }
            record.setName(rs.getString("compositionname"));
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
			
            try {
	            String uuid = rs.getString("refstruc") + "-" + 
	            		I5Utils.addDashes(rs.getString("refstrucuuid")).toLowerCase();
	            record.setProperty(Property.getI5UUIDInstance(),uuid);
            } catch (Exception xx) {
            	record.removeProperty(Property.getI5UUIDInstance());
            }
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
