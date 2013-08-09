package ambit2.db.update.test;

import java.util.UUID;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.I5Utils;
import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.db.substance.CreateSubstance;
import ambit2.db.substance.DeleteSubstance;
import ambit2.db.substance.UpdateSubstance;
import ambit2.db.update.IQueryUpdate;

public class Substance_crud_test  extends CRUDTest<Object,SubstanceRecord>{
	private static final String example_uuid =    "IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734";
	private static final String example_rs_uuid = "ECB5-2c94e32c-3662-4dea-ba00-43787b8a6fd3";
	@Override
	protected IQueryUpdate<Object, SubstanceRecord> createQuery()
			throws Exception {
		SubstanceRecord c = new SubstanceRecord();
		c.setCompanyName("name");
		c.setPublicName("public name");
		c.setCompanyUUID(example_uuid);
		c.setReferenceSubstanceUUID(example_rs_uuid);
		c.setFormat("i5d");
		c.setContent("<?xml>");
		c.setSubstancetype("Multiconstituent");
		c.setIdsubstance(1);
		return new CreateSubstance(c);
	}

	@Override
	protected void createVerify(IQueryUpdate<Object, SubstanceRecord> query)
			throws Exception {
	       IDatabaseConnection c = getConnection();	
			ITable table = 	c.createQueryTable("EXPECTED","SELECT prefix,hex(uuid) u,rs_prefix,hex(rs_uuid) rs_u,name,publicname,format,content,substanceType,documentType FROM substance where idsubstance=1");
			Assert.assertEquals(1,table.getRowCount());
			Assert.assertEquals("IUC4",table.getValue(0,"prefix"));
			Assert.assertEquals(example_uuid,table.getValue(0,"prefix") + "-" + I5Utils.addDashes(table.getValue(0,"u").toString().toLowerCase()));
			Assert.assertEquals(example_rs_uuid,table.getValue(0,"rs_prefix") + "-" + I5Utils.addDashes(table.getValue(0,"rs_u").toString().toLowerCase()));
			Assert.assertEquals("name",table.getValue(0,"name"));
			Assert.assertEquals("public name",table.getValue(0,"publicname"));
			Assert.assertEquals("i5d",table.getValue(0,"format"));
			Assert.assertEquals("Multiconstituent",table.getValue(0,"substanceType"));
			//Assert.assertEquals("<?xml>".getBytes("UTF-8"),table.getValue(0,"content"));
			c.close();	
	}

	@Override
	protected IQueryUpdate<Object, SubstanceRecord> deleteQuery()
			throws Exception {
		
		SubstanceRecord c = new SubstanceRecord();
		c.setIdsubstance(1);
		return new DeleteSubstance(c);

	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object, SubstanceRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM substance where idsubstance=1");
		Assert.assertEquals(0,table.getRowCount());
		c.close();	
	}

	@Override
	protected IQueryUpdate<Object, SubstanceRecord> updateQuery()
			throws Exception {
		SubstanceRecord c = new SubstanceRecord();
		c.setIdsubstance(1);
		c.setContent("<?xml>");
		c.setFormat("i5._4");
		c.setCompanyName("name");
		c.setPublicName("public name");
		c.setCompanyUUID(example_uuid);
		c.setReferenceSubstanceUUID(example_rs_uuid);
		c.setSubstancetype("Multiconstituent");

		return new UpdateSubstance(c);

	}

	@Override
	protected void updateVerify(IQueryUpdate<Object, SubstanceRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT prefix,hex(uuid) u,rs_prefix,hex(rs_uuid) rs_u,name,publicname,format,content,substanceType FROM substance where idsubstance=1");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("IUC4",table.getValue(0,"prefix"));
		Assert.assertEquals(example_uuid,table.getValue(0,"prefix") + "-" + I5Utils.addDashes(table.getValue(0,"u").toString().toLowerCase()));
		Assert.assertEquals(example_rs_uuid,table.getValue(0,"rs_prefix") + "-" + I5Utils.addDashes(table.getValue(0,"rs_u").toString().toLowerCase()));		
		Assert.assertEquals("name",table.getValue(0,"name"));
		Assert.assertEquals("public name",table.getValue(0,"publicname"));
		Assert.assertEquals("i5._4",table.getValue(0,"format"));		
		Assert.assertEquals("Multiconstituent",table.getValue(0,"substanceType"));
		c.close();	
		
	}

	@Override
	protected IQueryUpdate<Object, SubstanceRecord> createQueryNew()
			throws Exception {
		SubstanceRecord c = new SubstanceRecord();
		//c.setIdsubstance(1);
		c.setContent("<?xml>");
		c.setFormat("i5._4.");
		c.setCompanyName("name");
		c.setPublicName("public name");
		c.setCompanyUUID(example_uuid);
		c.setReferenceSubstanceUUID(example_rs_uuid);
		return new CreateSubstance(c);
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, SubstanceRecord> query)
			throws Exception {
	        IDatabaseConnection c = getConnection();	

			ITable table = 	c.createQueryTable("EXPECTED","SELECT idsubstance,prefix,hex(uuid) u,rs_prefix,hex(rs_uuid) rs_u,name,publicname,format,content FROM substance where idsubstance!=1");
			Assert.assertTrue(table.getRowCount()>0);
			Assert.assertEquals("IUC4",table.getValue(0,"prefix"));
			Assert.assertEquals(example_uuid,table.getValue(0,"prefix") + "-" + I5Utils.addDashes(table.getValue(0,"u").toString().toLowerCase()));
			Assert.assertEquals(example_rs_uuid,table.getValue(0,"rs_prefix") + "-" + I5Utils.addDashes(table.getValue(0,"rs_u").toString().toLowerCase()));
			Assert.assertEquals("name",table.getValue(0,"name"));
			Assert.assertEquals("public name",table.getValue(0,"publicname"));
			Assert.assertEquals("i5._4.",table.getValue(0,"format"));	
			c.close();
		
	}

	@Test
	public void testUUID() throws Exception {
		String[] uuid = I5Utils.splitI5UUID(example_uuid);
		Assert.assertEquals("IUC4", uuid[0]);
		Assert.assertEquals("efdb21bb-e79f-3286-a988-b6f6944d3734", uuid[1]);
		UUID u = UUID.fromString(uuid[1]);
		Assert.assertEquals(uuid[1],u.toString());
		String nodash = uuid[1].replace("-", "");
		Assert.assertEquals(uuid[1],I5Utils.addDashes(nodash));
		
	}
}
