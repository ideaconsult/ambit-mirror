package ambit2.rest.test.dataset;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.MissingFeatureValuesResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.rdf.RDFStructuresIterator;
import ambit2.rest.test.ResourceTest;

public class MissingFeatureValuesResourceTest extends ResourceTest {

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/query%s", port,MissingFeatureValuesResource.resource);
	}
	protected Reference createMissingValuesReference() {
		Form form = new Form();
		form.add(OpenTox.params.feature_uris.toString(),
				String.format("http://localhost:%d%s/3", port,	PropertyResource.featuredef));
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/1",port));

		Reference ref = new Reference(getTestURI());
		ref.setQuery(form.getQueryString());
		return ref;
	}

	@Test
	public void testCreateDatasetStructuresMissingValuesOnly() throws Exception {
		Reference ref = createMissingValuesReference();
		Form form = new Form();
		form.add(OpenTox.params.dataset_uri.toString(),ref.toString());
		testPost(String.format("http://localhost:%d/dataset", port),MediaType.APPLICATION_WWW_FORM,form.getWebRepresentation());
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
			Assert.assertNotNull(target.getProperties());
			/*
			for (Property p : target.getProperties()) {
				System.out.println(p);
				System.out.println(p.getId());
				System.out.println(target.getProperty(p));
			}
			*/
			Assert.assertNotNull(target.getContent());
			Assert.assertEquals(MOL_TYPE.SDF.toString(),target.getFormat());
			count++;
		}
		Assert.assertEquals(3,count);
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM query join query_results using(idquery) where idquery=3");
		Assert.assertEquals(3,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT idproperty FROM query join template_def using(idtemplate) where idquery=3");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(3,table.getValue(0,"idproperty"));
		c.close();		
	}	
}
