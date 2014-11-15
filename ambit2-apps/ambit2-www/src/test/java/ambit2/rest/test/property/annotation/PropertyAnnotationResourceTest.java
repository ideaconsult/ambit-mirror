package ambit2.rest.test.property.annotation;

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
import ambit2.rest.property.annotations.PropertyAnnotationResource;
import ambit2.rest.reference.ReferenceURIReporter;
import ambit2.rest.test.ResourceTest;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * test for {@link PropertyAnnotationResource}
 * @author nina
 *
 */
public class PropertyAnnotationResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s/1/annotation", port,PropertyResource.featuredef);
	}
	
	protected void setDatabase() throws Exception {
		setUpDatabase("src/test/resources/src-datasets_model.xml");
	}
	/*
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
	*/
	/*
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

	
}
