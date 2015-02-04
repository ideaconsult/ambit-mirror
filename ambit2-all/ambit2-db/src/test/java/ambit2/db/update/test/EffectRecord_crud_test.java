package ambit2.db.update.test;

import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Assert;

import ambit2.base.data.I5Utils;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.Value;
import ambit2.db.substance.study.DeleteEffectRecords;
import ambit2.db.substance.study.UpdateEffectRecords;

public class EffectRecord_crud_test  extends CRUDTest<ProtocolApplication<Protocol, Params,String,Params,String>,EffectRecord> {

	static EffectRecord effect = initeffect();
	
	protected static EffectRecord initeffect() {
		EffectRecord record = new EffectRecord();

		IParams params = new Params();params.put("Exposure", "96");
		record.setConditions(params);
		record.setEndpoint("LC50");
		record.setUnit("mg/L");
		record.setLoValue(83.1);
		return record;
	}

	protected static EffectRecord initeffect_PC_PARTITION_SECTION() {

		EffectRecord record = new EffectRecord<String,IParams,String>();
		IParams params = new Params();
		params = new Params();params.put("Temperature", "25 \u2103C");
		record.setConditions(params);
		record.setEndpoint("log Pow");
		record.setLoValue(0.35);
		return record;
	}
	protected static EffectRecord initeffect_TO_BIODEG_WATER_SCREEN_SECTION() {
		EffectRecord record = new EffectRecord<String,IParams,String>();
		IParams params = new Params();
		Value v = new Value();
		v.setUnits("d");
		v.setLoValue(7.0);
		params.put("Sampling time", v);
		record.setConditions(params);
		record.setEndpoint("% Degradation");
		record.setLoValue(90);
		record.setUnit("%");
		return record;
	}
	protected static EffectRecord initeffect_TO_ACUTE_ORAL_SECTION() {
		EffectRecord record = new EffectRecord<String,Params,String>();
		IParams params = new Params();
		params = new Params();params.put("Temperature", "25 \u2103C");params.put("Sex","male");
		record.setConditions(params);
		record.setEndpoint("LD50");
		record.setLoValue(260);
		record.setUpValue(320);
		record.setUnit("mg/kg bw");
		return record;
	}
	@Override
	protected IQueryUpdate<ProtocolApplication<Protocol, Params,String,Params,String>,EffectRecord> updateQuery()
			throws Exception {
		Protocol p = new Protocol("test");
		p.setCategory("PC_PARTITION_SECTION");
		p.setTopCategory("P-CHEM");		
		ProtocolApplication papp = new ProtocolApplication(p);
		papp.setDocumentUUID("IUC4-7adb0d03-f69b-32a9-9efe-86b4a8577893");
		return new UpdateEffectRecords("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",papp,effect);
		
	}

