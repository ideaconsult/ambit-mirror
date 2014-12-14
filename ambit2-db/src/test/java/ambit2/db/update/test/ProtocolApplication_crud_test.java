package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.I5Utils;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.db.substance.study.UpdateSubstanceStudy;

public class ProtocolApplication_crud_test extends CRUDTest<String,ProtocolApplication<Protocol, IParams, String, IParams, String>> {

	static ProtocolApplication<Protocol, IParams, String, IParams, String> papp = initpa();
	
	protected static ProtocolApplication initpa() {
		Protocol protocol = new Protocol("Short-term toxicity to fish, IUC4#53/Ch.4.1");
		protocol.setCategory("EC_FISHTOX_SECTION");
		protocol.addGuideline("Method: other: acute toxicity test; \"static bioassay\"");
		ProtocolApplication papp = new ProtocolApplication<Protocol, IParams, String, IParams, String>(protocol);
		IParams params = new Params();params.put("Test organism", "Lepomis cyanellus");
		papp.setParameters(params);
		papp.setReference("reference");
		papp.setDocumentUUID("IUC4-7adb0d03-f69b-32a9-9efe-86b4a8577893");
		EffectRecord record = new EffectRecord<String,IParams,String>();
		params = new Params();params.put("Exposure", "96");
		record.setConditions(params);
		record.setEndpoint("LC50");
		record.setUnit("mg/L");
		record.setLoValue(83.1);
		papp.addEffect(record);
		return papp;
	}

	protected static ProtocolApplication initpc() {
		Protocol protocol = new Protocol("Partition coefficient, IUC4#5/Ch.2.5");
		protocol.setCategory("PC_PARTITION_SECTION");
		protocol.addGuideline("Method: other (measured)");
		ProtocolApplication papp = new ProtocolApplication<Protocol, IParams, String, IParams, String>(protocol);
		IParams params = new Params();
		papp.setParameters(params);
		papp.setReference("reference");
		papp.setDocumentUUID("IUC4-2f64ab27-d2be-352e-b9d8-4f0274fd6633");
		EffectRecord record = new EffectRecord<String,IParams,String>();
		params = new Params();params.put("Temperature", "25 \u2103C");
		record.setConditions(params);
		record.setEndpoint("log Pow");
		record.setLoValue(0.35);
		papp.addEffect(record);
		return papp;
	}

	protected static ProtocolApplication initacutetox() {
		Protocol protocol = new Protocol("Acute toxicity: oral, IUC4#2/Ch.5.1.1");
		protocol.setCategory("TO_ACUTE_ORAL_SECTION");
		protocol.addGuideline("Method: other: no data");
		ProtocolApplication papp = new ProtocolApplication<Protocol, IParams, String, IParams, String>(protocol);
		IParams params = new Params();
		papp.setParameters(params);
		papp.setReference("Smyth, H. F. Seaton J., and Fischer L. (1941).|The single dose toxicity of some glycols and derivatives.|J. Ind. Hyg. Toxicol. 23, 259-268");
		params.put("Species","rat");
		params.put("Sex","male/female");
		papp.setDocumentUUID("IUC4-ae64fc3b-22a4-3173-9362-9cce1ff622ae");
		EffectRecord record = new EffectRecord<String,IParams,String>();
		params = new Params();params.put("Temperature", "25 \u2103C");params.put("Sex","male");
		record.setConditions(params);
		record.setEndpoint("LD50");
		record.setLoValue(260);
		record.setUpValue(320);
		record.setUnit("mg/kg bw");
		papp.addEffect(record);
		return papp;
	}

