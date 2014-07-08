package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.update.qlabel.CreateStructureQLabel;
import ambit2.db.update.qlabel.DeleteStructureQLabel;

public class StructureQLabel_crud_test extends CRUDTest<IStructureRecord,QLabel> {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dbFile = "src/test/resources/ambit2/db/processors/test/qdescriptors-datasets.xml";	
	}
	@Override
	protected IQueryUpdate<IStructureRecord, QLabel> createQuery()
			throws Exception {
		CreateStructureQLabel q = new CreateStructureQLabel();
		q.setGroup(new StructureRecord(-1,100211,null,null));
		q.setObject(new QLabel(QUALITY.ProbablyOK));
		return q;
	}

	@Override
	protected IQueryUpdate<IStructureRecord, QLabel> createQueryNew()
			throws Exception {
		CreateStructureQLabel q = new CreateStructureQLabel();
		q.setGroup(new StructureRecord(-1,100211,null,null));
		q.setObject(new QLabel(QUALITY.OK));
		return q;
	}

	@Override
	protected void createVerify(IQueryUpdate<IStructureRecord, QLabel> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from quality_structure where idstructure=100211 and label=\"ProbablyOK\"");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<IStructureRecord, QLabel> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from quality_structure where idstructure=100211 and label=\"OK\"");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}

	@Override
	protected IQueryUpdate<IStructureRecord, QLabel> deleteQuery()
			throws Exception {
		DeleteStructureQLabel q = new DeleteStructureQLabel();
		q.setGroup(new StructureRecord(-1,100214,null,null));
		q.setObject(new QLabel(QUALITY.OK));
		return q;
	}

	@Override
	protected void deleteVerify(IQueryUpdate<IStructureRecord, QLabel> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from quality_structure where idstructure=100214 and label=\"OK\"");
		Assert.assertEquals(0,table.getRowCount());
		c.close();

		
	}

	@Override
	protected IQueryUpdate<IStructureRecord, QLabel> updateQuery()
			throws Exception {
		CreateStructureQLabel q = new CreateStructureQLabel();
		q.setGroup(new StructureRecord(-1,100214,null,null));
		q.setObject(new QLabel(QUALITY.ProbablyOK));
		return q;
	}

	@Override
	protected void updateVerify(IQueryUpdate<IStructureRecord, QLabel> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from quality_structure where idstructure=100214 and label=\"ProbablyOK\"");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
	}

}
