package ambit2.rest.test.dataset;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.restlet.data.MediaType;

import ambit2.base.data.Property;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.StructureQueryResource;
import ambit2.rest.test.ResourceTest;

/**
 * ambit.acad.bg/query/scope/dataset/all/similarity/method/fingerprints/distance/tanimoto/threshold/0.5/smiles/CCC
 * @author nina
 *
 */
public class DatasetReporterTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/dataset/1", port);
	}

	@Test
	public void testRDFXML() throws Exception {
		testGet(String.format("http://localhost:%d/dataset/1?%s=http://localhost:%d%s", 
				port,
				StructureQueryResource.feature_URI,
				port,
				PropertyResource.featuredef)
				,MediaType.APPLICATION_RDF_XML);
	}	
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			/*
			Assert.assertTrue(
					line.equals("http://localhost:8181/compound/7") ||
					line.equals("http://localhost:8181/compound/10"));
					*/
			count++;
		}
		return count ==4;
	}			
	@Test
	public void testXML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_XML);
	}
	@Override
	public boolean verifyResponseXML(String uri, MediaType media, InputStream in)
			throws Exception {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return count ==1;
	}		
	@Test
	public void testCSV() throws Exception {
		testGet(String.format(
				"http://localhost:%d/dataset/1?%s=http://localhost:%d%s", 
				port,
				StructureQueryResource.feature_URI,
				port,
				PropertyResource.featuredef)
				,MediaType.TEXT_CSV);
	}
	@Override
	public boolean verifyResponseCSV(String uri, MediaType media, InputStream in)
			throws Exception {

		int count = 0;
		IteratingDelimitedFileReader reader = new IteratingDelimitedFileReader(in);
		while (reader.hasNext()) {
			Object o = reader.next();
			Assert.assertTrue(o instanceof IChemObject);
			
			Assert.assertNotNull(((IChemObject)o).getProperties());
			//Assert.assertEquals(4,((IChemObject)o).getProperties().size());
			count++;
		}
		in.close();
		return count==4;

	}			
	@Test
	public void testHTML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_HTML);
	}
	@Override
	public boolean verifyResponseHTML(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return count > 0;
	}		

}
