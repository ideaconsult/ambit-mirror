package ambit2.db.update.test;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.Template;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.update.IQueryUpdate;
import ambit2.db.update.model.CreateModel;
import ambit2.db.update.model.DeleteModel;
import ambit2.db.update.model.UpdateModel;

public class Model_crud_test extends  CRUDTest<Object,ModelQueryResults>  {

	@Override
	protected IQueryUpdate<Object,ModelQueryResults> createQuery() throws Exception {
		ModelQueryResults q = new ModelQueryResults();
		q.setName("Test model");
		q.setContent("Nothing");
		q.setPredictors(new Template("Octanol-water partition coefficient (Kow)"));
		q.setDependent(new Template("BCF"));
		return new CreateModel(q);
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,ModelQueryResults> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","select * from models");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
	}

	@Override
	protected IQueryUpdate<Object,ModelQueryResults> deleteQuery() throws Exception {
		ModelQueryResults ref = new ModelQueryResults();
		ref.setId(100);
		return new DeleteModel(ref);
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object,ModelQueryResults> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED_USER","SELECT * from models where idmodel=100");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
	}

	@Override
	protected IQueryUpdate<Object,ModelQueryResults> updateQuery() throws Exception {
		ModelQueryResults ref = new ModelQueryResults();
		ref.setContent("new content");
		ref.setId(100);
		return new UpdateModel(ref);
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,ModelQueryResults> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED_USER","SELECT * from models where idmodel=100 and content='new content'");
		Assert.assertEquals(1,table.getRowCount());
		
		c.close();
		
	}

	@Override
	protected IQueryUpdate<Object, ModelQueryResults> createQueryNew()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, ModelQueryResults> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void testCreateNew() throws Exception {
	}

}