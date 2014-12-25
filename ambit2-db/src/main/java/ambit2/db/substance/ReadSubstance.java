package ambit2.db.substance;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.I5Utils;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;
import ambit2.db.substance.relation.ReadSubstanceComposition;

public class ReadSubstance  extends AbstractReadSubstance<CompositionRelation,SubstanceRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3661558183996204387L;
	private static String sql = "select idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name from substance\n";
	private static String  q_idsubstance = "idsubstance=?";
	private static String  q_uuid = "prefix=? and uuid=unhex(?)";
	protected boolean retrieveComposition = false;
	private static String sql_relatedsubstances = 
		 
		//r.name,r.cmp_uuid,r.function,relation,
		"select idsubstance,prefix,hex(uuid) as huuid,documentType,format,s.name,publicname,content,substanceType,\n"+
		"s.rs_prefix,hex(s.rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name,\n" +
		"r.cmp_prefix,hex(r.cmp_uuid) cmp_huuid,r.name as compositionname,r.idchemical,r.relation,r.`function`,proportion_typical,proportion_typical_value,proportion_typical_unit,proportion_real_lower,proportion_real_lower_value,proportion_real_upper,proportion_real_upper_value,proportion_real_unit,r.rs_prefix as refstruc,hex(r.rs_uuid) as refstrucuuid\n"+
		"from substance s join substance_relation r using(idsubstance) where idchemical=?";
	//"select cmp_prefix,hex(cmp_uuid) cmp_huuid,r.name as compositionname,idsubstance,idchemical,relation,`function`,proportion_typical,proportion_typical_value,proportion_typical_unit,proportion_real_lower,proportion_real_lower_value,proportion_real_upper,proportion_real_upper_value,proportion_real_unit,r.rs_prefix,hex(r.rs_uuid) from substance_relation r ";
	/*
		"select idsubstance,prefix,hex(uuid) as huuid,documentType,format,substance.name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name\n"+ 
		"from substance	where idsubstance in (select distinct(idsubstance) from substance_relation where idchemical = ?)";
		*/
	
	private static String sql_bychemical =
		"select distinct(substance.idsubstance),prefix,hex(uuid) as huuid,documentType,format,substance.name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "+ 
		"from substance join substance_relation using(rs_prefix,rs_uuid) where rs_prefix is not null and rs_uuid is not null and idchemical = ?";
	

	public ReadSubstance() {
		super();
	}
	public ReadSubstance(SubstanceRecord record) {
		super();
		setValue(record);
	}
	public ReadSubstance(CompositionRelation composition) {
		super();
		setFieldname(composition);
	}
	@Override
	public String getSQL() throws AmbitException {
		if (getValue()!=null) {
			if (getValue().getIdsubstance()>0) {
				StringBuilder b = new StringBuilder();
				b.append(sql);
				b.append("where ");
				b.append(q_idsubstance);
				return b.toString();
			} else if (getValue().getCompanyUUID()!= null) {
				StringBuilder b = new StringBuilder();
				b.append(sql);
				b.append("where ");
				b.append(q_uuid);
				return b.toString();
			} else if (getValue().getIdchemical()>0) { 
				return sql_bychemical;
			} else
				throw new AmbitException("Unspecified substance");
		} else if (getFieldname()!=null && getFieldname().getSecondStructure()!=null && getFieldname().getSecondStructure().getIdchemical()>0) {
			retrieveComposition = true;
			return sql_relatedsubstances;		
		} else {
			return sql;
		}
	}
	@Override
	public SubstanceRecord getObject(ResultSet rs) throws AmbitException {
		SubstanceRecord r =  super.getObject(rs);
		if (retrieveComposition) {
			r.addStructureRelation(ReadSubstanceComposition.readCompositionRelation(
					new CompositionRelation(new SubstanceRecord(), new StructureRecord(), new Proportion()), rs,null));
		}
		return r;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getValue()!=null) {
			if (getValue().getIdsubstance()>0) {
				params1.add(new QueryParam<Integer>(Integer.class, getValue().getIdsubstance()));
				return params1;
			} else if (getValue().getCompanyUUID()!= null) {
				String o_uuid = getValue().getCompanyUUID();
				if (o_uuid==null) throw new AmbitException("Empty substance id");
				String[] uuid = new String[]{null,o_uuid==null?null:o_uuid.toString()};
				if (o_uuid!=null) 
					uuid = I5Utils.splitI5UUID(o_uuid.toString());
				params1.add(new QueryParam<String>(String.class, uuid[0]));
				params1.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
				return params1;
			} else if (getValue().getIdchemical()>0) {
				params1.add(new QueryParam<Integer>(Integer.class, getValue().getIdchemical()));
				return params1;
			} else
				throw new AmbitException("Unspecified substance");
		} else if (getFieldname()!=null && getFieldname().getSecondStructure()!=null && getFieldname().getSecondStructure().getIdchemical()>0) {
			params1.add(new QueryParam<Integer>(Integer.class, getFieldname().getSecondStructure().getIdchemical()));
			return params1;			
		} else return null;
				
	}
	@Override
	protected SubstanceRecord getRecord() {
		return (getValue()==null)?new SubstanceRecord():getValue();
	}

}
