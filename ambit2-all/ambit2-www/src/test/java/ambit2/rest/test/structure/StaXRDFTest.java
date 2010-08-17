package ambit2.rest.test.structure;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.rdf.RDFStructuresIterator;
import ambit2.rest.test.ResourceTest;

import com.hp.hpl.jena.ontology.OntModel;

public class StaXRDFTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/dataset/1?jena=false", port);
	}
	
	@Test
	public void simpleTestRDFXML() throws Exception {
		//testGet(getTestURI(),MediaType.APPLICATION_RDF_XML);
		ClientResource r = new ClientResource(getTestURI());
		Representation rep = r.get(MediaType.APPLICATION_RDF_XML);
		Assert.assertEquals(Status.SUCCESS_OK,r.getStatus());
		Assert.assertTrue(rep.isAvailable());
		System.out.println(rep.getText());
		
		rep.release();
		r.release();
	}		
	@Test
	public void testRDFXML() throws Exception {
		//testGet(getTestURI(),MediaType.APPLICATION_RDF_XML);
		
		RDFStructuresIterator i = new RDFStructuresIterator(new Reference(getTestURI()));
		i.setBaseReference(new Reference(String.format("http://localhost:%d", port)));
		int count = 0;
		while (i.hasNext()) {
			IStructureRecord record = i.next();
			System.out.println(record);
			Assert.assertTrue(record.getIdchemical()>0);
			Assert.assertTrue(record.getIdstructure()>0);
			count++;
		}
		i.close();
		Assert.assertEquals(4,count);

	}	
	@Override
	public OntModel verifyResponseRDFXML(String uri, MediaType media,
			InputStream in) throws Exception {

		return null;
	}
}