	protected static ProtocolApplication initbiodeg() {
		Protocol protocol = new Protocol("Biodegradation in water: screening tests, IUC4#1/Ch.3.5");
		protocol.setCategory("TO_BIODEG_WATER_SCREEN_SECTION");
		protocol.addGuideline("OECD Guideline 301 D (Ready Biodegradability: Closed Bottle Test)");
		ProtocolApplication papp = new ProtocolApplication<Protocol, IParams, String, IParams, String>(protocol);
		IParams params = new Params();
		papp.setParameters(params);
		papp.setReference("reference");
		papp.setDocumentUUID("IUC4-1d75f01c-3b2b-35f5-84f1-ce23e22b6c73");
		EffectRecord record = new EffectRecord<String,Params,String>();
		params = new Params();params.put("Time point", "28 d");
		record.setConditions(params);
		record.setEndpoint("% Degradation");
		record.setLoValue(90);
		record.setUnit("%");
		papp.addEffect(record);
		return papp;
	}


	@Override
	protected IQueryUpdate<String,ProtocolApplication<Protocol, IParams, String, IParams, String>> updateQuery()
			throws Exception {
		return new UpdateSubstanceStudy("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",papp);
	}

	@Override
	protected void updateVerify(IQueryUpdate<String,ProtocolApplication<Protocol, IParams, String, IParams, String>> query)
			throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
		"SELECT document_prefix,hex(document_uuid) u,endpointcategory,endpoint,guidance,substance_prefix,hex(substance_uuid) su,params,reference from substance_protocolapplication where document_prefix='IUC4' and document_UUID=unhex('7ADB0D03F69B32A99EFE86B4A8577893')");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(papp.getDocumentUUID(),table.getValue(0,"document_prefix") + "-" + I5Utils.addDashes(table.getValue(0,"u").toString().toLowerCase()));
		Assert.assertEquals("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",table.getValue(0,"substance_prefix") + "-" + I5Utils.addDashes(table.getValue(0,"su").toString().toLowerCase()));
		Assert.assertEquals(papp.getProtocol().getEndpoint(),table.getValue(0,"endpoint"));
		Assert.assertEquals(papp.getProtocol().getCategory(),table.getValue(0,"endpointcategory"));
		Assert.assertEquals(papp.getProtocol().getGuideline().get(0),table.getValue(0,"guidance"));
		Assert.assertEquals(papp.getReference(),table.getValue(0,"reference"));
		Assert.assertEquals(papp.getParameters().toString(),table.getValue(0,"params"));
		c.close();

	}
/*
	


	 {
"uuid":	"IUC4-ae64fc3b-22a4-3173-9362-9cce1ff622ae/0",
"protocol":	{"category":"TO_ACUTE_ORAL_SECTION","endpoint":"Acute toxicity: oral, IUC4#2/Ch.5.1.1","guidance": ["Method: other: no data"]},
"parameters":	{"Test Organism":"guinea pig","Reference":"Smyth, H. F. Seaton J., and Fischer L. (1941).|The single dose toxicity of some glycols and derivatives.|J. Ind. Hyg. Toxicol. 23, 259-268","Sex":"male/female"},
"effects":	[{
"endpoint":	"LD50",
"conditions":	{"Sex":"male/female"},
"result":	{
	"unit":	"mg/kg bw",
	"loValue":	260.0
	}
}]
}
	 
 */

	@Override
	public void testDelete() throws Exception {
	}

	@Override
	protected IQueryUpdate<String, ProtocolApplication<Protocol, IParams, String, IParams, String>> createQuery()
			throws Exception {
		return new UpdateSubstanceStudy("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",initpc());
	}

	@Override
	protected IQueryUpdate<String, ProtocolApplication<Protocol, IParams, String, IParams, String>> createQueryNew()
			throws Exception {
		return new UpdateSubstanceStudy("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",initacutetox());
	}

	@Override
	protected IQueryUpdate<String, ProtocolApplication<Protocol, IParams, String, IParams, String>> deleteQuery()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerify(
			IQueryUpdate<String, ProtocolApplication<Protocol, IParams, String, IParams, String>> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createVerifyNew(
			IQueryUpdate<String, ProtocolApplication<Protocol, IParams, String, IParams, String>> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deleteVerify(
			IQueryUpdate<String, ProtocolApplication<Protocol, IParams, String, IParams, String>> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
