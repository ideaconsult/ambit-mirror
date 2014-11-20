package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.db.update.bundle.endpoints.AddEndpointToBundle;
import ambit2.db.update.bundle.endpoints.DeleteEndpointFromBundle;

public class BundleEndpoints_crud_test extends CRUDTest<SubstanceEndpointsBundle,SubstanceProperty> {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dbFile = "src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml";			
	}
	@Override
	protected IQueryUpdate<SubstanceEndpointsBundle, SubstanceProperty> createQuery()
			throws Exception {
		AddEndpointToBundle query = new AddEndpointToBundle();
		query.setGroup(new SubstanceEndpointsBundle(1));
		SubstanceProperty p = new SubstanceProperty("TOX","TO_SENSITIZATION_SECTION",null,"Default");
		query.setObject(p);
		return query;
	}

	@Override
	protected IQueryUpdate<SubstanceEndpointsBundle, SubstanceProperty> createQueryNew()
			throws Exception {
		AddEndpointToBundle query = new AddEndpointToBundle();
		query.setGroup(new SubstanceEndpointsBundle(1));
		SubstanceProperty p = new SubstanceProperty("TOX","TO_SKIN_IRRITATION_SECTION",null,"Default");
		query.setObject(p);
		return query;		
	}

	@Override
	protected IQueryUpdate<SubstanceEndpointsBundle, SubstanceProperty> updateQuery()
			throws Exception {
		AddEndpointToBundle query = new AddEndpointToBundle();
		query.setGroup(new SubstanceEndpointsBundle(1));
		SubstanceProperty p = new SubstanceProperty("P-CHEM","PC_BOILING_SECTION",null,"Default");
		query.setObject(p);
		return query;		
	}

	@Override
	protected IQueryUpdate<SubstanceEndpointsBundle, SubstanceProperty> deleteQuery()
			throws Exception {
		DeleteEndpointFromBundle query = new DeleteEndpointFromBundle();
		query.setGroup(new SubstanceEndpointsBundle(1));
		SubstanceProperty p = new SubstanceProperty("P-CHEM","PC_BOILING_SECTION",null,"Default");
		query.setObject(p);
		return query;	
	}

	@Override
	protected void createVerify(
			IQueryUpdate<SubstanceEndpointsBundle, SubstanceProperty> query)
			throws Exception {
		 IDatabaseConnection c = getConnection();	
		 ITable table = 	c.createQueryTable("EXPECTED_BUNDLE",
				 "SELECT idbundle,topcategory,endpointcategory,endpointhash from bundle_endpoints where idbundle=1 and endpointcategory='TO_SENSITIZATION_SECTION'");
		 Assert.assertEquals(1,table.getRowCount());
  		 c.close();
	}

	@Override
	protected void createVerifyNew(
			IQueryUpdate<SubstanceEndpointsBundle, SubstanceProperty> query)
			throws Exception {
		 IDatabaseConnection c = getConnection();	
		 ITable table = 	c.createQueryTable("EXPECTED_BUNDLE","SELECT idbundle,topcategory,endpointcategory,endpointhash from bundle_endpoints where idbundle=1 and endpointcategory='TO_SKIN_IRRITATION_SECTION'" );
		 Assert.assertEquals(1,table.getRowCount());
  		 c.close();
	}

	@Override
	protected void updateVerify(
			IQueryUpdate<SubstanceEndpointsBundle, SubstanceProperty> query)
			throws Exception {
		 IDatabaseConnection c = getConnection();	
		 ITable table = 	c.createQueryTable("EXPECTED_BUNDLE",
				 "SELECT idbundle,topcategory,endpointcategory,endpointhash from bundle_endpoints where idbundle=1 and endpointcategory='PC_BOILING_SECTION'");
		 Assert.assertEquals(1,table.getRowCount());
  		 c.close();		
	}

	@Override
	protected void deleteVerify(
			IQueryUpdate<SubstanceEndpointsBundle, SubstanceProperty> query)
			throws Exception {
		 IDatabaseConnection c = getConnection();	
		 ITable table = 	c.createQueryTable("EXPECTED_BUNDLE",
				 "SELECT idbundle,topcategory,endpointcategory,endpointhash from bundle_endpoints where idbundle=1 and endpointcategory='PC_BOILING_SECTION'");
		 Assert.assertEquals(0,table.getRowCount());
  		 c.close();		
	}

}