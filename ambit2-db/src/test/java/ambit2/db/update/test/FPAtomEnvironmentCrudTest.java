package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.update.fpae.CreateAtomEnvironment;
import ambit2.db.update.fpae.DeleteAtomEnvironment;
import ambit2.descriptors.processors.AtomEnvironmentGenerator;

public class FPAtomEnvironmentCrudTest extends CRUDTest<IStructureRecord,IStructureRecord>  {

	@Override
	protected IQueryUpdate<IStructureRecord, IStructureRecord> createQuery()
			throws Exception {
		CreateAtomEnvironment q =  new CreateAtomEnvironment();
		IStructureRecord record = new StructureRecord(7,100211,null,null);
		
		IDatabaseConnection c = getConnection();	
		ProcessorStructureRetrieval p = new ProcessorStructureRetrieval();
		p.setConnection(c.getConnection());
	 	AtomEnvironmentGenerator gen = new AtomEnvironmentGenerator();
    	gen.setMaxLevels(1);
    	gen.setUseHydrogens(false);
    	record = gen.process(p.process(record));
		q.setObject(record);
		q.setGroup(record);
		c.close();
		return q;		
		
	}

	@Override
	protected IQueryUpdate<IStructureRecord, IStructureRecord> createQueryNew()
			throws Exception {
		return createQuery();
	}

	@Override
	protected void createVerify(
			IQueryUpdate<IStructureRecord, IStructureRecord> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createVerifyNew(
			IQueryUpdate<IStructureRecord, IStructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT * from fpaechemicals where idchemical=%d ",query.getGroup().getIdchemical()));
		
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}

	@Override
	protected IQueryUpdate<IStructureRecord, IStructureRecord> deleteQuery()
			throws Exception {
		DeleteAtomEnvironment q = new DeleteAtomEnvironment();
		IStructureRecord record = new StructureRecord(7,100211,null,null);
		q.setGroup(record);
		return q;
	}

	@Override
	protected void deleteVerify(
			IQueryUpdate<IStructureRecord, IStructureRecord> query)
			throws Exception {
		
	}

	@Override
	protected IQueryUpdate<IStructureRecord, IStructureRecord> updateQuery()
			throws Exception {
		return null;
	}

	@Override
	protected void updateVerify(
			IQueryUpdate<IStructureRecord, IStructureRecord> query)
			throws Exception {
		
		
	}
	@Override
	public void testDelete() throws Exception {
	}
	@Override
	public void testUpdate() throws Exception {
	}
	@Override
	public void testCreateNew() throws Exception {
	}
}
