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
	public void testRDFTurtle() throws Exception {
		testGet(getTestURI(),MediaType.APPLICATION_RDF_TURTLE);
	}	
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
					line.equals(String.format("http://localhost:%d%s/1",port,PropertyResource.featuredef)) ||
					line.equals(String.format("http://localhost:%d%s/2",port,PropertyResource.featuredef)) ||
					line.equals(String.format("http://localhost:%d%s/3",port,PropertyResource.featuredef))
					);
			count++;
		}
		return count==3;
	}	
	

}
