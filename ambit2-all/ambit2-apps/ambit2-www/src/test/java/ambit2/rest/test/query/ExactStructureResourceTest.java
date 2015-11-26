package ambit2.rest.test.query;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.query.ExactStructureQueryResource;
import ambit2.rest.rdf.RDFStructuresIterator;
import ambit2.rest.test.ResourceTest;

public class ExactStructureResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/query%s?search=%s&feature_uris[]=%s", port,
				ExactStructureQueryResource.resource,
				//"/smarts",
				Reference.encode("[Br-].CC[P+](c1ccccc1)(c2ccccc2)c3ccccc3"),
				Reference.encode(String.format("http://localhost:%d/feature/3", port)));
	}
	@Test
	public void testSDF() throws Exception {
		testGet(getTestURI(),ChemicalMediaType.CHEMICAL_MDLSDF);
	}	
	@Override
	public boolean verifyResponseSDF(String uri, MediaType media, InputStream in)
			throws Exception {
			IteratingSDFReader reader = new IteratingSDFReader(in, SilentChemObjectBuilder.getInstance());
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
	
	@Test
	public void testRDFXML() throws Exception {
		Reference ref = new Reference(getTestURI());
		/*
		Reference ref = new Reference(
				String.format("http://localhost:%d/dataset/1?%s=%s", 
						port,
						OpenTox.params.feature_uris.toString(),
						Reference.encode(String.format("http://localhost:%d%s", port,	PropertyResource.featuredef))
						));
						*/
		RDFStructuresIterator iterator = new RDFStructuresIterator(ref);
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d",port)));
		while (iterator.hasNext()) {
			IStructureRecord target = iterator.next();
			Assert.assertTrue(target.getIdchemical()>0);
			Assert.assertTrue(target.getIdstructure()>0);
			Assert.assertNotNull(target.getRecordProperties());
			for (Property p : target.getRecordProperties()) {
				System.out.println(p);
				System.out.println(p.getId());
				System.out.println(target.getRecordProperty(p));
			}
			Assert.assertNotNull(target.getContent());
			Assert.assertEquals(MOL_TYPE.SDF.toString(),target.getFormat());			
		}

	}		
}
