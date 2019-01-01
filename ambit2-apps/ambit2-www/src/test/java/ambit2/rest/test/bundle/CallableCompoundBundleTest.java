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

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.bundle.CallableCompoundBundle;
import ambit2.rest.structure.CompoundURIReporter;
import ambit2.rest.test.CreateAmbitDatabaseProcessor;

public class CallableCompoundBundleTest extends DbUnitTest {
	
	@Test
	public void testCreateBundle() throws Exception {
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		IDatabaseConnection c1 = getConnection();
		
		Form form  = new Form();
		form.add("compound_uri","http://localhost:8081/ambit2/compound/11");
		form.add("tag","source");
		form.add("remarks","12345");
		
		try {
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
			CompoundURIReporter<IQueryRetrieval<IStructureRecord>> reporter =
					new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(new Reference("http://localhost:8081/ambit2"));
			CallableCompoundBundle callable = new CallableCompoundBundle(
					bundle,reporter,Method.POST,form,c.getConnection(),null
					);
			TaskResult task = callable.call();
			ITable table = 	c1.createQueryTable("EXPECTED",String.format("SELECT idbundle,idchemical,tag,remarks  from bundle_chemicals where idchemical=11 and idbundle=1"));
			Assert.assertEquals(1,table.getRowCount());
			Assert.assertEquals("source",table.getValue(0,"tag"));
			Assert.assertEquals("12345",table.getValue(0,"remarks"));
			
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
		form.add("compound_uri","http://localhost:8081/ambit2/compound/7");
		form.add("command","delete");
		
		try {
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
			CompoundURIReporter<IQueryRetrieval<IStructureRecord>> reporter =
					new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(new Reference("http://localhost:8081/ambit2"));
			CallableCompoundBundle callable = new CallableCompoundBundle(
					bundle,reporter,Method.PUT,form,c.getConnection(),null
					);
			TaskResult task = callable.call();
			ITable table = 	c1.createQueryTable("EXPECTED",String.format("SELECT idbundle,idchemical from bundle_chemicals where idchemical=7 and idbundle=1"));
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
		form.add("compound_uri","http://localhost:8081/ambit2/compound/11");
		form.add("command","add");
		form.add("tag","source");
		form.add("remarks","12345");
		
		try {
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
			CompoundURIReporter<IQueryRetrieval<IStructureRecord>> reporter =
					new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(new Reference("http://localhost:8081/ambit2"));
			CallableCompoundBundle callable = new CallableCompoundBundle(
					bundle,reporter,Method.PUT,form,c.getConnection(),null
					);
			TaskResult task = callable.call();
			ITable table = 	c1.createQueryTable("EXPECTED",String.format("SELECT idbundle,idchemical,tag,remarks from bundle_chemicals where idchemical=11 and idbundle=1"));
			Assert.assertEquals(1,table.getRowCount());
			Assert.assertEquals("source",table.getValue(0,"tag"));
			Assert.assertEquals("12345",table.getValue(0,"remarks"));
			
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
