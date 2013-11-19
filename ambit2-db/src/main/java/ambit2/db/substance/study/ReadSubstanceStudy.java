package ambit2.db.substance.study;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.I5Utils;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

public class ReadSubstanceStudy extends AbstractQuery<String,ProtocolApplication, EQCondition, ProtocolApplication> implements IQueryRetrieval<ProtocolApplication>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1980335091441168568L;
	protected ProtocolApplication record = new ProtocolApplication(new Protocol(null));
	public final static String sql = 
		"SELECT document_prefix,hex(document_uuid) u,topcategory,endpointcategory,endpoint,guidance,substance_prefix,hex(substance_uuid) su,params,reference from substance_protocolapplication  where substance_prefix =? and hex(substance_uuid) =?";
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()==null) throw new AmbitException("Empty substance id");
		String[] uuid = new String[]{null,getFieldname()};
		uuid = I5Utils.splitI5UUID(getFieldname());
		params.add(new QueryParam<String>(String.class, uuid[0]));
		params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
		return params;
	}

	@Override
	public ProtocolApplication getObject(ResultSet rs) throws AmbitException {
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
            /*
            try {
            	record.setCompanyUUID(rs.getString("substance_prefix") + "-" + I5Utils.addDashes(rs.getString("su").toString().toLowerCase()));
            } catch (Exception xx) {
            	papp.setDocumentUUID(null);
            }
            */
            
    		record.setReference(rs.getString("reference"));
    		record.setParameters(rs.getString("params")); //parse json

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
