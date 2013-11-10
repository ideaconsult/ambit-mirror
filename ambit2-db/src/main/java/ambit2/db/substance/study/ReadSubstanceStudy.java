package ambit2.db.substance.study;

import java.sql.ResultSet;
import java.util.List;

import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

public class ReadSubstanceStudy extends AbstractQuery<String,ProtocolApplication, EQCondition, SubstanceRecord> implements IQueryRetrieval<SubstanceRecord>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1980335091441168568L;
	protected SubstanceRecord record = new SubstanceRecord();
	public final static String sql = 
		"SELECT document_prefix,hex(document_uuid) u,endpoint,guidance,substance_prefix,hex(substance_uuid) su,params,reference from substance_protocolapplication";
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		/*
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getValue()!=null && getValue().getIdsubstance()>0)
			params.add(new QueryParam<Integer>(Integer.class,getValue().getIdsubstance()));
		else throw new AmbitException("Empty ID");
	
		return params;
		*/
		return null;
	}

	@Override
	public SubstanceRecord getObject(ResultSet rs) throws AmbitException {
		record.clear();
		try {
            ProtocolApplication<Protocol,Params,String,Params,String> papp = 
            	new ProtocolApplication<Protocol, Params, String, Params, String>(new Protocol(rs.getString("endpoint")));
            papp.getProtocol().addGuidance(rs.getString("guidance"));
            record.addtMeasurement(papp);
            try {
            	papp.setDocumentUUID(rs.getString("document_prefix") + "-" + I5Utils.addDashes(rs.getString("u").toString().toLowerCase()));
            } catch (Exception xx) {
            	papp.setDocumentUUID(null);
            }			
            try {
            	record.setCompanyUUID(rs.getString("substance_prefix") + "-" + I5Utils.addDashes(rs.getString("su").toString().toLowerCase()));
            } catch (Exception xx) {
            	papp.setDocumentUUID(null);
            }		
    		papp.setReference(rs.getString("reference"));
    		//papp.setParameters(rs.getString("params")); //parse json

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
	public double calculateMetric(SubstanceRecord object) {
		return 1;
	}

	

}
