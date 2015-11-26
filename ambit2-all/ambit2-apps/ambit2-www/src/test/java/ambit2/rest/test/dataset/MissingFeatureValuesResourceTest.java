package ambit2.rest.test.dataset;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.rest.OpenTox;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.rdf.RDFStructuresIterator;
import ambit2.rest.task.CallableDatasetCreator;
import ambit2.rest.test.ResourceTest;

public class MissingFeatureValuesResourceTest extends ResourceTest {

	@Override
	public String getTestURI() {
		return createMissingValuesReference().toString();
	}
	protected Reference createMissingValuesReference() {
		return CallableDatasetCreator.createFilterReference(
				String.format("http://localhost:%d", port),
				String.format("http://localhost:%d/dataset/1",port),
				new String[] {String.format("http://localhost:%d%s/3", port,	PropertyResource.featuredef)});
	}

	@Test
	public void testCreateDatasetStructuresMissingValuesOnly() throws Exception {
		Reference ref = createMissingValuesReference();
		Form form = new Form();
		form.add(OpenTox.params.dataset_uri.toString(),ref.toString());
		Response r = testPost(String.format("http://localhost:%d/dataset", port),MediaType.APPLICATION_WWW_FORM,form.getWebRepresentation());
		r.release();
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM query join query_results using(idquery) where idquery=3");
		Assert.assertEquals(3,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT idproperty FROM query join template_def using(idtemplate) where idquery=3");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(3,table.getValue(0,"idproperty"));
		c.close();				
	}	
	
	

	@Test
	public void testRDFXML() throws Exception {

		RDFStructuresIterator iterator = new RDFStructuresIterator(createMissingValuesReference());
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d",port)));
		int count = 0;
		while (iterator.hasNext()) {
			IStructureRecord target = iterator.next();
			Assert.assertTrue(target.getIdchemical()>0);
			Assert.assertTrue(target.getIdstructure()>0);
			Assert.assertNotNull(target.getRecordProperties());

			Assert.assertNotNull(target.getContent());
			Assert.assertEquals(MOL_TYPE.SDF.toString(),target.getFormat());
			count++;
		}
		Assert.assertEquals(3,count);
		

	}	
	
	
	@Test
	public void testCreateEmpty() throws Exception {
		
		Reference ref = CallableDatasetCreator.createFilterReference(
				String.format("http://localhost:%d", port),
				String.format("http://localhost:%d/compound/11/conformer/100215",port),
				new String[] {String.format("http://localhost:%d%s/3", port,	PropertyResource.featuredef)});
		
		 createMissingValuesReference();
		Form form = new Form();
		form.add(OpenTox.params.dataset_uri.toString(),ref.toString());
		testAsyncTask(String.format("http://localhost:%d/dataset", port),
				form,
				Status.SUCCESS_OK, 
				"http://localhost:8181/dataset/R3"
				);
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM query");
		Assert.assertEquals(2,table.getRowCount());
		c.close();			
	}
	@Override
	public void testGetJavaObject(String uri, MediaType media,
			Status expectedStatus) throws Exception {
		// TODO Auto-generated method stub
		super.testGetJavaObject(uri, media, expectedStatus);
	}
}
