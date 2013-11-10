package ambit2.db.update.test;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.I5Utils;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.db.substance.study.UpdateSubstanceStudy;
import ambit2.db.update.IQueryUpdate;

public class ProtocolApplication_crud_test extends CRUDTest<String,ProtocolApplication<Protocol, Params, String, Params, String>> {

	static ProtocolApplication<Protocol, Params, String, Params, String> papp = initpa();
	
	protected static ProtocolApplication initpa() {
		Protocol protocol = new Protocol("Short-term toxicity to fish, IUC4#53/Ch.4.1");
		protocol.addGuidance("Method: other: acute toxicity test; \"static bioassay\"");
		ProtocolApplication papp = new ProtocolApplication<Protocol, Params, String, Params, String>(protocol);
		Params params = new Params();params.put("Test organism", "Lepomis cyanellus");
		papp.setParameters(params);
		papp.setReference("reference");
		papp.setDocumentUUID("IUC4-7adb0d03-f69b-32a9-9efe-86b4a8577893");
		EffectRecord record = new EffectRecord<String,Params,String>();
		params = new Params();params.put("Exposure", "96");
		record.setConditions(params);
		record.setEndpoint("LC50");
		record.setUnit("mg/L");
		record.setLoValue(83.1);
		papp.addEffect(record);
		return papp;
	}

	@Override
	protected IQueryUpdate<String,ProtocolApplication<Protocol, Params, String, Params, String>> updateQuery()
			throws Exception {
		//System.out.println(papp);
		return new UpdateSubstanceStudy("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",papp);
	}

	@Override
	protected void updateVerify(IQueryUpdate<String,ProtocolApplication<Protocol, Params, String, Params, String>> query)
			throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
		"SELECT document_prefix,hex(document_uuid) u,endpoint,guidance,substance_prefix,hex(substance_uuid) su,params,reference from substance_protocolapplication");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(papp.getDocumentUUID(),table.getValue(0,"document_prefix") + "-" + I5Utils.addDashes(table.getValue(0,"u").toString().toLowerCase()));
		Assert.assertEquals("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",table.getValue(0,"substance_prefix") + "-" + I5Utils.addDashes(table.getValue(0,"su").toString().toLowerCase()));
		Assert.assertEquals(papp.getProtocol().getEndpoint(),table.getValue(0,"endpoint"));
		Assert.assertEquals(papp.getProtocol().getGuidance().get(0),table.getValue(0,"guidance"));
		Assert.assertEquals(papp.getReference(),table.getValue(0,"reference"));
		//Assert.assertEquals(papp.getParameters().toString(),table.getValue(0,"params"));
		c.close();

	}
	@Override
	public void testCreate() throws Exception {

	}
	@Override
	public void testCreateNew() throws Exception {
	}
	@Override
	public void testDelete() throws Exception {
	}

	@Override
	protected IQueryUpdate<String, ProtocolApplication<Protocol, Params, String, Params, String>> createQuery()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IQueryUpdate<String, ProtocolApplication<Protocol, Params, String, Params, String>> createQueryNew()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IQueryUpdate<String, ProtocolApplication<Protocol, Params, String, Params, String>> deleteQuery()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerify(
			IQueryUpdate<String, ProtocolApplication<Protocol, Params, String, Params, String>> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createVerifyNew(
			IQueryUpdate<String, ProtocolApplication<Protocol, Params, String, Params, String>> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deleteVerify(
			IQueryUpdate<String, ProtocolApplication<Protocol, Params, String, Params, String>> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
