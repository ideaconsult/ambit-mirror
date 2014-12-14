package ambit2.db.substance.study;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.I5Utils;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.ReliabilityParams;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;

public class ReadSubstanceStudy<PA extends ProtocolApplication<Protocol,String,String,IParams,String>> extends AbstractQuery<String,PA, EQCondition, PA> 
									implements IQueryRetrieval<PA>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1980335091441168568L;
	protected PA record = (PA)new ProtocolApplication<Protocol,String,String,IParams,String>(new Protocol(null));
	private final static String sql = 
		"SELECT document_prefix,hex(document_uuid) u,topcategory,endpointcategory,endpoint,guidance,substance_prefix,hex(substance_uuid) su," +
		"params,interpretation_result,interpretation_criteria,reference,reference_year,reference_owner," +
		"owner_prefix,hex(owner_uuid) ou,idsubstance,hex(rs_prefix),hex(rs_uuid) rsu,owner_name," +
		"reliability,isRobustStudy,isUsedforClassification,isUsedforMSDS,purposeFlag,studyResultType\n" +
		"from substance_protocolapplication p\n" +
		"left join substance s on s.prefix=p.substance_prefix and s.uuid=p.substance_uuid\n"+
		"where substance_prefix =? and substance_uuid = unhex(?) ";

	private final static String whereTopCategory = "\nand topcategory=?";
	private final static String whereCategory = "\nand endpointcategory=?";
	private final static String whereProperty = "\nand document_uuid in (select document_uuid from substance_experiment where endpointhash =unhex(?))";
	@Override
	public String getSQL() throws AmbitException {
		if ((getValue()!=null) && (getValue().getProtocol()!=null)) {
			String wsql = sql;
			if (getValue().getProtocol().getTopCategory()!=null) 
				wsql += whereTopCategory;
			if (getValue().getProtocol().getCategory()!=null) 
				wsql += whereCategory;
			if (getValue().getEffects()!= null && getValue().getEffects().get(0)!=null && getValue().getEffects().get(0).getSampleID()!=null) {
				wsql += whereProperty;
			}
			
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
			if (getValue().getEffects()!= null && getValue().getEffects().get(0)!=null && getValue().getEffects().get(0).getSampleID()!=null) {
				params.add(new QueryParam<String>(String.class, getValue().getEffects().get(0).getSampleID()));
			}
		}
		return params;
	}

	@Override
	public PA getObject(ResultSet rs) throws AmbitException {
		record.clear();
		try {
			Protocol protocol = new Protocol(rs.getString("endpoint"));
			protocol.addGuideline(rs.getString("guidance"));
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
    		try {record.setReferenceYear(Integer.toString(rs.getInt("reference_year")));} catch (Exception x) {record.setReferenceYear(null);}
    		try {record.setReferenceOwner(rs.getString("reference_owner"));} catch (Exception x) {record.setReferenceOwner(null);}
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
			ReliabilityParams reliability = new ReliabilityParams();
			record.setReliability(reliability);
            try { reliability.setValue(rs.getString("reliability"));} catch (Exception x) { }
    		try { reliability.setIsRobustStudy(rs.getBoolean("isRobustStudy")); } catch (Exception x) { }
    		try { reliability.setIsUsedforClassification(rs.getBoolean("isUsedforClassification")); } catch (Exception x) { }
    		try { reliability.setIsUsedforMSDS(rs.getBoolean("isUsedforMSDS"));} catch (Exception x) { }
    		try { reliability.setPurposeFlag(rs.getString("purposeFlag"));} catch (Exception x) { }
    		try { reliability.setStudyResultType(rs.getString("studyResultType"));} catch (Exception x) { }
    		            
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
