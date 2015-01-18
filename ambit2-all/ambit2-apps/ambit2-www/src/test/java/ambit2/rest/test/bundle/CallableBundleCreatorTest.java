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

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.rest.bundle.CallableBundleCreator;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.test.CreateAmbitDatabaseProcessor;

public class CallableBundleCreatorTest extends DbUnitTest {
	
	@Test
	public void testCreateBundle() throws Exception {
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		IDatabaseConnection c1 = getConnection();
		
		Form form  = new Form();
		form.add(ISourceDataset.fields.title.name(),"title");
		form.add(ISourceDataset.fields.source.name(),"source");
		form.add(ISourceDataset.fields.license.name(),"license");
		form.add(ISourceDataset.fields.maintainer.name(),"maintainer");
		form.add(ISourceDataset.fields.rightsHolder.name(),"rightsHolder");
		form.add("description","description");
		form.add(ISourceDataset.fields.url.name(),"url");
		form.add(ISourceDataset.fields.stars.name(),"9");
		
		try {
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle();
			DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>,SubstanceEndpointsBundle> reporter = new DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>,SubstanceEndpointsBundle>(new Reference("http://localhost"));
			CallableBundleCreator callable = new CallableBundleCreator(
					bundle,reporter,Method.POST,form,c.getConnection(),null
					);
			TaskResult task = callable.call();
			ITable table = 	c1.createQueryTable("EXPECTED",String.format("SELECT idbundle,description from bundle where name='title'"));
			Assert.assertEquals(1,table.getRowCount());
			Assert.assertEquals("description",table.getValue(0, "description"));
			
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
		
		try {
			ITable table = 	c1.createQueryTable("EXPECTED",String.format("SELECT idbundle from bundle where idbundle=1"));
			Assert.assertEquals(1,table.getRowCount());
			
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
			DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>,SubstanceEndpointsBundle> reporter = new DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>,SubstanceEndpointsBundle>(new Reference("http://localhost"));
			CallableBundleCreator callable = new CallableBundleCreator(
					bundle,reporter,Method.DELETE,new Form(),c.getConnection(),null
					);
			TaskResult task = callable.call();
			table = 	c1.createQueryTable("EXPECTED",String.format("SELECT idbundle from bundle where idbundle=1"));
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
		form.add(ISourceDataset.fields.title.name(),"title");
		form.add(ISourceDataset.fields.source.name(),"source");
		form.add(ISourceDataset.fields.license.name(),"license");
		form.add(ISourceDataset.fields.maintainer.name(),"maintainer");
		form.add(ISourceDataset.fields.rightsHolder.name(),"rightsHolder");
		form.add("description","description");
		form.add(ISourceDataset.fields.url.name(),"url");
		form.add(ISourceDataset.fields.stars.name(),"9");
		
		try {
			ITable table = 	c1.createQueryTable("EXPECTED",String.format("SELECT idbundle from bundle where idbundle=1"));
			Assert.assertEquals(1,table.getRowCount());
			
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
			DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>,SubstanceEndpointsBundle> reporter = new DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>,SubstanceEndpointsBundle>(new Reference("http://localhost"));
			CallableBundleCreator callable = new CallableBundleCreator(
					bundle,reporter,Method.PUT,form,c.getConnection(),null
					);
			TaskResult task = callable.call();
			table = 	c1.createQueryTable("EXPECTED",String.format("SELECT idbundle,name,licenseURI,rightsHolder,maintainer,stars,title,url,description from bundle join catalog_references using(idreference) where idbundle=1"));
			Assert.assertEquals(1,table.getRowCount());
			Assert.assertEquals("title",table.getValue(0,"name"));
			Assert.assertEquals("maintainer",table.getValue(0,"maintainer"));
			Assert.assertEquals("rightsHolder",table.getValue(0,"rightsHolder"));
			Assert.assertEquals("license",table.getValue(0,"licenseURI"));
			Assert.assertEquals("description",table.getValue(0, "description"));
			//Assert.assertEquals("url",table.getValue(0,"url"));
			//Assert.assertEquals("source",table.getValue(0,"title"));
			
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
