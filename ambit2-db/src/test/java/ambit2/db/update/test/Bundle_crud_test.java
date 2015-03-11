package ambit2.db.update.test;

import java.math.BigInteger;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.CreateBundle;
import ambit2.db.update.bundle.CreateBundleCopy;
import ambit2.db.update.bundle.CreateBundleVersion;
import ambit2.db.update.bundle.DeleteBundle;
import ambit2.db.update.bundle.UpdateBundle;
import ambit2.db.update.bundle.matrix.CreateMatrixFromBundle;
import ambit2.db.update.bundle.matrix.ReadEffectRecordByBundleMatrix._matrix;

public class Bundle_crud_test extends CRUDTest<Object, SubstanceEndpointsBundle> {
    @Override
    public void setUp() throws Exception {
	super.setUp();
	dbFile = "src/test/resources/ambit2/db/processors/test/dataset-properties1.xml";
    }

    @Override
    public void setUpDatabase(String xmlfile) throws Exception {
	super.setUpDatabase(xmlfile);
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM bundle where idbundle=1");
	Assert.assertEquals(1, table.getRowCount());
	c.close();
    }

    @Override
    protected IQueryUpdate<Object, SubstanceEndpointsBundle> createQuery() throws Exception {
	SubstanceEndpointsBundle adataset = new SubstanceEndpointsBundle();
	adataset.setName("ambit");
	adataset.setTitle("new_title");
	adataset.setURL("new_url");
	adataset.setDescription("description");
	adataset.setLicenseURI(ISourceDataset.license.CC0_1_0.getURI());
	adataset.setUserName("testuser");
	CreateBundle user = new CreateBundle();
	user.setObject(adataset);
	return user;
    }

    @Override
    protected void createVerify(IQueryUpdate<Object, SubstanceEndpointsBundle> query) throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable(
			"EXPECTED_BUNDLE",
			"SELECT idbundle,hex(bundle_number) bn,version,user_name,licenseURI,description,created,updated,published_status FROM bundle where name='ambit'");
	Assert.assertEquals(1, table.getRowCount());
	Object idbundle = table.getValue(0, "idbundle");
	Assert.assertNotNull(idbundle);
	Assert.assertNotNull(table.getValue(0, "bn"));
	Assert.assertEquals("description", table.getValue(0, "description"));
	Assert.assertEquals("testuser", table.getValue(0, "user_name"));
	Assert.assertEquals(1, table.getValue(0, "version"));
	Assert.assertEquals("draft", table.getValue(0, "published_status"));
	Assert.assertNotNull(table.getValue(0, "created"));
	Assert.assertNotNull(table.getValue(0, "updated"));

	table = c.createQueryTable("EXPECTED_REF", "SELECT * FROM catalog_references where title='new_title'");
	Assert.assertEquals(1, table.getRowCount());

