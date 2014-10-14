package ambit2.rest.test.structure;

import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.restlet.data.MediaType;

import weka.core.Instances;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.test.ResourceTest;

import com.lowagie.text.pdf.PdfReader;

public class DatasetStructuresResourceTest extends ResourceTest {
		
		@Override
		public String getTestURI() {
			return String.format("http://localhost:%d/dataset/1/compound", port);
		}

		@Test
		public void testRDFXML() throws Exception {
			testGet(getTestURI(),MediaType.APPLICATION_RDF_XML);
		}			
		
			/*
		 * (non-Javadoc)
		 * @see ambit2.rest.test.ResourceTest#verifyResponseARFF(java.lang.String, org.restlet.data.MediaType, java.io.InputStream)
		 */
		@Override
		public Instances verifyResponseARFF(String uri, MediaType media, InputStream in)
				throws Exception {
			Instances instances = super.verifyResponseARFF(uri, media, in);
			Assert.assertEquals(4,instances.numInstances());
			//Assert.assertEquals("Dataset",instances.relationName());
			//Assert.assertEquals("1530-32-1",instances.firstInstance().stringValue(0));
			Assert.assertEquals("URI",instances.attribute(0).name());
			return instances;
		}		
		@Test
		public void testARFF() throws Exception {
			testGet(getTestURI(),ChemicalMediaType.WEKA_ARFF);
		}			
		/*
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
			return count >0;
		}	
		*/
		@Test
		public void testSDF() throws Exception {
			testGet(getTestURI(),ChemicalMediaType.CHEMICAL_MDLSDF);
		}
		@Override
		public boolean verifyResponseSDF(String uri, MediaType media, InputStream in)
				throws Exception {
			MyIteratingMDLReader reader = new MyIteratingMDLReader(in, SilentChemObjectBuilder.getInstance());
			int count = 0;
			while (reader.hasNext()) {
				Object o = reader.next();
				Assert.assertTrue(o instanceof IAtomContainer);
				IAtomContainer mol = (IAtomContainer)o;
				Assert.assertTrue(mol.getAtomCount()>0);
				count++;
			}
			return count==4;
		}		
		@Test
		public void testPDF() throws Exception {
			testGet(getTestURI(),MediaType.APPLICATION_PDF);
		}		
		@Override
		public boolean verifyResponsePDF(String uri, MediaType media, InputStream in)
				throws Exception {
			PdfReader reader = new PdfReader(in);
			Assert.assertNotNull(reader);
			Assert.assertTrue(reader.getNumberOfPages()>0);
			return true;
		}		
}
