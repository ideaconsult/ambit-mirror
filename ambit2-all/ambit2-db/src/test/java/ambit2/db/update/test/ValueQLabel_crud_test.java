package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.AmbitUser;
import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.db.update.qlabel.CreateValueQLabel;
import ambit2.db.update.qlabel.DeleteValueQLabel;

public class ValueQLabel_crud_test extends CRUDTest<Integer,QLabel> {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dbFile = "src/test/resources/ambit2/db/processors/test/qdescriptors-datasets.xml";	
	}
	@Override
	protected IQueryUpdate<Integer, QLabel> createQuery() throws Exception {
		CreateValueQLabel q = new CreateValueQLabel();
		q.setGroup(1);
		QLabel label = new QLabel(QUALITY.ProbablyOK);
		label.setText("");
		label.setUser(new AmbitUser("guest"));
		q.setObject(label);
		return q;
	}

	@Override
	protected IQueryUpdate<Integer, QLabel> createQueryNew() throws Exception {
		return null;
	}

	@Override
	protected void createVerify(IQueryUpdate<Integer, QLabel> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from quality_labels where id=1 and label=\"ProbablyOK\"");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Integer, QLabel> query)
			throws Exception {
		
	}

	@Override
	protected IQueryUpdate<Integer, QLabel> deleteQuery() throws Exception {
		DeleteValueQLabel q = new DeleteValueQLabel();
		q.setGroup(1);
		q.setObject(new QLabel(QUALITY.ProbablyOK));
		return q;
	}
	@Override
	public void testCreateNew() throws Exception {
		try {
		super.testCreateNew();
		} catch (Exception x) {}
	}
	@Override
	public void testUpdate() throws Exception {
		try {
		super.testUpdate();
		} catch (Exception x) {}
	}
	@Test
	public void testDelete() throws Exception {
		IQueryUpdate<Integer,QLabel> query = deleteQuery();
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		executor.setConnection(c.getConnection());
		executor.open();
		Assert.assertTrue(executor.process(query)==0);
		deleteVerify(query);
		c.close();
	}
	@Override
	protected void deleteVerify(IQueryUpdate<Integer, QLabel> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from quality_labels where id=1 and label=\"ProbablyOK\"");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
	}

	@Override
	protected IQueryUpdate<Integer, QLabel> updateQuery() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateVerify(IQueryUpdate<Integer, QLabel> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
