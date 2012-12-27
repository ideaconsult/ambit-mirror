package ambit2.rest.test.query;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.engine.util.Base64;

import ambit2.rest.AbstractResource;
import ambit2.rest.structure.CompoundLookup;
import ambit2.rest.test.ResourceTest;

public class CompoundLookupTest extends ResourceTest {
	private final String testInChI = 
		"InChI=1S/C20H20P.BrH/c1-2-21(18-12-6-3-7-13-18,19-14-8-4-9-15-19)20-16-10-5-11-17-20;/h3-17H,2H2,1H3;1H/q+1;/p-1";

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/query%s/search/all?search=%s&search=%s", port,
				CompoundLookup.resource,
				//"/smarts",
				Reference.encode("1530-32-1"),
				Reference.encode(String.format("50-00-0")));
	}
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	
	@Test
	public void testSingleInChI() throws Exception {
		String q = String.format("http://localhost:%d/query%s/search/all?search=%s", port,
				CompoundLookup.resource,
				Reference.encode(testInChI)
				);
		testGet(q,MediaType.TEXT_URI_LIST);
	}
	
	@Test
	public void testSingleMol() throws Exception {
		InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
		InChIToStructure p = factory.getInChIToStructure(testInChI,SilentChemObjectBuilder.getInstance());
		IAtomContainer ac = p.getAtomContainer();
		StringWriter w = new StringWriter();
		MDLV2000Writer writer = new MDLV2000Writer(w);
		writer.write(ac);
		writer.close();
		
		String query = Base64.encode(w.getBuffer().toString().getBytes("UTF-8"),false);
		String q = String.format("http://localhost:%d/query%s/search/all?%s=%s", port,
				CompoundLookup.resource,
				AbstractResource.b64search_param,
				Reference.encode(query)
				);
		testGet(q,MediaType.TEXT_URI_LIST);
	}
	
	@Test
	public void testSingleCAS() throws Exception {
		String q = String.format("http://localhost:%d/query%s/search/all?search=%s", port,
				CompoundLookup.resource,
				//"/smarts",
				Reference.encode("1530-32-1")
				);
		testGet(q,MediaType.TEXT_URI_LIST);
	}
	
	@Test
	public void testSingleName() throws Exception {
		String q = String.format("http://localhost:%d/query%s/%s", port,
				CompoundLookup.resource,
				//"/smarts",
				Reference.encode("1530-32-1")
				);
		testGet(q,MediaType.TEXT_URI_LIST);
	}	
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = r.readLine())!= null) {
			Assert.assertTrue(line.startsWith(String.format("http://localhost:%d/compound/11",port)));
		}
		return true;
	}
}
