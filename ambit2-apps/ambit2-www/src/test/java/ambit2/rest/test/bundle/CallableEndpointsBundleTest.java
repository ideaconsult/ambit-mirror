package ambit2.rest.test.bundle;

import junit.framework.Assert;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.test.DbUnitTest;
import net.idea.restnet.i.task.TaskResult;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.rest.bundle.CallableEndpointsBundle;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.test.CreateAmbitDatabaseProcessor;

public class CallableEndpointsBundleTest extends DbUnitTest {
	
	@Test
	public void testAddEndpoint() throws Exception {
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		IDatabaseConnection c1 = getConnection();
		
		Form form  = new Form();
		form.add("topcategory","TOX");
		form.add("endpointcategory","TO_SKIN_IRRITATION_SECTION");
		
		try {
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
			PropertyURIReporter reporter =
					new PropertyURIReporter(new Reference("http://localhost:8081/ambit2"));
			CallableEndpointsBundle callable = new CallableEndpointsBundle(
					bundle,reporter,Method.POST,form,c.getConnection(),null
					);
			TaskResult task = callable.call();
			ITable table = 	c1.createQueryTable("EXPECTED",String.format("SELECT idbundle,topcategory,endpointcategory from bundle_endpoints"));
			Assert.assertEquals(1,table.getRowCount());
			
		} catch (Exception x) {
			throw x;
		} finally {
			try {c.close();} catch (Exception x) {}
			try {c1.close();} catch (Exception x) {}
		}
	}
	
	@Test
	public void testDeleteEndpoint() throws Exception {
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		IDatabaseConnection c1 = getConnection();
		
		Form form  = new Form();
		form.add("topcategory","TOX");
		form.add("endpointcategory","TO_SKIN_IRRITATION_SECTION");
		form.add("command","delete");
		try {
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
			PropertyURIReporter reporter =
					new PropertyURIReporter(new Reference("http://localhost:8081/ambit2"));
			CallableEndpointsBundle callable = new CallableEndpointsBundle(
					bundle,reporter,Method.PUT,form,c.getConnection(),null
					);
			TaskResult task = callable.call();
			ITable table = 	c1.createQueryTable("EXPECTED",String.format("SELECT idbundle,topcategory,endpointcategory from bundle_endpoints"));
			Assert.assertEquals(0,table.getRowCount());
			
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
