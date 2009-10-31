package ambit2.rest.test.property;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.rest.property.PropertyDOMParser;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.test.ResourceTest;

/**
 * test for {@link PropertyResource}
 * @author nina
 *
 */
public class AllFeatureDefinitionsTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s", port,PropertyResource.featuredef);
	}
	
	@Test
	public void testXML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_XML);
	}
	/*
<?xml version="1.0" encoding="UTF-8"?><FeatureDefinitions xmlns="http://opentox.org/1.0"><FeatureDefinition ID="1" Name="Property 1" Reference="8" type="TODO"><link href="http://localhost:8181/feature_definition/1"/><Reference xmlns="http://www.opentox.org/Reference/1.0" AlgorithmID="NA" ID="8" Name="Dummy"/></FeatureDefinition></FeatureDefinitions>
	 */
	
	@Override
	public boolean verifyResponseXML(String uri, MediaType media, InputStream in)
			throws Exception {

		Document doc = createDOM(in);
        PropertyDOMParser parser = new PropertyDOMParser() {
        	@Override
        	public void handleItem(Property entry) throws AmbitException {
        		System.out.println(entry);
        		switch (entry.getId()) {
        		case 1: {
            		Assert.assertEquals("Property 1",entry.getName());
            		Assert.assertEquals(8,entry.getReference().getId());      
            		break;
        		}
        		case 2: {
            		Assert.assertEquals("Property 2",entry.getName());
            		Assert.assertEquals(8,entry.getReference().getId());   
            		break;
        		}
        		case 3: {
            		Assert.assertEquals("CAS",entry.getName());
            		Assert.assertEquals(8,entry.getReference().getId());  
            		break;
        		}
        		default: Assert.fail("Undefined id "+entry.getId());
        		}
        		//Assert.assertEquals(1,entry.getId());
        		//Assert.assertEquals("Property 1",entry.getName());
        		//Assert.assertEquals(8,entry.getReference().getId());
        	}
        };
        parser.parse(doc);
        return true;
	}	
	/*
	@Override
	public boolean verifyResponseXML(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			System.out.println(line);
			count++;
		}
		return count>0;
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
	public void testURIRepresentation() throws Exception {
		testGetRepresentation(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	@Override
	public boolean verifyRepresentation(String uri, MediaType media,
			Representation representation) throws Exception {
		
		ReferenceList list = new ReferenceList(representation);
		for (Reference ref : list ) {
			System.out.println("\n");
			System.out.println(ref.getBaseRef());
			System.out.println(ref.getFragment());
			System.out.println(ref.getExtensions());
			System.out.println(ref.getHierarchicalPart());
			System.out.println(ref.getHostDomain());
			System.out.println(ref.getIdentifier());
			System.out.println(ref.getLastSegment());
			System.out.println(ref.getMatrix());
			System.out.println(ref.getAuthority());
			System.out.println(ref.getPath());
			System.out.println(ref.getQuery());
			System.out.println(ref.getRelativePart());
			System.out.println(ref.getRemainingPart());
			System.out.println(ref.getScheme());
			System.out.println(ref.getSegments());
		}
		return true;
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
			Assert.assertTrue(
					line.equals(String.format("http://localhost:%d/feature_definition/1",port)) ||
					line.equals(String.format("http://localhost:%d/feature_definition/2",port)) ||
					line.equals(String.format("http://localhost:%d/feature_definition/3",port))
					);
			count++;
		}
		return count==3;
	}	
	

}
