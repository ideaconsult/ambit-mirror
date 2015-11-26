package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.update.fpae.CreateAtomEnvironmentMatrix;
import ambit2.db.update.fpae.DeleteAtomEnvironment;
import ambit2.descriptors.processors.AtomEnvironmentMatrixGenerator;

public class FPAtomEnvironmentCrudTest extends CRUDTest<IStructureRecord,IStructureRecord>  {

	@Override
	protected IQueryUpdate<IStructureRecord, IStructureRecord> createQuery()
			throws Exception {
		CreateAtomEnvironmentMatrix q =  new CreateAtomEnvironmentMatrix();
		IStructureRecord record = new StructureRecord(7,100211,null,null);
		
		IDatabaseConnection c = getConnection();	
		ProcessorStructureRetrieval p = new ProcessorStructureRetrieval();
		p.setConnection(c.getConnection());
	 	AtomEnvironmentMatrixGenerator gen = new AtomEnvironmentMatrixGenerator(3);
    	record = gen.process(p.process(record));
		q.setObject(record);
		q.setGroup(record);
		c.close();
		return q;		
		
	}
	private static String ae_expected = "Csp2A Csp2A2 Csp2Csp3B Csp2Csp3B2 Csp2Csp3C Csp2Csp3C4 Csp2Csp3D Csp2Csp3D4 Csp2Ominusco2B Csp2Ominusco2B2 Csp2Osp2co2B Csp2Osp2co2B2 Csp3A Csp3A14 Csp3Csp3B Csp3Csp3B12 Csp3Csp3C Csp3Csp3C10 Csp3Csp3D Csp3Csp3D8 Csp3Ominusco2C Csp3Ominusco2C2 Csp3Ominusco2D Csp3Ominusco2D4 Csp3Osp2co2C Csp3Osp2co2C2 Csp3Osp2co2D Csp3Osp2co2D4 Ominusco2A Ominusco2A2 Ominusco2Osp2co2C Ominusco2Osp2co2C2 Osp2co2A Osp2co2A2 XA XA1 ";
	@Override
	protected IQueryUpdate<IStructureRecord, IStructureRecord> createQueryNew()
			throws Exception {
		return createQuery();
	}

	@Override
	protected void createVerify(
			IQueryUpdate<IStructureRecord, IStructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT idchemical,tags,bc,levels,factory from fpatomenvironments where idchemical=%d ",query.getGroup().getIdchemical()));
		
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(ae_expected,table.getValue(0, "tags"));
		Assert.assertEquals(18,table.getValue(0, "bc"));
		Assert.assertEquals("3",table.getValue(0, "levels").toString());
		c.close();
		
	}

	@Override
	protected void createVerifyNew(
			IQueryUpdate<IStructureRecord, IStructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT idchemical,tags,bc from fpatomenvironments where idchemical=%d ",query.getGroup().getIdchemical()));
		
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(ae_expected,table.getValue(0, "tags"));
		Assert.assertEquals(18,table.getValue(0, "bc"));
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
