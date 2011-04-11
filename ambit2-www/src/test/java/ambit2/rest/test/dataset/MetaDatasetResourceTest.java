package ambit2.rest.test.dataset;

import java.io.StringWriter;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.opentox.rdf.OT;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.StringRepresentation;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.dataset.MetadataRDFReporter;
import ambit2.rest.rdf.RDFMetaDatasetIterator;
import ambit2.rest.test.ResourceTest;

import com.hp.hpl.jena.ontology.OntModel;

public class MetaDatasetResourceTest extends ResourceTest {

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/dataset/1/metadata", port);
	}

	@Test
	public void testRDFXML() throws Exception {
		Reference ref = new Reference(
				String.format("http://localhost:%d/dataset/1/metadata", 
						port
						));
						
		RDFMetaDatasetIterator iterator = new RDFMetaDatasetIterator(ref);
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d",port)));
		int count = 0;
		while (iterator.hasNext()) {
			ISourceDataset target = iterator.next();
			Assert.assertEquals(1,target.getID());
			Assert.assertEquals("Dbunit dataset", target.getName());
			Assert.assertEquals("XLogP", target.getSource());
			if (target instanceof SourceDataset)
				Assert.assertEquals("XLogP reference", ((SourceDataset)target).getURL());
			count++;
		}
		Assert.assertEquals(1,count);
		iterator.close();
	}	
	
	@Test
	public void testUpdateExistingEntry() throws Exception {
		//name and license uri can be updated only (for now)
		String name = "New dataset name";
		String title = "AAAA";
		String url = "BBBB";
		SourceDataset p = new SourceDataset(name,new LiteratureEntry(title,url));
		p.setLicenseURI(ISourceDataset.license.CC0_1_0.getURI());
		
		OntModel model = OT.createModel();
		MetadataRDFReporter.addToModel(model,p,new DatasetURIReporter(new Reference(String.format("http://localhost:%d", port)),null));
		StringWriter writer = new StringWriter();
		model.write(writer,"RDF/XML");
		System.out.println(writer.toString());
		
		Response response =  testPut(
					getTestURI(),
					MediaType.TEXT_URI_LIST,
					new StringRepresentation(writer.toString(),MediaType.APPLICATION_RDF_XML)
					);
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT * FROM src_dataset join catalog_references using(idreference) where name='%s' and licenseURI='%s' ",
						name,
						ISourceDataset.license.CC0_1_0.getURI()));
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		Assert.assertEquals("http://localhost:8181/dataset/1/metadata", response.getLocationRef().toString());
	}
	
	@Test
	public void testUpdateExistingEntryWWW() throws Exception {
		//name and license uri can be updated only (for now)

		String name = "New dataset name";
		Form form = new Form();
		form.add("title",name);
		form.add("license", ISourceDataset.license.CC0_1_0.getURI());
		
		
		Response response =  testPut(
					getTestURI(),
					MediaType.TEXT_URI_LIST,
					form.getWebRepresentation()
					);
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT * FROM src_dataset join catalog_references using(idreference) where name='%s' and licenseURI='%s' ",
						name,
						ISourceDataset.license.CC0_1_0.getURI()));
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		Assert.assertEquals("http://localhost:8181/dataset/1/metadata", response.getLocationRef().toString());
	}
}
