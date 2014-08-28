package ambit2.db.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;


public class FacetedSearchSubstance  extends AbstractReadSubstance<List<ProtocolApplication<Protocol, Params, String, Params,String>>,String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9142126306356090595L;
	private SubstanceRecord record = new SubstanceRecord();
	
	
			/*
		by endpoints 
		select s.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name  from substance  s
		where s.uuid in 
		(
		SELECT e1.substance_uuid uuid FROM 
		substance_experiment e1
		join substance_experiment e2
		on (`e1`.`substance_prefix` = `e2`.`substance_prefix`) and (`e1`.`substance_uuid` = `e2`.`substance_uuid`)
		join substance_experiment e3
		on (`e1`.`substance_prefix` = `e3`.`substance_prefix`) and (`e1`.`substance_uuid` = `e3`.`substance_uuid`)
		where 
		(e1.topcategory="ECOTOX" and e1.endpointcategory = "EC_ALGAETOX_SECTION" 
		 and e1.endpoint = "NOEC" and e1.lovalue between 2 and 2.5
		)
		and
		(e2.topcategory="P-CHEM" and e2.endpointcategory = "PC_BOILING_SECTION" 
		 and e2.endpoint = "Boiling point" and e2.lovalue > 0
		)
		and
		(e3.topcategory="TOX" and e3.endpointcategory = "TO_REPEATED_ORAL_SECTION" 
		 and e3.endpoint = "NOAEL" and e3.lovalue > 10
		)

		) 
			 */

	/*
	by study
	select s.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name  from substance  s
	where s.uuid in 
	(
	SELECT e1.substance_uuid uuid FROM substance_protocolapplication e1
	join substance_protocolapplication e2 on (`e1`.`substance_prefix` = `e2`.`substance_prefix`) and (`e1`.`substance_uuid` = `e2`.`substance_uuid`)
	join substance_experiment e3 on (`e1`.`substance_prefix` = `e3`.`substance_prefix`) and (`e1`.`substance_uuid` = `e3`.`substance_uuid`)
	where (e1.topcategory="ECOTOX" and e1.endpointcategory = "EC_ALGAETOX_SECTION")
	and (e2.topcategory="P-CHEM" and e2.endpointcategory = "PC_BOILING_SECTION")
	and (e3.topcategory="TOX" and e3.endpointcategory = "TO_REPEATED_ORAL_SECTION")
	) 
	 */	
	private static final String sql_byprotocolcategory =
		"select substance.idsubstance,prefix,hex(uuid) as huuid,documentType,format,name,publicname,content,substanceType,rs_prefix,hex(rs_uuid) as rs_huuid,owner_prefix,hex(owner_uuid) as owner_huuid,owner_name "+ 
		"from substance where uuid in (%s where %s)";
	private static final String subselect = "SELECT e1.substance_uuid uuid FROM substance_protocolapplication e1\n";
	private static final String subselect_experiment = "SELECT e1.substance_uuid uuid FROM substance_experiment e1\n";
	
	private static final String where_category = "e%d.topcategory=? and e%d.endpointcategory = ?\n";
	private static final String where_endpoint = " and e%d.endpoint=?\n";
	
	private static final String where_endpoint_lovalue = " and (e%d.lovalue %s ? || e%d.upvalue %s ?)\n";
	private static final String where_endpoint_upvalue = " and (e%d.lovalue %s ? || e%d.upvalue %s ?)\n";
	private static final String where_endpoint_textvalue = " and e%d.textvalue=?\n";
	private static final String where_units = " and e%d.unit=?\n";
	
	private static final String where_interpretationresult = " and e%d.interpretation_result=?\n";
	
	private static final String join_category  = "join substance_protocolapplication e%d on (`e1`.`substance_prefix` = `e%d`.`substance_prefix`) and (`e1`.`substance_uuid` = `e%d`.`substance_uuid`)\n";
	private static final String join_experiment  = "join substance_experiment e%d on (`e1`.`substance_prefix` = `e%d`.`substance_prefix`) and (`e1`.`substance_uuid` = `e%d`.`substance_uuid`)\n";
	@Override
	public String getSQL() throws AmbitException {
		if (getFieldname()==null || getFieldname().size()==0) throw new AmbitException();
		StringBuilder b = null;
		StringBuilder where = new StringBuilder();
		for (int i=0; i < getFieldname().size(); i++) {
			boolean endpoint = false;
			if (b==null) {
				where.append(String.format(where_category,(i+1),(i+1)));
				if (getFieldname().get(i).getEffects()!=null)
					endpoint = addEndpointValueSQL(getFieldname().get(i), where, i+1);
				else if (getFieldname().get(i).getInterpretationResult()!=null) {
					where.append(String.format(where_interpretationresult,(i+1)));
					endpoint = false;
				}
				b = new StringBuilder();
				if (endpoint)
					b.append(subselect_experiment);
				else {
					b.append(subselect);
				}	
				
			}  else { 
				where.append("and ");
				where.append(String.format(where_category,(i+1),(i+1)));
				if (getFieldname().get(i).getEffects()!=null)
					endpoint = addEndpointValueSQL(getFieldname().get(i), where, i+1);
				else if (getFieldname().get(i).getInterpretationResult()!=null) {
					where.append(String.format(where_interpretationresult,(i+1)));
					endpoint = false;
				}				
				if (endpoint)
					b.append(String.format(join_experiment,(i+1),(i+1),(i+1)));
				else {
					b.append(String.format(join_category,(i+1),(i+1),(i+1)));
				}
			}	
			
		}
		String sql = String.format(sql_byprotocolcategory,b,where);
		logger.fine(sql);
		return sql;
	}

	private boolean addEndpointValueSQL(ProtocolApplication<Protocol, Params, String, Params,String> papp, StringBuilder where, int index) {
		boolean added = false;
		for (int j=0; j < papp.getEffects().size(); j++) {
			EffectRecord<String,Params,String> effect = papp.getEffects().get(j);
			if (effect.getEndpoint()!=null) {
				where.append(String.format(where_endpoint,index));
				added = true;
			}
			if (effect.getUnit()!=null) {
				where.append(String.format(where_units,index));
				added = true;
			}
			if (effect.getLoValue()!=null) {
				where.append(String.format(where_endpoint_lovalue,
						index,effect.getLoQualifier()==null?">=":effect.getLoQualifier(),
						index,effect.getLoQualifier()==null?">=":effect.getLoQualifier()								
								));
				added = true;
			}	
			if (effect.getUpValue()!=null) {
				where.append(String.format(where_endpoint_upvalue,
						index,effect.getUpQualifier()==null?"<=":effect.getUpQualifier(),
						index,effect.getUpQualifier()==null?"<=":effect.getUpQualifier()								
								));
				added = true;
			}		
			if (effect.getTextValue() !=null) {
				where.append(String.format(where_endpoint_textvalue,index));
				added = true;
			}
		}
		return added;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null || getFieldname().size()==0) throw new AmbitException();
		List<QueryParam> params = new ArrayList<QueryParam>();
		for (int i=0; i < getFieldname().size(); i++) {
			Protocol p = getFieldname().get(i).getProtocol();
			params.add(new QueryParam<String>(String.class,p.getTopCategory()));
			params.add(new QueryParam<String>(String.class,p.getCategory()));
			if (getFieldname().get(i).getEffects()!=null) {
				for (int j=0; j < getFieldname().get(i).getEffects().size(); j++) {
					EffectRecord<String,Params,String> effect = getFieldname().get(i).getEffects().get(j);
					if (effect.getEndpoint()!=null) {
						params.add(new QueryParam<String>(String.class,effect.getEndpoint()));		
					}
					if (effect.getUnit()!=null) {
						params.add(new QueryParam<String>(String.class,effect.getUnit()));		
					}
					if (effect.getLoValue()!=null) {
						params.add(new QueryParam<Double>(Double.class,effect.getLoValue()));
						params.add(new QueryParam<Double>(Double.class,effect.getLoValue()));
					}
					if (effect.getUpValue()!=null) {
						params.add(new QueryParam<Double>(Double.class,effect.getUpValue()));
						params.add(new QueryParam<Double>(Double.class,effect.getUpValue()));
					}					
					if (effect.getTextValue() !=null) {
						params.add(new QueryParam<String>(String.class,effect.getTextValue().toString()));
					}
				}
			} else if (getFieldname().get(i).getInterpretationResult() !=null) {
				params.add(new QueryParam<String>(String.class,getFieldname().get(i).getInterpretationResult()));
			}
		}
		return params;
	}

	@Override
	protected SubstanceRecord getRecord() {
		return record;
	}
}
