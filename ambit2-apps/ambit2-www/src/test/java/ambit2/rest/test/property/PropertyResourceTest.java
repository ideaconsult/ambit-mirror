package ambit2.rest.test.property;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import junit.framework.Assert;
import net.idea.restnet.rdf.ns.OT;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.opentox.dsl.OTFeature;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.db.search.property.RetrieveFieldNamesByAlias;
import ambit2.rest.OpenTox;
import ambit2.rest.property.PropertyRDFReporter;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.query.QueryResource;
import ambit2.rest.rdf.RDFPropertyIterator;
import ambit2.rest.reference.ReferenceURIReporter;
import ambit2.rest.test.ResourceTest;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.vocabulary.DC;

/**
 * test for {@link PropertyResource}
 * @author nina
 *
 */
public class PropertyResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s/1", port,PropertyResource.featuredef);
	}
	

	@Test
	public void testQueryName() throws Exception {
		RDFPropertyIterator iterator = new RDFPropertyIterator(new Reference(
				String.format("http://localhost:%d%s?%s=%s", port,
						PropertyResource.featuredef,QueryResource.search_param,"Property")
				));
		iterator.setCloseModel(true);
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d", port)));
		while (iterator.hasNext()) {
			Property p = iterator.next();
			Assert.assertTrue(p.getName().startsWith("Property"));

		}
		iterator.close();
	}	
	@Test
	public void testQuerySameAS() throws Exception {
		RDFPropertyIterator iterator = new RDFPropertyIterator(new Reference(
				String.format("http://localhost:%d%s?%s=%s", port,
						PropertyResource.featuredef,OpenTox.params.sameas.toString(),"CASRN")
				));
		iterator.setCloseModel(true);
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d", port)));
		while (iterator.hasNext()) {
			Property p = iterator.next();
			Assert.assertEquals("CAS",p.getName());
			Assert.assertEquals(3,p.getId());
		}
		iterator.close();
	}
	@Test
	public void testRDFXML() throws Exception {
		RDFPropertyIterator iterator = new RDFPropertyIterator(new Reference(getTestURI()));
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d", port)));
		while (iterator.hasNext()) {
			
			Property p = iterator.next();
			Assert.assertEquals("Property 1",p.getName());
			Assert.assertEquals(1,p.getId());
		}
		iterator.close();
	}	
	
	@Test
	public void testRDFXMLForeignURI() throws Exception {
		try {		
			RDFPropertyIterator iterator = new RDFPropertyIterator(new Reference("http://google.com"));
			iterator.setBaseReference(new Reference(String.format("http://localhost:%d", port)));
			while (iterator.hasNext()) {
				Property p = iterator.next();
				Assert.assertTrue(false);
			}
			iterator.close();
			Assert.assertTrue(false);
		} catch (Exception x) {
			Assert.assertTrue(true);
		}
	}		
	/*
	public boolean verifyResponseXML(String uri, MediaType media, InputStream in)
			throws Exception {

		Document doc = createDOM(in);
        PropertyDOMParser parser = new PropertyDOMParser() {
        	@Override
        	public void handleItem(Property entry) throws AmbitException {
        		Assert.assertEquals(1,entry.getId());
        		Assert.assertEquals("Property 1",entry.getName());
        		Assert.assertEquals(8,entry.getReference().getId());
        	}
        };
        parser.parse(doc);
        return true;
	}
	*/	

	@Test
	public void testHTML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_HTML);
	}
	@Override
	public boolean verifyResponseHTML(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			//Assert.assertEquals("1530-32-1 ", line);
			System.out.println(line);
			count++;
		}
		return count>1;
	}
	
	
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			Assert.assertEquals(
					String.format("http://localhost:%d%s/1",port,PropertyResource.featuredef)
							, line);
			count++;
		}
		return count==1;
	}	
	
	@Test
	public void testDeleteEntry() throws Exception {
		
		OTFeature feature = OTFeature.feature(String.format("http://localhost:%d%s/1", port,PropertyResource.featuredef));
		feature.delete();
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties where idproperty=1");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
	}	
	
	@Test
	public void testCopyEntry() throws Exception {
		
		Form form = new Form();  
		form.add(OpenTox.params.feature_uris.toString(),String.format("http://localhost:%d%s/1", port,PropertyResource.featuredef));
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					form.getWebRepresentation());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		Assert.assertEquals("http://localhost:8181/feature/1",response.getLocationRef().toString());
		System.out.println(response.getLocationRef());
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(3,table.getRowCount());
		c.close();
	}	
	@Test
	public void testCopyEntry1() throws Exception {
		OntModel model = OT.createModel();
		Property p = new Property(null);
		p.setId(1);
		
		Request q = new Request();
		q.setRootRef(new Reference(String.format("http://localhost:%d", port)));
		PropertyURIReporter reporter = new PropertyURIReporter(q);
		
		PropertyRDFReporter.addToModel(model, 
				p,
				reporter,
				new ReferenceURIReporter());
		StringWriter writer = new StringWriter();
		model.write(writer,"RDF/XML");
		
		Form form = new Form();  
		form.add(OpenTox.params.feature_uris.toString(),String.format("http://localhost:%d%s/1", port,PropertyResource.featuredef));
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(3,table.getRowCount());
		c.close();
	}		
	
	@Test
	public void testCreateEntry2() throws Exception {
		
		StringWriter writer = new StringWriter();
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("feature.rdf")));
        String line;
        while ((line = br.readLine()) != null) {
        	writer.append(line);
        }
        br.close();    		
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		Assert.assertEquals("http://localhost:8181/feature/4", response.getLocationRef().toString());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where name='cas' and comments='CasRN' and title='http://my.resource.org' and url='Default'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}		
	
	@Test
	public void testCreateEntry3() throws Exception {
		
		StringWriter writer = new StringWriter();
        BufferedReader br = new BufferedReader(
        		new InputStreamReader(getClass().getClassLoader().getResourceAsStream("feature1.rdf")));
        String line;
        while ((line = br.readLine()) != null) {
        	writer.append(line);
        }
        br.close();    		
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		Assert.assertEquals(response.getLocationRef().toString(),"http://localhost:8181/feature/4");
		
		//weird nondeterministic error in ambit.uni-plovdiv.bg
		for (int i=0; i < 100;i++) {
			response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					//String.format("http://localhost:8080/ambit2-www%s",PropertyResource.featuredef),
					//String.format("http://ambit.uni-plovdiv.bg:8080/ambit2%s",PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
			//System.out.println(response.getStatus());
			Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
			//Assert.assertEquals(response.getLocationRef().toString(),"http://localhost:8181/feature/http%3A%2F%2Fother.com%2Ffeature%2F200Default");
			
		}
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties where name='http://other.com/feature/200'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}	
	
	@Test
	public void testCreateEntryMNA() throws Exception {
		
		StringWriter writer = new StringWriter();
        BufferedReader br = new BufferedReader(
        		new InputStreamReader(getClass().getClassLoader().getResourceAsStream("featureMNA2.rdf")));
        String line;
        while ((line = br.readLine()) != null) {
        	writer.append(line);
        }
        br.close();    		
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		Assert.assertEquals(response.getLocationRef().toString(),"http://localhost:8181/feature/4");
		
		//weird nondeterministic error in ambit.uni-plovdiv.bg
		for (int i=0; i < 100;i++) {
			response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					//String.format("http://localhost:8080/ambit2-www%s",PropertyResource.featuredef),
					//String.format("http://ambit.uni-plovdiv.bg:8080/ambit2%s",PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
			//System.out.println(response.getStatus());
			Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
			//Assert.assertEquals(response.getLocationRef().toString(),"http://localhost:8181/feature/http%3A%2F%2Fother.com%2Ffeature%2F200Default");
			
		}
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,title,url,comments FROM properties join catalog_references using(idreference) where name='MNA descriptors' and url='http://www.ibmc.msk.ru/en/departments/30' and title='http://195.178.207.233/PASS/Pe.html'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}	
	
	@Test
	public void testCreateEntryMNA3() throws Exception {
		
		StringWriter writer = new StringWriter();
        BufferedReader br = new BufferedReader(
        		new InputStreamReader(getClass().getClassLoader().getResourceAsStream("featureMNA3.rdf")));
        String line;
        while ((line = br.readLine()) != null) {
        	writer.append(line);
        }
        br.close();    		
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		Assert.assertEquals(response.getLocationRef().toString(),"http://localhost:8181/feature/4");
		
		//weird nondeterministic error in ambit.uni-plovdiv.bg
		for (int i=0; i < 100;i++) {
			response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					//String.format("http://localhost:8080/ambit2-www%s",PropertyResource.featuredef),
					//String.format("http://ambit.uni-plovdiv.bg:8080/ambit2%s",PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
			//System.out.println(response.getStatus());
			Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
			//Assert.assertEquals(response.getLocationRef().toString(),"http://localhost:8181/feature/http%3A%2F%2Fother.com%2Ffeature%2F200Default");
			
		}
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,title,url,comments FROM properties join catalog_references using(idreference) where name='MNA descriptors' and url='http://www.ibmc.msk.ru/en/departments/30' and title='http://195.178.207.233/PASS/Pe.html'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}		
	/**
<pre>
<rdf:RDF
    xmlns:j.0="http://purl.org/net/nknouf/ns/bibtex#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
    xmlns:ot="http://www.opentox.org/api/1.1#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/" > 
  <rdf:Description rdf:about="http://www.opentox.org/api/1.1#units">
    <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#DatatypeProperty"/>
  </rdf:Description>
  <rdf:Description rdf:about="http://purl.org/net/nknouf/ns/bibtex#Entry">
    <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#Class"/>
  </rdf:Description>
  <rdf:Description rdf:about="http://www.opentox.org/api/1.1#hasSource">
    <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#ObjectProperty"/>
  </rdf:Description>
  <rdf:Description rdf:nodeID="A0">
    <rdfs:seeAlso>bbb</rdfs:seeAlso>
    <dc:title>aaa</dc:title>
    <rdf:type rdf:resource="http://purl.org/net/nknouf/ns/bibtex#Entry"/>
  </rdf:Description>
  <rdf:Description rdf:about="http://www.opentox.org/api/1.1#Feature">
    <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#Class"/>
  </rdf:Description>
  <rdf:Description rdf:nodeID="A1">
    <dc:type>http://www.w3.org/2001/XMLSchema#string</dc:type>
    <ot:hasSource rdf:nodeID="A0"/>
    <owl:sameAs>CasRN</owl:sameAs>
    <ot:units></ot:units>
    <dc:title>cas</dc:title>
    <rdf:type rdf:resource="http://www.opentox.org/api/1.1#Feature"/>
  </rdf:Description>
</rdf:RDF>

</pre>
	 * @throws Exception
	 */
	@Test
	public void testCreateEntry() throws Exception {

		OntModel model = OT.createModel();
		Property p = new Property("cas",new LiteratureEntry("aaa","bbb"));
		p.setNominal(true);
		PropertyRDFReporter.addToModel(model, 
				p,
				new PropertyURIReporter(new Reference(String.format("http://localhost:%d%s", port))),
				new ReferenceURIReporter());
		StringWriter writer = new StringWriter();
		model.write(writer,"RDF/XML");
		System.out.println(writer.toString());

		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
		//Assert.assertEquals("http://localhost:8181/feature/4", response.getLocationRef().toString());
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT * FROM properties join catalog_references using(idreference) where name='cas' and comments='%s' and title='aaa' and url='bbb' and isLocal=1",Property.opentox_CAS));

		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}	
	
	//this fails because of mysql foreign key constraints
	public void testUpdateExistingEntry1() throws Exception {

		Property p = new Property("Property 1",new LiteratureEntry("Dummy 1","NA"));
		p.setLabel("Test");
		p.setId(1);
		OntModel model = OT.createModel();
		PropertyRDFReporter.addToModel(model,p,
				new PropertyURIReporter(new Reference(String.format("http://localhost:%d%s", port))),
				new ReferenceURIReporter());
		StringWriter writer = new StringWriter();
		model.write(writer,"RDF/XML");

		Response response =  testPost(
					String.format("http://localhost:%d%s/2", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT * FROM properties join catalog_references using(idreference) where name='Property 1' and comments='%s' and title='Dummy' and url='NA'","http://www.opentox.org/api/1.1#Test"));
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		Assert.assertEquals("http://localhost:8181/feature/2", response.getLocationRef().toString());
	}	
	
	@Test
	public void testUpdateExistingEntry() throws Exception {
		//now we can only update by PUT, not POST
		Property p = new Property("Property 1",new LiteratureEntry("Dummy","NA"));
		p.setLabel("Test");
		p.setNominal(true);
		OntModel model = OT.createModel();
		PropertyRDFReporter.addToModel(model,p,
				new PropertyURIReporter(new Reference(String.format("http://localhost:%d%s", port))),
				new ReferenceURIReporter());
		StringWriter writer = new StringWriter();
		model.write(writer,"RDF/XML");

		Response response =  testPut(
					String.format("http://localhost:%d%s/1", port,PropertyResource.featuredef),
					MediaType.TEXT_URI_LIST,
					new StringRepresentation(writer.toString(),MediaType.APPLICATION_RDF_XML)
					);
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT isLocal FROM properties join catalog_references using(idreference) where name='Property 1' and comments='%s' and title='Dummy' and url='NA'","http://www.opentox.org/api/1.1#Test"));
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(true,table.getValue(0,"isLocal"));
		c.close();
		Assert.assertEquals("http://localhost:8181/feature/1", response.getLocationRef().toString());
	}	
	
	@Test
	public void testUpdateSameAs() throws Exception {
		IDatabaseConnection c = getConnection();
		String sameAs ="http://example.org/#testEntry";

		ITable table = 	c.createQueryTable("EXPECTED",String.format("SELECT * FROM properties where idproperty=1 and comments='%s'",sameAs));
		Assert.assertEquals(0,table.getRowCount());
		OTFeature feature = OTFeature.feature(
					String.format("http://localhost:%d%s/1", port,PropertyResource.featuredef));

		feature.setSameas(sameAs);
		
        
		table = 	c.createQueryTable("EXPECTED",String.format("SELECT * FROM properties where idproperty= 1 and comments='%s'",sameAs));
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
	}		
	@Test
	public void testCreateExistingEntry() throws Exception {

		Property p = new Property("CAS",new LiteratureEntry("Dummy","NA"));
		p.setLabel("CasRN");
		OntModel model = OT.createModel();
		PropertyRDFReporter.addToModel(model,p,
				new PropertyURIReporter(new Reference(String.format("http://localhost:%d%s", port))),
				new ReferenceURIReporter());
		StringWriter writer = new StringWriter();
		model.write(writer,"RDF/XML");

		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format("SELECT * FROM properties join catalog_references using(idreference) where name='CAS' and comments='%s' and title='Dummy' and url='NA'","http://www.opentox.org/api/1.1#CASRN"));
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		Assert.assertEquals("http://localhost:8181/feature/3", response.getLocationRef().toString());
	}	
	
	@Test
	public void testCreateEntry4() throws Exception {

		OntModel model = OT.createModel();
		//feature.addRDFType(OT.OTClass.Feature.createProperty(featureModel));
		Individual f = model.createIndividual(OT.OTClass.Feature.getOntClass(model));
		f.addProperty(model.createAnnotationProperty(DC.creator.getURI()), model.createTypedLiteral("http://opentox.ntua.gr"));

		f.setSameAs(model.createResource("http://other.com/feature/200", OT.OTClass.Feature.getOntClass(model)));

		
		StringWriter writer = new StringWriter();
		model.write(writer,"RDF/XML");

		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
        IDatabaseConnection c = getConnection();
        //don't have consistent way to store dc:creator, so we are ignoring it
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where name='http://other.com/feature/200' and comments='http://other.com/feature/200' and title='http://opentox.ntua.gr' and url='http://opentox.ntua.gr'");
		//ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where name='http://other.com/feature/200' and comments='http://other.com/feature/200' and title='http://opentox.ntua.gr' and url='http://localhost:8181'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}		
	
	@Test
	public void testCreateForeignEntry() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.feature_uris.toString(),"http://my.new.property.org");
		
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.TEXT_RDF_N3,
					form.getWebRepresentation());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where name='http://my.new.property.org' and comments='http://my.new.property.org' and title='http://my.new.property.org' and url='http://my.new.property.org'");
		Assert.assertEquals(1,table.getRowCount());

	}

	/*
	public void testParser() throws Exception {
		String xml = String.format( 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><FeatureDefinitions xmlns=\"http://opentox.org/1.0\">" +
			"<%s ID=\"1\" %s=\"Property 1\" %s=\"8\" %s=\"Property\">" +
			"<link href=\"http://localhost:%d%s/1\"/>" +
			"</%s></FeatureDefinitions>",
			XMLTags.node_featuredef,
			XMLTags.attr_name,
			XMLTags.node_reference,
			XMLTags.attr_type,
			port,
			PropertyResource.featuredef,			
			XMLTags.node_featuredef,
			//?
			port,
			XMLTags.node_reference);
			
		final List<Property> le = new ArrayList<Property>();
		Document doc = createDOM(new StringReader(xml));
		PropertyDOMParser parser = new PropertyDOMParser() {
        	@Override
        	public void handleItem(Property entry) throws AmbitException {
        		le.add(entry);

        	}
        };
        parser.parse(doc);
        Assert.assertEquals(1,le.size());
        Assert.assertEquals("Property 1",le.get(0).getName());
        Assert.assertEquals("Property",le.get(0).getLabel());
        Assert.assertEquals(8,le.get(0).getReference().getId());
	}	
	*/
	@Test
	public void testGetJavaObject() throws Exception {
		testGetJavaObject(String.format("http://localhost:%d%s?%s=%s", port,PropertyResource.featuredef,
				OpenTox.params.sameas.toString(),Reference.encode(Property.opentox_CAS)),
				MediaType.APPLICATION_JAVA_OBJECT,org.restlet.data.Status.SUCCESS_OK);
	}
	
	@Override
	public Object verifyResponseJavaObject(String uri, MediaType media,
			Representation rep) throws Exception {
		Object o = super.verifyResponseJavaObject(uri, media, rep);
		Assert.assertTrue(o instanceof RetrieveFieldNamesByAlias);
		RetrieveFieldNamesByAlias query = (RetrieveFieldNamesByAlias)o;
		Assert.assertEquals(Property.opentox_CAS,query.getValue());
		return o;
	}
	
	@Test
	public void testCreateEntryNTUA_1() throws Exception {
		testCreateEntryNTUA("ntua/from22198.rdf");
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,title,url,comments FROM properties join catalog_references using(idreference) where name='Scaled Feature http://apps.ideaconsult.net:8080/ambit2/feature/22198'");
		Assert.assertEquals(1,table.getRowCount());
		
		c.close();
	}
	
	@Test
	public void testCreateEntryNTUA_2() throws Exception {
		testCreateEntryNTUA("ntua/from22199.rdf");
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,title,url,comments FROM properties join catalog_references using(idreference) where name='Scaled Feature http://apps.ideaconsult.net:8080/ambit2/feature/22199'");
		Assert.assertEquals(1,table.getRowCount());
		
		c.close();
	}
	
	@Test
	public void testCreateEntryNTUA_3() throws Exception {
		testCreateEntryNTUA("ntua/from22200.rdf");
		//testCreateEntryNTUA("ntua/from22201.rdf");
		//testCreateEntryNTUA("ntua/from22202.rdf");
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,title,url,comments FROM properties join catalog_references using(idreference) where name='Scaled Feature http://apps.ideaconsult.net:8080/ambit2/feature/22200'");
		Assert.assertEquals(1,table.getRowCount());
		
		c.close();
	}
	public void testCreateEntryNTUA(String file) throws Exception {
		
		StringWriter writer = new StringWriter();
        BufferedReader br = new BufferedReader(
        		new InputStreamReader(getClass().getClassLoader().getResourceAsStream(file)));
        String line;
        while ((line = br.readLine()) != null) {
        	writer.append(line);
        }
        br.close();    		
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		Assert.assertEquals(response.getLocationRef().toString(),"http://localhost:8181/feature/4");
		
		/*
		//weird nondeterministic error in ambit.uni-plovdiv.bg
		for (int i=0; i < 100;i++) {
			response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					//String.format("http://localhost:8080/ambit2-www%s",PropertyResource.featuredef),
					//String.format("http://ambit.uni-plovdiv.bg:8080/ambit2%s",PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
			//System.out.println(response.getStatus());
			Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
			//Assert.assertEquals(response.getLocationRef().toString(),"http://localhost:8181/feature/http%3A%2F%2Fother.com%2Ffeature%2F200Default");
			
		}
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,title,url,comments FROM properties join catalog_references using(idreference) where name='MNA descriptors' and url='http://www.ibmc.msk.ru/en/departments/30' and title='http://195.178.207.233/PASS/Pe.html'");
		Assert.assertEquals(1,table.getRowCount());
		
		c.close();
		*/
	}	
	
}
