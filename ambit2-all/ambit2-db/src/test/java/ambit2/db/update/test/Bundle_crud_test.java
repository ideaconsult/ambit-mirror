package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.CreateBundle;
import ambit2.db.update.bundle.DeleteBundle;
import ambit2.db.update.bundle.UpdateBundle;
import ambit2.db.update.bundle.matrix.CreateMatrixFromBundle;
import ambit2.db.update.bundle.matrix.ReadEffectRecordByBundleMatrix._matrix;

public class Bundle_crud_test extends CRUDTest<Object,SubstanceEndpointsBundle> {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dbFile = "src/test/resources/ambit2/db/processors/test/dataset-properties1.xml";			
	}
	@Override
	public void setUpDatabase(String xmlfile) throws Exception {
		super.setUpDatabase(xmlfile);
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM bundle where idbundle=1");
		Assert.assertEquals(1,table.getRowCount());
		c.close();		
	}
	@Override
	protected IQueryUpdate<Object,SubstanceEndpointsBundle> createQuery() throws Exception {
		SubstanceEndpointsBundle adataset = new SubstanceEndpointsBundle();
		adataset.setName("ambit");
		adataset.setTitle("new_title");
		adataset.setURL("new_url");
		adataset.setLicenseURI(ISourceDataset.license.CC0_1_0.getURI());
		CreateBundle user = new CreateBundle();
		user.setObject(adataset);
		return user;
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,SubstanceEndpointsBundle> query) throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED_BUNDLE","SELECT idbundle,licenseURI FROM bundle where name='ambit'");
		Assert.assertEquals(1,table.getRowCount());
		Object idtemplate = table.getValue(0,"idbundle");
		Assert.assertNotNull(idtemplate);
		
		table = c.createQueryTable("EXPECTED_REF","SELECT * FROM catalog_references where title='new_title'");
		Assert.assertEquals(1,table.getRowCount());
				
		c.close();
	}

	@Override
	protected IQueryUpdate<Object,SubstanceEndpointsBundle> deleteQuery() throws Exception {

		SubstanceEndpointsBundle adataset = new SubstanceEndpointsBundle();
		adataset.setID(1);
		DeleteBundle q = new DeleteBundle(adataset);
		return  (IQueryUpdate<Object,SubstanceEndpointsBundle>) q;
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object,SubstanceEndpointsBundle> query) throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM bundle where idbundle=1");
		Assert.assertEquals(0,table.getRowCount());
	
		c.close();
	}

	@Override
	protected IQueryUpdate<Object,SubstanceEndpointsBundle> updateQuery() throws Exception {
		SubstanceEndpointsBundle adataset = new SubstanceEndpointsBundle();
		adataset.setID(1);		
		adataset.setName("nina");
		adataset.setTitle("EURAS.BE");
		adataset.setLicenseURI(ISourceDataset.license.CC0_1_0.getURI());
		return new UpdateBundle(adataset);
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,SubstanceEndpointsBundle> query) throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT licenseURI,title,url,type FROM bundle join catalog_references using(idreference) where name='nina'");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(ISourceDataset.license.CC0_1_0.getURI(),table.getValue(0, "licenseURI"));
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM bundle where name='Dataset 1'");
		Assert.assertEquals(0,table.getRowCount());		
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
	protected IQueryUpdate<Object, SubstanceEndpointsBundle> createQueryNew()
			throws Exception {
		SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle();
		bundle.setID(1);		
		return new CreateMatrixFromBundle(bundle,_matrix.matrix_working);
	}
	@Override
	protected void createVerifyNew(IQueryUpdate<Object, SubstanceEndpointsBundle> query)
			throws Exception {
		/*
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED_BUNDLE","SELECT idbundle,licenseURI FROM bundle where name='ambit'");
		Assert.assertEquals(1,table.getRowCount());
		Object idtemplate = table.getValue(0,"idbundle");
		Assert.assertNotNull(idtemplate);
		
		table = c.createQueryTable("EXPECTED_REF","SELECT * FROM catalog_references where title='new_title'");
		Assert.assertEquals(1,table.getRowCount());
				
		c.close();
		*/
		
	}

}