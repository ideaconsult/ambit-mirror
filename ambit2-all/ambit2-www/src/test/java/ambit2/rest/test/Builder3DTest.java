package ambit2.rest.test;

import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import ambit2.rest.ChemicalMediaType;
import ambit2.rest.algorithm.quantumchemical.Build3DResource;

public class Builder3DTest  extends ResourceTest  {

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/%s?search=CCC",port,Build3DResource.resource);
	}
	@Test
	public void testInvalidSmiles() throws Exception {
		Status status = testHandleError(String.format("http://localhost:%d/%s?search=ZZZ",port,Build3DResource.resource),ChemicalMediaType.CHEMICAL_MDLSDF);
		Assert.assertEquals(400,status.getCode());
	}	
	@Test
	public void testSDF() throws Exception {
		testGet(getTestURI(),ChemicalMediaType.CHEMICAL_MDLSDF);
	}
	@Override
	public boolean verifyResponseSDF(String uri, MediaType media, InputStream in)
			throws Exception {
		IteratingMDLReader reader = new IteratingMDLReader(in,SilentChemObjectBuilder.getInstance());
		while (reader.hasNext()) {
			Object object = reader.next();
			Assert.assertTrue(object instanceof IAtomContainer);
			IAtomContainer a = (IAtomContainer) object;
			String[] properties = {"EIGENVALUES","TOTAL ENERGY","NO. OF FILLED LEVELS","EHOMO",
					"FINAL HEAT OF FORMATION","IONIZATION POTENTIAL","CORE-CORE REPULSION","ELECTRONIC ENERGY",
					"MOLECULAR WEIGHT","ELUMO"};
			for (String property:properties)
				Assert.assertNotNull(a.getProperty(property));
		}
		return true;
	}	
	@Test
	public void testGetJavaObject() throws Exception {
	}
}
