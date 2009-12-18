package ambit2.rest.test.query;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.rest.ChemicalMediaType;
import ambit2.rest.test.ResourceTest;

public class SmartsResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/query/smarts?search=%s", port,
				Reference.encode("c1ccccc1"));
	}
	@Test
	public void testSDF() throws Exception {
		testGet(getTestURI(),ChemicalMediaType.CHEMICAL_MDLSDF);
	}	
	@Override
	public boolean verifyResponseSDF(String uri, MediaType media, InputStream in)
			throws Exception {
			IteratingMDLReader reader = new IteratingMDLReader(in, DefaultChemObjectBuilder.getInstance());
			int count = 0;
			while (reader.hasNext()) {
				Object o = reader.next();
				Assert.assertTrue(o instanceof IAtomContainer);
				IAtomContainer mol = (IAtomContainer)o;
				Assert.assertEquals(22,mol.getAtomCount());
				Assert.assertEquals(23,mol.getBondCount());
				count++;
			}
			return count==1;
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
		while ((line = r.readLine())!= null) {
			Assert.assertEquals(String.format("http://localhost:%d/compound/11/conformer/100215",port),line);
		}
		return true;
	}
}