	c.close();
    }

    @Override
    protected IQueryUpdate<Object, SubstanceEndpointsBundle> deleteQuery() throws Exception {

	SubstanceEndpointsBundle adataset = new SubstanceEndpointsBundle();
	adataset.setID(1);
	DeleteBundle q = new DeleteBundle(adataset);
	return (IQueryUpdate<Object, SubstanceEndpointsBundle>) q;
    }

    @Override
    protected void deleteVerify(IQueryUpdate<Object, SubstanceEndpointsBundle> query) throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM bundle where idbundle=1");
	Assert.assertEquals(0, table.getRowCount());

	c.close();
    }

    @Override
    protected IQueryUpdate<Object, SubstanceEndpointsBundle> updateQuery() throws Exception {
	SubstanceEndpointsBundle adataset = new SubstanceEndpointsBundle();
	adataset.setID(1);
	adataset.setName("nina");
	adataset.setTitle("EURAS.BE");
	adataset.setDescription("description");
	adataset.setLicenseURI(ISourceDataset.license.CC0_1_0.getURI());
	return new UpdateBundle(adataset);
    }

    @Override
    protected void updateVerify(IQueryUpdate<Object, SubstanceEndpointsBundle> query) throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable("EXPECTED",
			"SELECT licenseURI,title,url,type,description FROM bundle join catalog_references using(idreference) where name='nina'");
	Assert.assertEquals(1, table.getRowCount());
	Assert.assertEquals("description", table.getValue(0, "description"));
	Assert.assertEquals(ISourceDataset.license.CC0_1_0.getURI(), table.getValue(0, "licenseURI"));
	table = c.createQueryTable("EXPECTED", "SELECT * FROM bundle where name='Dataset 1'");
	Assert.assertEquals(0, table.getRowCount());
	c.close();
    }

    @Test
    public void testCreateNew() throws Exception {
	IQueryUpdate<Object, SubstanceEndpointsBundle> query = createQueryNew();
	setUpDatabase(dbFile);
	IDatabaseConnection c = getConnection();
	executor.setConnection(c.getConnection());
	executor.open();
	executor.process(query);
	createVerifyNew(query);
	c.close();
    }

    @Override
    protected IQueryUpdate<Object, SubstanceEndpointsBundle> createQueryNew() throws Exception {
	SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle();
	bundle.setID(1);
	return new CreateMatrixFromBundle(bundle, _matrix.matrix_working);
    }

    @Override
    protected void createVerifyNew(IQueryUpdate<Object, SubstanceEndpointsBundle> query) throws Exception {
	/*
	 * IDatabaseConnection c = getConnection(); ITable table =
	 * c.createQueryTable("EXPECTED_BUNDLE",
	 * "SELECT idbundle,licenseURI FROM bundle where name='ambit'");
	 * Assert.assertEquals(1,table.getRowCount()); Object idtemplate =
	 * table.getValue(0,"idbundle"); Assert.assertNotNull(idtemplate);
	 * 
	 * table = c.createQueryTable("EXPECTED_REF",
	 * "SELECT * FROM catalog_references where title='new_title'");
	 * Assert.assertEquals(1,table.getRowCount());
	 * 
	 * c.close();
	 */

    }

    @Test
    public void testCreateNewVersion() throws Exception {
	SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle();
	bundle.setID(1);
	IQueryUpdate<Object, SubstanceEndpointsBundle> query = new CreateBundleVersion(bundle);
	setUpDatabase(dbFile);
	IDatabaseConnection c = getConnection();
	executor.setConnection(c.getConnection());
	executor.open();
	executor.process(query);
	createNewVersionVerify(query);
	c.close();
    }
    
    protected void createNewVersionVerify(IQueryUpdate<Object, SubstanceEndpointsBundle> query) throws Exception {

	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable("EXPECTED",
			"SELECT idbundle,version,hex(bundle_number) bn,published_status FROM bundle where bundle_number in (select bundle_number from bundle where idbundle=1) order by published_status");
	Assert.assertEquals(2, table.getRowCount());
	Assert.assertEquals("EFDB21BBE79F3286A988B6F6944D3734", table.getValue(0, "bn"));
	Assert.assertEquals("EFDB21BBE79F3286A988B6F6944D3734", table.getValue(1, "bn"));
	Assert.assertEquals("draft", table.getValue(0, "published_status"));
	Assert.assertEquals("archived", table.getValue(1, "published_status"));
	Assert.assertEquals(2, table.getValue(0, "version"));
	Assert.assertEquals(1, table.getValue(1, "version"));
	Assert.assertNotSame(1, query.getObject().getID());
	c.close();

    }

    @Test
    public void testCreateNewCopy() throws Exception {
	SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle();
	bundle.setUserName("test");
	bundle.setID(1);
	IQueryUpdate<Object, SubstanceEndpointsBundle> query = new CreateBundleCopy(bundle);
	setUpDatabase(dbFile);
	IDatabaseConnection c = getConnection();
	executor.setConnection(c.getConnection());
	executor.open();
	executor.process(query);
	createNewCopyVerify(query);
	c.close();
    }
    
    protected void createNewCopyVerify(IQueryUpdate<Object, SubstanceEndpointsBundle> query) throws Exception {

	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable("EXPECTED",
			"SELECT idbundle,version,hex(bundle_number) bn,published_status FROM bundle where name='Assessment' order by idbundle");
	Assert.assertEquals(2, table.getRowCount());
	Assert.assertEquals(new BigInteger("1"), table.getValue(0, "idbundle"));
	Assert.assertNotSame(new BigInteger("1"), table.getValue(1, "idbundle"));
	Assert.assertEquals("EFDB21BBE79F3286A988B6F6944D3734", table.getValue(0, "bn"));
	Assert.assertNotSame("EFDB21BBE79F3286A988B6F6944D3734", table.getValue(1, "bn"));
	Assert.assertEquals("draft", table.getValue(0, "published_status"));
	Assert.assertEquals("draft", table.getValue(1, "published_status"));
	Assert.assertEquals(1, table.getValue(0, "version"));
	Assert.assertEquals(1, table.getValue(1, "version"));
	Assert.assertNotSame(1, query.getObject().getID());
	c.close();

    }
}