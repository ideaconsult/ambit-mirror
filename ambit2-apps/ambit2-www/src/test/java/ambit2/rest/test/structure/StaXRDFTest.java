package ambit2.rest.test.structure;

import java.io.InputStream;

import net.idea.restnet.c.task.ClientResourceWrapper;

import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.rdf.RDFStructuresIterator;
import ambit2.rest.test.ResourceTest;

import com.hp.hpl.jena.ontology.OntModel;

public class StaXRDFTest extends ResourceTest {
	@Override
	public String getTestURI() {
		//return String.format("http://localhost:%d/dataset/2?rdfwriter=stax", port);
		return String.format("http://localhost:%d/dataset/1?rdfwriter=jena&feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2",
				port,port,port);
	}
	//
	@Override
	protected void setDatabase() throws Exception {
		setUpDatabase("src/test/resources/src-datasets_model.xml");
	}
	/**
<pre>
<rdf:RDF
    xmlns:ot="http://www.opentox.org/api/1.1#"
    xmlns:ac="http://localhost:8181/compound/"
    xmlns:bx="http://purl.org/net/nknouf/ns/bibtex#"
    xmlns:otee="http://www.opentox.org/echaEndpoints.owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:ar="http://localhost:8181/reference/"
    xmlns:ad="http://localhost:8181/dataset/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:am="http://localhost:8181/model/"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:af="http://localhost:8181/feature/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
    xmlns:ota="http://www.opentox.org/algorithmTypes.owl#"
    xmlns="http://localhost:8181/"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:ag="http://localhost:8181/algorithm/"
  xml:base="http://localhost:8181/">
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#Dataset"/>
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#Compound"/>
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#Feature"/>
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#FeatureValue"/>
  <owl:Class rdf:about="http://www.opentox.org/api/1.1#DataEntry"/>
  <owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#dataEntry"/>
  <owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#compound"/>
  <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/description"/>
  <ot:Dataset rdf:about="query/smarts">
    <ot:dataEntry>
      <ot:DataEntry>
        <ot:compound>
          <ot:Compound rdf:about="compound/11/conformer/100215"/>
        </ot:compound>
      </ot:DataEntry>
    </ot:dataEntry>
  </ot:Dataset>
  <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/type"/>
  <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/title"/>
</rdf:RDF>
</pre>
<pre>
<?xml version="1.0" ?>
<rdf:RDF xmlns:ot="http://www.opentox.org/api/1.1#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:owl="http://www.w3.org/2002/07/owl#" xmlns:dc="http://purl.org/dc/elements/1.1/">
<owl:Class rdf:about="http://www.opentox.org/api/1.1#Dataset">
</owl:Class><owl:Class rdf:about="http://www.opentox.org/api/1.1#DataEntry">
</owl:Class><owl:Class rdf:about="http://www.opentox.org/api/1.1#Feature">
</owl:Class><owl:Class rdf:about="http://www.opentox.org/api/1.1#FeatureValue">
</owl:Class><owl:Class rdf:about="http://www.opentox.org/api/1.1#Compound">
</owl:Class><owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#compound">
</owl:ObjectProperty><owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#dataEntry">
</owl:ObjectProperty><owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#values">
</owl:ObjectProperty><owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#feature">
</owl:ObjectProperty><owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#hasSource">
</owl:ObjectProperty><owl:ObjectProperty rdf:about="http://www.opentox.org/api/1.1#acceptValue">
</owl:ObjectProperty><owl:DatatypeProperty rdf:about="http://www.opentox.org/api/1.1#units">
</owl:DatatypeProperty><owl:DatatypeProperty rdf:about="http://www.opentox.org/api/1.1#value">
</owl:DatatypeProperty><owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/description">
</owl:AnnotationProperty><owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/creator">
</owl:AnnotationProperty><owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/type">
</owl:AnnotationProperty><owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/title">
</owl:AnnotationProperty>
<ot:Dataset rdf:about="http://localhost:8181/query/smarts"></ot:Dataset></rdf:RDF>
</pre>
	 * @throws Exception
	 */
	@Test
	public void testSmartsQueryStax() throws Exception {
		String rdfwriter = "stax";
		String uri = 
		String.format("http://localhost:%d/query/smarts?rdfwriter=%s&search=%s",
				port,rdfwriter,Reference.encode("c1ccccc1"));
		
		RDFStructuresIterator i = new RDFStructuresIterator(new Reference(uri));
		i.setBaseReference(new Reference(String.format("http://localhost:%d", port)));
		int count = 0;

		while (i.hasNext()) {
			IStructureRecord record = i.next();

			Assert.assertTrue(record.getIdchemical()>0);
			Assert.assertTrue(record.getIdstructure()>0);

			count++;
		}
		i.close();
		Assert.assertEquals(1,count);

	}		
	@Test
	public void simpleTestRDFXML() throws Exception {
		//testGet(getTestURI(),MediaType.APPLICATION_RDF_XML);
		ClientResourceWrapper r = new ClientResourceWrapper(getTestURI());
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
		boolean propertyfound = false;
		while (i.hasNext()) {
			IStructureRecord record = i.next();

			Assert.assertTrue(record.getIdchemical()>0);
			Assert.assertTrue(record.getIdstructure()>0);
			int props = 0;
			for (Property p : record.getProperties()) {
				System.out.println(p.getName());
				propertyfound =propertyfound ||  "Property 1".equals(p.getName());
				props++;
			}
			System.out.println(props);
			count++;
		}
		i.close();
		Assert.assertEquals(4,count);
		Assert.assertTrue(propertyfound);

	}	
	@Override
	public OntModel verifyResponseRDFXML(String uri, MediaType media,
			InputStream in) throws Exception {

		return null;
	}
}
