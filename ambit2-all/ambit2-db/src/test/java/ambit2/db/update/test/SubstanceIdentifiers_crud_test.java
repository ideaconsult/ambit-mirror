package ambit2.db.update.test;

import java.util.ArrayList;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.db.substance.ids.UpdateSubstanceIdentifiers;

public class SubstanceIdentifiers_crud_test extends CRUDTest<Object,SubstanceRecord>{
	private static final String example_uuid =    "IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734";
														
	
	@Override
	protected IQueryUpdate<Object, SubstanceRecord> createQuery()
			throws Exception {
		SubstanceRecord c = new SubstanceRecord();
		c.setCompanyUUID(example_uuid);
		if (c.getExternalids()==null) c.setExternalids(new ArrayList<ExternalIdentifier>());
		c.getExternalids().add(new ExternalIdentifier("System1","id1"));
		c.getExternalids().add(new ExternalIdentifier("System2","id2"));
		return new UpdateSubstanceIdentifiers(c);
	}

	@Override
	protected void createVerify(IQueryUpdate<Object, SubstanceRecord> query)
			throws Exception {
	       IDatabaseConnection c = getConnection();	
			ITable table = 	c.createQueryTable("EXPECTED","SELECT prefix,hex(uuid) u,type,id FROM substance_ids");
			Assert.assertEquals(2,table.getRowCount());
			Assert.assertEquals("IUC4",table.getValue(0,"prefix"));
			Assert.assertEquals(example_uuid,table.getValue(0,"prefix") + "-" + I5Utils.addDashes(table.getValue(0,"u").toString().toLowerCase()));
			//Assert.assertEquals("<?xml>".getBytes("UTF-8"),table.getValue(0,"content"));
			c.close();	
	}

	@Override
	protected IQueryUpdate<Object, SubstanceRecord> deleteQuery()
			throws Exception {
		
		SubstanceRecord c = new SubstanceRecord();
		c.setCompanyUUID(example_uuid);
		c.setExternalids(null);
		return new UpdateSubstanceIdentifiers(c);

	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object, SubstanceRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM substance_ids ");
		Assert.assertEquals(0,table.getRowCount());
		c.close();	
	}

	@Override
	protected IQueryUpdate<Object, SubstanceRecord> updateQuery()
			throws Exception {
		return null;
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object, SubstanceRecord> query)
			throws Exception {
        
		
	}

	@Override
	protected IQueryUpdate<Object, SubstanceRecord> createQueryNew()
			throws Exception {
		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, SubstanceRecord> query)
			throws Exception {
	  
		
	}
	
	@Override
	public void testUpdate() throws Exception {
	}
	@Override
	public void testCreateNew() throws Exception {

	}
}
