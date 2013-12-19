package ambit2.db.substance.study;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.I5Utils;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

public class ReadSubstanceStudy<PA extends ProtocolApplication<Protocol,String,String,Params,String>> extends AbstractQuery<String,PA, EQCondition, PA> 
									implements IQueryRetrieval<PA>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1980335091441168568L;
	protected PA record = (PA)new ProtocolApplication<Protocol,String,String,Params,String>(new Protocol(null));
	private final static String sql = 
		"SELECT document_prefix,hex(document_uuid) u,topcategory,endpointcategory,endpoint,guidance,substance_prefix,hex(substance_uuid) su," +
		"params,interpretation_result,interpretation_criteria,reference," +
		"owner_prefix,hex(owner_uuid) ou,idsubstance,hex(rs_prefix),hex(rs_uuid) rsu,owner_name from substance_protocolapplication p\n" +
		"left join substance s on s.prefix=p.substance_prefix and s.uuid=p.substance_uuid\n"+
		"where substance_prefix =? and hex(substance_uuid) =? ";


	private final static String whereTopCategory = "\nand topcategory=?";
	private final static String whereCategory = "\nand endpointcategory=?";
	@Override
	public String getSQL() throws AmbitException {
		if ((getValue()!=null) && (getValue().getProtocol()!=null)) {
			String wsql = sql;
			if (getValue().getProtocol().getTopCategory()!=null) 
				wsql += whereTopCategory;
			if (getValue().getProtocol().getCategory()!=null) 
				wsql += whereCategory;
			return wsql;
		} else return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()==null) throw new AmbitException("Empty substance id");
		String[] uuid = new String[]{null,getFieldname()};
		uuid = I5Utils.splitI5UUID(getFieldname());
		params.add(new QueryParam<String>(String.class, uuid[0]));
		params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
		
		if ((getValue()!=null) && (getValue().getProtocol()!=null)) {
			if (getValue().getProtocol().getTopCategory()!=null) 
				params.add(new QueryParam<String>(String.class, getValue().getProtocol().getTopCategory()));
			if (getValue().getProtocol().getCategory()!=null) 
				params.add(new QueryParam<String>(String.class, getValue().getProtocol().getCategory()));				
		}
		return params;
	}

	@Override
	public PA getObject(ResultSet rs) throws AmbitException {
		record.clear();
		try {
			Protocol protocol = new Protocol(rs.getString("endpoint"));
			protocol.addGuidance(rs.getString("guidance"));
			protocol.setCategory(rs.getString("endpointcategory"));
			protocol.setTopCategory(rs.getString("topcategory"));
            record.setProtocol(protocol);
            try {
            	record.setDocumentUUID(rs.getString("document_prefix") + "-" + I5Utils.addDashes(rs.getString("u").toString().toLowerCase()));
            } catch (Exception xx) {
            	record.setDocumentUUID(null);
            }			
            try {
            	record.setSubstanceUUID(rs.getString("substance_prefix") + "-" + I5Utils.addDashes(rs.getString("su").toString().toLowerCase()));
            } catch (Exception xx) {
            	record.setSubstanceUUID(null);
            }
            
    		record.setReference(rs.getString("reference"));
    		record.setParameters(rs.getString("params")); //parse json
    		record.setInterpretationCriteria(rs.getString("interpretation_criteria")); //parse json
    		record.setInterpretationResult(rs.getString("interpretation_result")); //parse json
    		
    		record.setCompanyName(rs.getString("owner_name"));
    		try {
            	record.setCompanyUUID(rs.getString("owner_prefix") + "-" + I5Utils.addDashes(rs.getString("ou").toString().toLowerCase()));
            } catch (Exception xx) {
            	record.setCompanyUUID(null);
            }

    		try {
            	record.setReferenceSubstanceUUID(rs.getString("rs_prefix") + "-" + I5Utils.addDashes(rs.getString("rsu").toString().toLowerCase()));
            } catch (Exception xx) {
            	record.setReferenceSubstanceUUID(null);
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
	public double calculateMetric(ProtocolApplication object) {
		return 1;
	}

	

}
