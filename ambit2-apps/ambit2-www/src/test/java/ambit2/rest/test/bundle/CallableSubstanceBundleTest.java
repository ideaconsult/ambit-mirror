package ambit2.rest.test.bundle;

import junit.framework.Assert;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.test.DbUnitTest;
import net.idea.restnet.i.task.TaskResult;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.rest.bundle.CallableSubstanceBundle;
import ambit2.rest.substance.SubstanceURIReporter;
import ambit2.rest.test.CreateAmbitDatabaseProcessor;

public class CallableSubstanceBundleTest extends DbUnitTest {
	
	@Test
	public void testCreateBundle() throws Exception {
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		IDatabaseConnection c1 = getConnection();
		
		Form form  = new Form();
		form.add("substance_uri","http://localhost:8081/ambit2/substance/IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734");
		
		try {
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
			SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>> reporter =
					new SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>>(new Reference("http://localhost:8081/ambit2"));
			CallableSubstanceBundle callable = new CallableSubstanceBundle(
					bundle,reporter,Method.POST,form,c.getConnection(),null
					);
			TaskResult task = callable.call();
			ITable table = 	c1.createQueryTable("EXPECTED",String.format("SELECT idbundle,idsubstance from bundle_substance"));
			Assert.assertEquals(1,table.getRowCount());
			
		} catch (Exception x) {
			throw x;
		} finally {
			try {c.close();} catch (Exception x) {}
			try {c1.close();} catch (Exception x) {}
		}
	}
	
	@Test
	public void testDeleteBundle() throws Exception {
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		IDatabaseConnection c1 = getConnection();
		
		Form form  = new Form();
		form.add("substance_uri","http://localhost:8081/ambit2/substance/IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734");
		form.add("command","delete");
		
		try {
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
			SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>> reporter =
					new SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>>(new Reference("http://localhost:8081/ambit2"));
			CallableSubstanceBundle callable = new CallableSubstanceBundle(
					bundle,reporter,Method.PUT,form,c.getConnection(),null
					);
			TaskResult task = callable.call();
			ITable table = 	c1.createQueryTable("EXPECTED",String.format("SELECT idbundle,idsubstance from bundle_substance"));
			Assert.assertEquals(0,table.getRowCount());
			
		} catch (Exception x) {
			throw x;
		} finally {
			try {c.close();} catch (Exception x) {}
			try {c1.close();} catch (Exception x) {}
		}
	}	
	
	@Test
	public void testUpdateBundle() throws Exception {
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		IDatabaseConnection c1 = getConnection();
		
		Form form  = new Form();
		form.add("substance_uri","http://localhost:8081/ambit2/substance/IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734");
		form.add("command","add");
		
		try {
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
			SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>> reporter =
					new SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>>(new Reference("http://localhost:8081/ambit2"));
			CallableSubstanceBundle callable = new CallableSubstanceBundle(
					bundle,reporter,Method.PUT,form,c.getConnection(),null
					);
			TaskResult task = callable.call();
			ITable table = 	c1.createQueryTable("EXPECTED",String.format("SELECT idbundle,idsubstance from bundle_substance"));
			Assert.assertEquals(1,table.getRowCount());
			
		} catch (Exception x) {
			throw x;
		} finally {
			try {c.close();} catch (Exception x) {}
			try {c1.close();} catch (Exception x) {}
		}
	}	
	
	protected String dbFile = "src/test/resources/descriptors-datasets.xml";

	@Override
	protected CreateDatabaseProcessor getDBCreateProcessor() {
		return new CreateAmbitDatabaseProcessor();
	}

	@Override
	public String getDBTables() {
		return "src/test/resources/tables.xml";
	}
	
    @Override
    protected String getConfig() {
    	return "ambit2/rest/config/ambit2.pref";
    }

}
