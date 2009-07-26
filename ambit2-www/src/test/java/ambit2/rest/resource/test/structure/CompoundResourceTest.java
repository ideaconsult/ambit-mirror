package ambit2.rest.resource.structure;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.restlet.data.MediaType;

import com.lowagie.text.pdf.PdfReader;

import ambit2.base.io.DownloadTool;
import ambit2.core.data.MoleculeTools;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.resource.test.ResourceTest;

public class CompoundResourceTest extends ResourceTest {
	
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/compound/10", port);
	}
	@Test
	public void testCML() throws Exception {
		testGet(getTestURI(),ChemicalMediaType.CHEMICAL_CML);
	}
	@Override
	public boolean verifyResponseCML(String uri, MediaType media, InputStream in)
			throws Exception {
		IMolecule mol = MoleculeTools.readCMLMolecule(new InputStreamReader(in));
		Assert.assertNotNull(mol);
		Assert.assertEquals(3,mol.getAtomCount());
		Assert.assertEquals(0,mol.getBondCount());
		return true;
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
			Assert.assertEquals(3,mol.getAtomCount());
			Assert.assertEquals(0,mol.getBondCount());
			count++;
		}
		return count==1;
	}
	@Test
	public void testSMILES() throws Exception {
		testGet(getTestURI(),ChemicalMediaType.CHEMICAL_SMILES);
	}	
	@Override
	public boolean verifyResponseSMILES(String uri, MediaType media,
			InputStream in) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			Assert.assertEquals("F.[F-].[Na+]	SMILES=F.[F-].[Na+]	metric=1.0",line);
			count++;
		}
		return count ==1;
	}
	@Test
	public void testPNG() throws Exception {
		testGet(getTestURI(),MediaType.IMAGE_PNG);
	}	
	@Override
	public boolean verifyResponsePNG(String uri, MediaType media, InputStream in)
			throws Exception {
		File file = new File("temp.png");
		file.delete();
		DownloadTool.download(in, file);
		Assert.assertTrue(file.exists());
		Image image = ImageIO.read(file);
		Assert.assertNotNull(image);
		file.delete();
		return true;
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
