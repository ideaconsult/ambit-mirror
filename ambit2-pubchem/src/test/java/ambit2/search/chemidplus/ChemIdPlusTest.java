package ambit2.search.chemidplus;

import junit.framework.Assert;

import org.junit.Test;

public class ChemIdPlusTest {
	
	@Test
	public void testFormatCas() throws Exception {
		ChemIdPlusRequest r = new ChemIdPlusRequest();
		Assert.assertEquals("0000050000",r.formatCAS("50-00-0"));
	}
	
	@Test
	public void testRetrieve() throws Exception {
		ChemIdPlusRequest r = new ChemIdPlusRequest();
		String mol = r.process("50-00-0");
		Assert.assertNotNull(mol);
		Assert.assertTrue(mol.indexOf("M  END")>0);
	}
}
