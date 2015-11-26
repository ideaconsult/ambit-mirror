package ambit2.rest.test.dataset;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import weka.core.Instances;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.rest.OpenTox;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.rdf.RDFInstancesIterator;
import ambit2.rest.rdf.RDFStructuresIterator;
import ambit2.rest.test.ResourceTest;

/**
 * 
 * @author nina
 *
 */
public class DatasetReporterTest extends ResourceTest {
	protected String policyID ;
	@Override
	public void setUp() throws Exception {
		super.setUp();
		try {
			//policyID = createPolicy(getTestURI());
		} catch (Exception x) {
			throw x;
		}
	}
	
	@Override
	public void tearDown() throws Exception {

		
		try {
			//if (policyID != null) deletePolicy(policyID);
		} catch (Exception x) { x.printStackTrace(); }
		super.tearDown();
	}
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/dataset/1", port);
	}

	@Test
	public void testRDFXML() throws Exception {
		Reference ref = new Reference(
				String.format("http://localhost:%d/dataset/1?%s=%s", 
						port,
						OpenTox.params.feature_uris.toString(),
						Reference.encode(String.format("http://localhost:%d%s", port,	PropertyResource.featuredef))
						));

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
	@Test
	public void testN3() throws Exception {
			testGet(getTestURI(),MediaType.TEXT_RDF_N3);

	}
	
	@Override
	public boolean verifyResponseRDFN3(String uri, MediaType media,
			InputStream in) throws Exception {

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
		return count >0;
	}			
	
	@Test
	public void testParseInstances() throws Exception {
		Reference ref = new Reference(DatasetReporterTest.class.getResource("/input.rdf"));
		RDFInstancesIterator  iterator = new RDFInstancesIterator(ref);
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d",port)));
		while (iterator.hasNext()) {
			iterator.next();
		}
		iterator.close();
		Instances instances = iterator.getInstances();
	
		Assert.assertEquals("http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/6", instances.relationName());
		Assert.assertEquals(522, instances.numInstances());
		Assert.assertEquals(4, instances.numAttributes());
		Assert.assertTrue(instances.attribute(0).isString());
		Assert.assertTrue(instances.attribute(1).isNumeric());
		Assert.assertTrue(instances.attribute(2).isNominal());
		Assert.assertTrue(instances.attribute(3).isNumeric());
		Assert.assertEquals("CompoundURI",instances.attribute(0).name());
		Assert.assertEquals("http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11938",instances.attribute(1).name());
		Assert.assertEquals("http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11936",instances.attribute(2).name());
		Assert.assertEquals("http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11951",instances.attribute(3).name());
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
	public void testCSV() throws Exception {
		testGet(String.format(
				"http://localhost:%d/dataset/1?%s=http://localhost:%d%s", 
				port,
				OpenTox.params.feature_uris.toString(),
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
