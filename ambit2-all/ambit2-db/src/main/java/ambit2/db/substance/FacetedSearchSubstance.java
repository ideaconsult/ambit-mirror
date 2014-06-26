package ambit2.db.substance;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.Protocol;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;


public class FacetedSearchSubstance  extends AbstractReadSubstance<List<Protocol>,String> {

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
	private static final String where_category = "e%d.topcategory=? and e%d.endpointcategory = ?\n";
	private static final String join_category  = "join substance_protocolapplication e%d on (`e1`.`substance_prefix` = `e%d`.`substance_prefix`) and (`e1`.`substance_uuid` = `e%d`.`substance_uuid`)\n"; 	
	@Override
	public String getSQL() throws AmbitException {
		if (getFieldname()==null || getFieldname().size()==0) throw new AmbitException();
		StringBuilder b = null;
		StringBuilder where = new StringBuilder();
		for (int i=0; i < getFieldname().size(); i++) {
			if (b==null) {
				b = new StringBuilder();
				b.append(subselect);
				where.append(String.format(where_category,(i+1),(i+1)));
			}  else { 
				b.append(String.format(join_category,(i+1),(i+1),(i+1)));
				where.append("and ");
				where.append(String.format(where_category,(i+1),(i+1)));
			}	
			
		}
		String sql = String.format(sql_byprotocolcategory,b,where);
		logger.fine(sql);
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null || getFieldname().size()==0) throw new AmbitException();
		List<QueryParam> params = new ArrayList<QueryParam>();
		for (int i=0; i < getFieldname().size(); i++) {
			Protocol p = getFieldname().get(i);
			params.add(new QueryParam<String>(String.class,p.getTopCategory()));
			params.add(new QueryParam<String>(String.class,p.getCategory()));
		}
		return params;
	}

	@Override
	protected SubstanceRecord getRecord() {
		return record;
	}
}
