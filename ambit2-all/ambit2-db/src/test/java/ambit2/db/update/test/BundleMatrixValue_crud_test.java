package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.study.ValueAnnotated;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.matrix.DeleteMatrixValue;

/**
 * We only test marking a value as deleted here
 * 
 * @author nina
 * 
 */
public class BundleMatrixValue_crud_test extends CRUDTest<SubstanceEndpointsBundle, ValueAnnotated> {
    @Override
    public void setUp() throws Exception {
	super.setUp();
	dbFile = "src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml";
    }

    @Override
    public void testCreate() throws Exception {
    }

    @Override
    public void testCreateNew() throws Exception {
    }

    @Override
    public void testUpdate() throws Exception {

    }

    @Override
    public void testDelete() throws Exception {
	IQueryUpdate<SubstanceEndpointsBundle,ValueAnnotated> query = deleteQuery();
	setUpDatabase(dbFile);
	
	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable("EXPECTED_BUNDLE",
			"SELECT idbundle,idresult,copied,deleted,remarks from bundle_substance_experiment where idbundle=1 and idresult=1");
	Assert.assertEquals(1, table.getRowCount());
	Assert.assertEquals(0,table.getValue(0,"deleted"));
	c.close();
	
	c = getConnection();
	executor.setConnection(c.getConnection());
	executor.open();
	Assert.assertTrue(executor.process(query)>=1);
	deleteVerify(query);
	c.close();
	
    }

    @Override
    protected IQueryUpdate<SubstanceEndpointsBundle, ValueAnnotated> createQuery() throws Exception {
	return null;
    }

    @Override
    protected IQueryUpdate<SubstanceEndpointsBundle, ValueAnnotated> createQueryNew() throws Exception {
	return null;
    }

    @Override
    protected IQueryUpdate<SubstanceEndpointsBundle, ValueAnnotated> updateQuery() throws Exception {
	return null;
    }

    @Override
    protected IQueryUpdate<SubstanceEndpointsBundle, ValueAnnotated> deleteQuery() throws Exception {
	SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
	ValueAnnotated value = new ValueAnnotated();
	value.setIdresult(1);
	value.setDeleted(true);
	value.setRemark("test");
	DeleteMatrixValue q = new DeleteMatrixValue(bundle,value);
	return q;
    }

    @Override
    protected void createVerify(IQueryUpdate<SubstanceEndpointsBundle, ValueAnnotated> query) throws Exception {
    }

    @Override
    protected void createVerifyNew(IQueryUpdate<SubstanceEndpointsBundle, ValueAnnotated> query) throws Exception {
    }

    @Override
    protected void updateVerify(IQueryUpdate<SubstanceEndpointsBundle, ValueAnnotated> query) throws Exception {
    }

    @Override
    protected void deleteVerify(IQueryUpdate<SubstanceEndpointsBundle, ValueAnnotated> query) throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c
		.createQueryTable("EXPECTED_BUNDLE",
			"SELECT idbundle,idresult,copied,deleted,remarks from bundle_substance_experiment where idbundle=1 and idresult=1");
	Assert.assertEquals(1, table.getRowCount());
	Assert.assertEquals(1,table.getValue(0,"deleted"));
	Assert.assertEquals("test",table.getValue(0,"remarks"));
	c.close();
    }

}