	@Override
	protected void updateVerify(IQueryUpdate<ProtocolApplication<Protocol, Params,String,Params,String>,EffectRecord> query)
			throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
		"SELECT document_prefix,hex(document_uuid) u,endpoint,conditions,unit,loQualifier,loValue,upQualifier,upvalue,topcategory,endpointcategory from substance_experiment");		
		Assert.assertEquals(5,table.getRowCount());
		Assert.assertEquals("IUC4-7adb0d03-f69b-32a9-9efe-86b4a8577893",
				table.getValue(0,"document_prefix") + "-" + I5Utils.addDashes(table.getValue(0,"u").toString().toLowerCase()));
		
		Assert.assertEquals(effect.getEndpoint(),table.getValue(0,"endpoint"));
		Assert.assertEquals(effect.getConditions().toString(),table.getValue(0,"conditions"));
		Assert.assertEquals(effect.getUnit(),table.getValue(0,"unit"));
		Assert.assertEquals(effect.getLoQualifier(),table.getValue(0,"loQualifier"));
		Assert.assertEquals(effect.getUpQualifier(),table.getValue(0,"upQualifier"));
		Assert.assertEquals(effect.getUpValue(),table.getValue(0,"upvalue"));
		Assert.assertEquals(effect.getLoValue(),table.getValue(0,"lovalue"));
		
		Assert.assertNotNull(table.getValue(0,"topcategory"));
		Assert.assertNotNull(table.getValue(0,"endpointcategory"));
		
		table = 	c.createQueryTable("EXPECTED",
		"SELECT document_prefix,hex(document_uuid) u,endpoint,conditions,unit,loQualifier,loValue,upQualifier,upvalue,topcategory,endpointcategory from substance_experiment where topcategory is null or endpointcategory is null");		
		Assert.assertEquals(0,table.getRowCount());
		
		table = 	c.createQueryTable("EXPECTED",
		"SELECT document_prefix,hex(document_uuid) u,endpoint,conditions,unit,loQualifier,loValue,upQualifier,upvalue,topcategory,endpointcategory from substance_experiment where substance_prefix is null or substance_uuid is null");		
		Assert.assertEquals(0,table.getRowCount());
				
		c.close();

	}


	@Override
	protected IQueryUpdate<ProtocolApplication<Protocol, Params,String,Params,String>, EffectRecord> createQuery() throws Exception {
		Protocol p = new Protocol("test");
		p.setCategory("PC_PARTITION_SECTION");
		p.setTopCategory("P-CHEM");

		ProtocolApplication papp = new ProtocolApplication(p);
		papp.setDocumentUUID("IUC4-2f64ab27-d2be-352e-b9d8-4f0274fd6633");
		return new UpdateEffectRecords("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",papp,initeffect_PC_PARTITION_SECTION());
	}

	@Override
	protected IQueryUpdate<ProtocolApplication<Protocol, Params,String,Params,String>, EffectRecord> createQueryNew()
			throws Exception {
		//return new UpdateEffectRecords("IUC4-1d75f01c-3b2b-35f5-84f1-ce23e22b6c73", initeffect_TO_BIODEG_WATER_SCREEN_SECTION());
		Protocol p = new Protocol("test");
		p.setCategory("TO_BIODEG_WATER_SCREEN_SECTION");
		p.setTopCategory("TOX");
		ProtocolApplication papp = new ProtocolApplication(p);
		papp.setDocumentUUID("IUC4-ae64fc3b-22a4-3173-9362-9cce1ff622ae");		
		
		return new UpdateEffectRecords("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",papp, initeffect_TO_BIODEG_WATER_SCREEN_SECTION());
		
	}

	@Override
	protected IQueryUpdate<ProtocolApplication<Protocol, Params,String,Params,String>, EffectRecord> deleteQuery() throws Exception {
		Protocol p = new Protocol("test");
		ProtocolApplication papp = new ProtocolApplication(p);
		papp.setDocumentUUID("IUC4-7adb0d03-f69b-32a9-9efe-86b4a8577893");
		return new DeleteEffectRecords(papp);
	}

	@Override
	protected void createVerify(IQueryUpdate<ProtocolApplication<Protocol, Params,String,Params,String>, EffectRecord> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<ProtocolApplication<Protocol, Params,String,Params,String>, EffectRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
		"SELECT document_prefix,hex(document_uuid) u,endpoint,conditions,unit,loQualifier,loValue,upQualifier,upvalue,topcategory,endpointcategory from substance_experiment where topcategory is null or endpointcategory is null");		
		Assert.assertEquals(0,table.getRowCount());
		
		table = 	c.createQueryTable("EXPECTED",
		"SELECT document_prefix,hex(document_uuid) u,endpoint,conditions,unit,loQualifier,loValue,upQualifier,upvalue,topcategory,endpointcategory from substance_experiment where substance_prefix is null or substance_uuid is null");		
		Assert.assertEquals(0,table.getRowCount());
		
		c.close();
		
	}

	@Override
	protected void deleteVerify(IQueryUpdate<ProtocolApplication<Protocol, Params,String,Params,String>, EffectRecord> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
