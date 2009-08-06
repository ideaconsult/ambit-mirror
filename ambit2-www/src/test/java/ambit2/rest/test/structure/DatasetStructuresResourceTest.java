package ambit2.rest.test.structure;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.restlet.data.MediaType;

import ambit2.rest.ChemicalMediaType;
import ambit2.rest.test.ResourceTest;

public class DatasetStructuresResourceTest extends ResourceTest {
		
		@Override
		public String getTestURI() {
			return String.format("http://localhost:%d/dataset/1/compound", port);
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
				Assert.assertEquals(21,mol.getAtomCount());
				Assert.assertEquals(18,mol.getBondCount());
				count++;
			}
			return count==1;
		}		
}
