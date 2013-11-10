package ambit2.db.update.test;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Assert;

import ambit2.base.data.I5Utils;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.Params;
import ambit2.db.substance.study.UpdateEffectRecords;
import ambit2.db.update.IQueryUpdate;

public class EffectRecord_crud_test  extends CRUDTest<String,EffectRecord> {

	static EffectRecord effect = initeffect();
	
	protected static EffectRecord initeffect() {
		EffectRecord record = new EffectRecord();

		Params params = new Params();params.put("Exposure", "96");
		record.setConditions(params);
		record.setEndpoint("LC50");
		record.setUnit("mg/L");
		record.setLoValue(83.1);
		return record;
	}

	@Override
	protected IQueryUpdate<String,EffectRecord> updateQuery()
			throws Exception {
		System.out.println(effect);
		return new UpdateEffectRecords("IUC4-7adb0d03-f69b-32a9-9efe-86b4a8577893",effect);
	}

	@Override
	protected void updateVerify(IQueryUpdate<String,EffectRecord> query)
			throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
		"SELECT document_prefix,hex(document_uuid) u,endpoint,conditions,unit,loQualifier,loValue,upQualifier,upvalue from substance_experiment");		
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("IUC4-7adb0d03-f69b-32a9-9efe-86b4a8577893",
				table.getValue(0,"document_prefix") + "-" + I5Utils.addDashes(table.getValue(0,"u").toString().toLowerCase()));
		
		Assert.assertEquals(effect.getEndpoint(),table.getValue(0,"endpoint"));
		Assert.assertEquals(effect.getConditions().toString(),table.getValue(0,"conditions"));
		Assert.assertEquals(effect.getUnit(),table.getValue(0,"unit"));
		Assert.assertEquals(effect.getLoQualifier(),table.getValue(0,"loQualifier"));
		Assert.assertEquals(effect.getUpQualifier(),table.getValue(0,"upQualifier"));
		Assert.assertEquals(effect.getUpValue(),table.getValue(0,"upvalue"));
		Assert.assertEquals(effect.getLoValue(),table.getValue(0,"lovalue"));
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
	protected IQueryUpdate<String, EffectRecord> createQuery() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IQueryUpdate<String, EffectRecord> createQueryNew()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IQueryUpdate<String, EffectRecord> deleteQuery() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerify(IQueryUpdate<String, EffectRecord> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<String, EffectRecord> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deleteVerify(IQueryUpdate<String, EffectRecord> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
