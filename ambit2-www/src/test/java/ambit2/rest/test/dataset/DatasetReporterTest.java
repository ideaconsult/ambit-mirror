package ambit2.rest.test.dataset;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import weka.core.Attribute;
import weka.core.Instance;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.rest.dataset.RDFInstancesParser;
import ambit2.rest.dataset.RDFStructuresReader;
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
		Reference ref = new Reference(
				String.format("http://localhost:%d/dataset/1?%s=%s", 
						port,
						StructureQueryResource.feature_URI,
						Reference.encode(String.format("http://localhost:%d%s", port,	PropertyResource.featuredef))
						));
		RDFStructuresReader reader = new RDFStructuresReader(String.format("http://localhost:%d",port));
		reader.setProcessorChain(new ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor>());
		reader.getProcessorChain().add(new IProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target)
					throws AmbitException {
				Assert.assertTrue(target.getIdchemical()>0);
				Assert.assertTrue(target.getIdstructure()>0);
				Assert.assertNotNull(target.getProperties());
				Assert.assertNotNull(target.getContent());
				Assert.assertEquals(MOL_TYPE.SDF.toString(),target.getFormat());
				return target;
			}
			public boolean isEnabled() {
				return true;
			}
			public void setEnabled(boolean value) {
			}
			public long getID() {
				return 0;
			}
		});

		reader.process(ref);
		//MediaType.APPLICATION_RDF_XML);
	}	
	
	@Test
	public void testInstances() throws Exception {
		Reference ref = new Reference(
				//"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/15"
				"http://localhost:8081/dataset/1?max=3"
				/*
				String.format("http://localhost:%d/dataset/2?%s=%s", 
						port,
						StructureQueryResource.feature_URI,
						Reference.encode(String.format("http://localhost:%d%s", port,	PropertyResource.featuredef))
						)
					*/	
				);
		RDFInstancesParser reader = new RDFInstancesParser(String.format("http://localhost:%d",port)) {
			@Override
			public void afterProcessing(Reference target,
					Iterator<Instance> iterator) throws AmbitException {
				super.afterProcessing(target, iterator);
				System.out.println(instances);
			}
		};
		reader.setProcessorChain(new ProcessorsChain<Instance, IBatchStatistics, IProcessor>());
		reader.getProcessorChain().add(new IProcessor<Instance,Instance>() {
			
			public Instance process(Instance target)
					throws AmbitException {
				Enumeration e = target.enumerateAttributes();
				while (e.hasMoreElements()) {
					Object o = e.nextElement();
					try {
						System.out.print(o.toString());
						System.out.print(target.stringValue((Attribute)o));
						
					} catch (Exception x) {
						
					}
					System.out.print("\t");
				}
				System.out.println();
				return target;
			}
			public boolean isEnabled() {
				return true;
			}
			public void setEnabled(boolean value) {
			}
			public long getID() {
				return 0;
			}
		});

		reader.process(ref);
		//MediaType.APPLICATION_RDF_XML);
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
