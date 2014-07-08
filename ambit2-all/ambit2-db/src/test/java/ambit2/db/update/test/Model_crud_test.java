package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.PredictedVarsTemplate;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.update.model.CreateModel;
import ambit2.db.update.model.DeleteModel;
import ambit2.db.update.model.UpdateModel;

public class Model_crud_test extends  CRUDTest<Object,ModelQueryResults>  {

	@Override
	protected IQueryUpdate<Object,ModelQueryResults> createQuery() throws Exception {
		ModelQueryResults q = new ModelQueryResults();
		q.setAlgorithm("http://localhost:8080/algorithm/pka");
		q.setName("Test model");
		q.setContent("Nothing");
		q.setParameters(new String[] {"-M"});
		q.setPredictors(new Template("New template"));
		q.setDependent(new Template("BCF"));
		q.setPredicted(new PredictedVarsTemplate("BCF"));
		q.setCreator("test-user");
		return new CreateModel(q);
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,ModelQueryResults> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","select parameters from models where algorithm='http://localhost:8080/algorithm/pka' and creator='test-user'");
		Assert.assertEquals(1,table.getRowCount());
		Object value = table.getValue(0,"parameters");
		Assert.assertEquals("-M\t",value.toString());
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
		ModelQueryResults q = new ModelQueryResults();
		q.setName("Test model");
		q.setContent("Nothing");
		
		Template t1 = new Template("Predictors template");
		t1.add(new Property("New predictor 1"));
		t1.add(new Property("New predictor 2"));
		q.setPredictors(t1);
		
		Template t2 = new Template("Dependent template");
		t2.add(new Property("New dependent"));
		q.setDependent(t2);
		
		PredictedVarsTemplate t3 = new PredictedVarsTemplate("Predicted template");
		t3.add(new Property("New predicted"));
		q.setPredicted(t3);		
		
		return new CreateModel(q);
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, ModelQueryResults> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","select * from models where name=\"Test model\" and content=\"Nothing\" and creator='guest'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","select * from template where name=\"Predictors template\"");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","select * from template where name=\"Dependent template\"");
		Assert.assertEquals(1,table.getRowCount());	
		table = 	c.createQueryTable("EXPECTED","select * from properties where name=\"New dependent\"");
		Assert.assertEquals(1,table.getRowCount());				
		table = 	c.createQueryTable("EXPECTED","select * from properties where name=\"New predictor 1\"");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","select * from properties where name=\"New predictor 2\"");
		Assert.assertEquals(1,table.getRowCount());				
				
		c.close();
		
	}

}