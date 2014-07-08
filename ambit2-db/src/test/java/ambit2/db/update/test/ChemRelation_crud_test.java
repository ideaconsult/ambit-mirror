package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.chemrelation.DeleteStructureRelation;
import ambit2.db.chemrelation.UpdateStructureRelation;

public class ChemRelation_crud_test extends CRUDTest<IStructureRecord,IStructureRecord>{

	@Override
	protected IQueryUpdate<IStructureRecord, IStructureRecord> createQuery() throws Exception {
		IStructureRecord c1 = new StructureRecord();
		c1.setIdchemical(7);
		IStructureRecord c2= new StructureRecord();
		c2.setIdchemical(10);
		return new UpdateStructureRelation(c1,c2,"relation");
	}

	@Override
	protected void createVerify(IQueryUpdate<IStructureRecord, IStructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idchemical1,idchemical2,relation,metric FROM chem_relation where idchemical1=7 and idchemical2=10 and relation='relation'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}


	protected IQueryUpdate<IStructureRecord, IStructureRecord> createQueryNew() throws Exception {
		IStructureRecord c1 = new StructureRecord();
		c1.setIdchemical(7);
		IStructureRecord c2= new StructureRecord();
		c2.setIdchemical(11);
		return new UpdateStructureRelation(c1,c2,"test",3.14);
	}
	
	protected void createVerifyNew(IQueryUpdate<IStructureRecord, IStructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idchemical1,idchemical2,relation,metric FROM chem_relation where idchemical1=7 and idchemical2=11 and relation='test'");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(3.14,table.getValue(0,"metric"));
		c.close();
	}	

	@Override
	protected IQueryUpdate<IStructureRecord, IStructureRecord> deleteQuery() throws Exception {
		IStructureRecord c1 = new StructureRecord();
		c1.setIdchemical(7);
		IStructureRecord c2= new StructureRecord();
		c2.setIdchemical(11);
		DeleteStructureRelation q = new DeleteStructureRelation(c1,c2,"test");
		return q;
	}

	@Override
	protected void deleteVerify(IQueryUpdate<IStructureRecord, IStructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idchemical1,idchemical2,relation,metric FROM chem_relation where idchemical1=7 and idchemical2=11 and relation='test'");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
	}

	@Override
	public void testUpdate() throws Exception {
		//do nothing
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

}