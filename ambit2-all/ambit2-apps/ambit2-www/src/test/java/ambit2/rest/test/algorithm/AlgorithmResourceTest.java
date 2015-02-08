package ambit2.rest.test.algorithm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;

import junit.framework.Assert;
import net.idea.opentox.cli.OTClient;
import net.idea.opentox.cli.structure.Compound;
import net.idea.opentox.cli.structure.CompoundClient;
import net.idea.restnet.c.task.ClientResourceWrapper;

import org.apache.http.HttpStatus;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTModel;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import ambit2.base.config.Preferences;
import ambit2.base.data.Property;
import ambit2.rest.OpenTox;
import ambit2.rest.rdf.RDFPropertyIterator;
import ambit2.rest.test.ResourceTest;

public class AlgorithmResourceTest extends ResourceTest {
    @BeforeClass
    public static void prepare() throws Exception {
        setLogLevel(Level.INFO);
    }
	@Override
	protected void setDatabase() throws Exception {
		setUpDatabase("src/test/resources/num-datasets.xml");
	}
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/algorithm", port);
	}
	
	public void testWADL() throws Exception {
		testGet(String.format("http://localhost:%d/algorithm/pka", port),MediaType.APPLICATION_WADL);
	}	
	
	
	public void testWADLApp() throws Exception {
		testGet(String.format("http://localhost:%d", port),MediaType.APPLICATION_WADL);
	}
	@Override
	public boolean verifyResponseWADL(String uri, MediaType media,
			InputStream in) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			count++;
			logger.info(line);
		}
		return false;
	}
	@Test
	public void testRDFXML() throws Exception {
		testGet(getTestURI(),MediaType.APPLICATION_RDF_XML);
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
			count++;
		}
		return count == 139;
	}	
	/**
	 * fails because of not finding the Freemarker templates.
	 * @throws Exception
	 */
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
			count++;
		}
		return count > 0;
	}	
	
	@Test
	public void testJSON() throws Exception {
		testGet(getTestURI(),MediaType.APPLICATION_JSON);
	}
	@Override
	public boolean verifyResponseJSON(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			count++;
		}
		return count > 0;
	}		
	

	@Test
	public void testCalculateCPSA() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		Reference ref = testAsyncTask(
				String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.CPSADescriptor",port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						));
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FXLogPorg.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor"));
		
		int count = 0;
		RDFPropertyIterator i = new RDFPropertyIterator(ref);
		i.setCloseModel(true);
		while (i.hasNext()) {
			count++;
		}
		i.close();
		Assert.assertEquals(29,count);
	}	
	
	@Test
	public void testCalculateEStateFP() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		Reference ref = testAsyncTask(
				String.format("http://localhost:%d/algorithm/ambit2.descriptors.fingerprints.EStateFingerprinterWrapper",port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						));
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FXLogPorg.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor"));
		
		int count = 0;
		RDFPropertyIterator i = new RDFPropertyIterator(ref);
		i.setCloseModel(true);
		while (i.hasNext()) {
			count++;
		}
		i.close();
		Assert.assertEquals(1,count);
	}
	@Test
	public void testCalculateDragon() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		Reference ref = testAsyncTask(
				String.format("http://localhost:%d/algorithm/ambit2.dragon.DescriptorDragonShell/MW",port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						));
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FXLogPorg.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor"));
		
		int count = 0;
		RDFPropertyIterator i = new RDFPropertyIterator(ref);
		i.setCloseModel(true);
		while (i.hasNext()) {
			count++;
		}
		i.close();
		Assert.assertEquals(4885,count);
	}	
	
	@Test
	public void testCalculateLogP() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		Reference ref = testAsyncTask(
				String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						));
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FXLogPorg.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor"));
		
		ref = testAsyncTask(
				String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						));
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FXLogPorg.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor"));
			
		int count = 0;
		RDFPropertyIterator i = new RDFPropertyIterator(ref);
		i.setCloseModel(true);
		while (i.hasNext()) {
			count++;
		}
		i.close();
		Assert.assertEquals(1,count);
	}	
	
	@Test
	public void testMOPACBalloon() throws Exception {
		Form headers = new Form();  
		Reference model = testAsyncTask(
				String.format("http://localhost:%d/algorithm/ambit2.mopac.MopacShellBalloon", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,
						"3"
						));
		
		
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		Reference ref = testAsyncTask(
				model.toString(),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						));
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FXLogPorg.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor"));
		
		ref = testAsyncTask(
				String.format("http://localhost:%d/algorithm/ambit2.mopac.MopacOriginalStructure", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F4%2Fpredicted"
						));
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FXLogPorg.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor"));
			
		int count = 0;
		RDFPropertyIterator i = new RDFPropertyIterator(ref);
		i.setCloseModel(true);
		while (i.hasNext()) {
			count++;
		}
		i.close();
		Assert.assertEquals(9,count);
	}		
	
	
	
	public void testMOPAC() throws Exception {
		Form headers = new Form();  
		Reference model = testAsyncTask(
				String.format("http://localhost:%d/algorithm/ambit2.mopac.MopacShell", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,
						"3"
						));
		
		
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		Reference ref = testAsyncTask(
				model.toString(),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						));
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FXLogPorg.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor"));
		
		ref = testAsyncTask(
				String.format("http://localhost:%d/algorithm/ambit2.mopac.MopacOriginalStructure", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F4%2Fpredicted"
						));
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FXLogPorg.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor"));
			
		int count = 0;
		RDFPropertyIterator i = new RDFPropertyIterator(ref);
		i.setCloseModel(true);
		while (i.hasNext()) {
			count++;
		}
		i.close();
		Assert.assertEquals(9,count);
	}		
	@Test
	public void testTautomers() throws Exception {

		OTClient otclient = new OTClient();
		CompoundClient cli = otclient.getCompoundClient();
		Compound substance = new Compound();
		substance.setName("warfarin");
		String inchi = "InChI=1S/C19H16O4/c1-12(20)11-15(13-7-3-2-4-8-13)17-18(21)14-9-5-6-10-16(14)23-19(17)22/h2-10,15,21H,11H2,1H3";
		String smiles = "CC(=O)CC(c1ccccc1)c2c(c3ccccc3oc2=O)O";
		substance.setInChI(inchi);
		substance.setSMILES(smiles);
		URL serviceRoot = new URL(String.format("http://localhost:%d/",port));
		net.idea.opentox.cli.task.RemoteTask task =  cli.registerSubstanceAsync(serviceRoot, substance, null, null);
		task.waitUntilCompleted(500);

		Assert.assertNotNull(task.getResult());
		Assert.assertEquals(HttpStatus.SC_OK,task.getStatus());
		Assert.assertNull(task.getError());
		Assert.assertTrue(task.getResult().toExternalForm().startsWith(String.format("http://localhost:%d/compound/",port)));
		

		
		Form headers = new Form();
		Reference model = testAsyncTask(
				String.format("http://localhost:%d/algorithm/tautomers", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,
						"3"
						));
		
		
		headers.add("dataset_uri",String.format(task.getResult().toExternalForm(), port));
		String expected = String.format("http://localhost:%d/query/relation/compound/HAS_TAUTOMER?dataset_uri=%s", port,
									Reference.encode(task.getResult().toExternalForm()));
		Reference ref = testAsyncTask(model.toString(),headers, Status.SUCCESS_OK,expected);
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM chem_relation");
		Assert.assertEquals(9,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset join struc_dataset using(id_srcdataset) where name='TAUTOMERS'");
		Assert.assertEquals(9,table.getRowCount());
		c.close();
		
		List list = cli.listURI(new URL(expected));
		Assert.assertEquals(9,list.size());

		//now try it again
		ref = testAsyncTask(model.toString(),headers, Status.SUCCESS_OK,expected);
		c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT idchemical1,idchemical2,relation,metric FROM chem_relation");
		Assert.assertEquals(9,table.getRowCount());
		Assert.assertNotNull(table.getValue(0,"metric"));
		c.close();
		
		
		otclient.close();
	}	
	@Test
	public void testCalculateFingerprints() throws Exception {
		
        IDatabaseConnection c = getConnection();	
        Connection connection = c.getConnection();
        Statement t = connection.createStatement();
        t.executeUpdate("delete from fp1024");
        t.close();
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from fp1024");
		Assert.assertEquals(0,table.getRowCount());
		
		c.close();			
		Form headers = new Form();  
		//headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/fingerprints", port),
				headers, Status.SUCCESS_OK,
				"");		
				//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2FBCUT%2Bdescriptors%2Fpredicted"));
		
        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT bc from fp1024");
		Assert.assertEquals(4,table.getRowCount());
		c.close();			
	}		
	
	@Test
	public void testStructureQualityWorkflow() throws Exception {
		
        IDatabaseConnection c = getConnection();	
        Connection connection = c.getConnection();
        Statement t = connection.createStatement();
        t.executeUpdate("delete from fp1024_struc");
        t.close();
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from fp1024_struc");
		Assert.assertEquals(0,table.getRowCount());
		
		c.close();			
		Form headers = new Form();  
		//headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/structurequality", port),
				headers, Status.SUCCESS_OK,
				"");		
				//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2FBCUT%2Bdescriptors%2Fpredicted"));
		
        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT bc from fp1024_struc");
		Assert.assertEquals(5,table.getRowCount());
		c.close();			
	}		
	
	@Test
	public void testSK1024Generator() throws Exception {
		
        IDatabaseConnection c = getConnection();	
        Connection connection = c.getConnection();
        Statement t = connection.createStatement();
        t.executeUpdate("delete from sk1024");
        t.close();
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from sk1024");
		Assert.assertEquals(0,table.getRowCount());
		
		c.close();			
		Form headers = new Form();  
		//headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/struckeys", port),
				headers, Status.SUCCESS_OK,
				"");		
				//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2FBCUT%2Bdescriptors%2Fpredicted"));
		
        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT bc from sk1024");
		Assert.assertEquals(4,table.getRowCount());
		c.close();			
	}		
	
	@Test
	public void testSmartsAccelerator() throws Exception {
		
        IDatabaseConnection c = getConnection();	
        Connection connection = c.getConnection();
        Statement t = connection.createStatement();
        t.executeUpdate("update structure set atomproperties=null");
        t.close();
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from structure where atomproperties is null");
		Assert.assertEquals(5,table.getRowCount());
		
		c.close();			
		Form headers = new Form();  
		//headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/smartsprop", port),
				headers, Status.SUCCESS_OK,
				"");		
				//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2FBCUT%2Bdescriptors%2Fpredicted"));
		
        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * from structure where atomproperties is not null");
		Assert.assertEquals(5,table.getRowCount());
		c.close();			
	}		
	
	@Test
	public void testInChIChemicals() throws Exception {
		
        IDatabaseConnection c = getConnection();	
        Connection connection = c.getConnection();
        Statement t = connection.createStatement();
        t.executeUpdate("update chemicals set inchi=null,inchikey=null");
        t.close();
        t = connection.createStatement();
        t.executeUpdate("update structure set preference=10000,structure='' where idstructure=100214");
        t.close();
        t = connection.createStatement();
        t.executeUpdate("update structure set structure='' where idchemical=11");
        t.close();                
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from chemicals where inchi is null");
		Assert.assertEquals(4,table.getRowCount());
		
		c.close();			
		Form headers = new Form();  
		headers.add("label","UNKNOWN");
		//headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/inchi", port),
				headers, Status.SUCCESS_OK,
				"");		
				//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2FBCUT%2Bdescriptors%2Fpredicted"));
		
        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * from chemicals where inchi is not null");
		Assert.assertEquals(3,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT idchemical from chemicals where inchi is null and inchikey is null");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(11,table.getValue(0,"idchemical"));	
		c.close();			
	}		
	@Test
	public void testGenerateInChI() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/ambit2.descriptors.InChI", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTw-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTw-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTc-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTc-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTp-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTp-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor"
						));		
				//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2FBCUT%2Bdescriptors%2Fpredicted"));
        IDatabaseConnection c = getConnection();	
        Connection connection = c.getConnection();
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from properties");
		Assert.assertEquals(6,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * from property_values");
		Assert.assertEquals(19,table.getRowCount());		
		c.close();			
	}	
	
	@Test
	public void testAtomEnvironments() throws Exception {
		
        IDatabaseConnection c = getConnection();	
        Connection connection = c.getConnection();
        Statement t = connection.createStatement();
        t.executeUpdate("delete from fpaechemicals");
        t.close();
		Form headers = new Form();  
		//headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/atomenvironments", port),
				headers, Status.SUCCESS_OK,
				"");		
				//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2FBCUT%2Bdescriptors%2Fpredicted"));
		
        c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from fpaechemicals group by idchemical");
		Assert.assertEquals(4,table.getRowCount());
		c.close();			
	}			
	@Test
	public void testCalculateSOME() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/ambit2.some.DescriptorSOMEShell", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTw-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTw-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTc-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTc-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTp-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTp-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor"
						));		
				//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2FBCUT%2Bdescriptors%2Fpredicted"));
		
		//http://localhost:8080/model/53?dataset_uri=http://localhost:8080/compound/151221/conformer/253534&media=image/png
	}
	
	@Test
	public void testCalculateBCUT() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTw-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTw-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTc-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTc-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTp-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTp-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor"
						));		
				//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2FBCUT%2Bdescriptors%2Fpredicted"));
	}	
	
	@Test
	/**
	 * https://sourceforge.net/tracker/?func=detail&aid=3434335&group_id=191756&atid=938657
	 */
	public void testCalculateIP() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.IPMolecularLearningDescriptor", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTw-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTw-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTc-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTc-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTp-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTp-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor"
						));		
				//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2FBCUT%2Bdescriptors%2Fpredicted"));
	}	
	
	@Test
	public void testCalculateAminoAcidCount() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		Reference ref = testAsyncTask(
				String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.AminoAcidCountDescriptor", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						));
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FXLogPorg.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor"));
		
		ref = testAsyncTask(
				String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.AminoAcidCountDescriptor", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						));
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FXLogPorg.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor"));
			
		int count = 0;
		RDFPropertyIterator i = new RDFPropertyIterator(ref);
		i.setCloseModel(true);
		while (i.hasNext()) {
			count++;
		}
		i.close();
		Assert.assertEquals(20,count);
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT count(*) c,group_concat(distinct(status)) s FROM	properties\n"+
				"join property_values v using(idproperty)\n"+
				"left join property_string using(idvalue_string)\n"+
				"join template_def t using(idproperty) join models on t.idtemplate=models.predicted and\n"+
				"models.name='Number of each amino acid in an atom container'\n"
				);
		Assert.assertEquals(new BigInteger("80"),table.getValue(0,"c"));
		Assert.assertEquals("OK",table.getValue(0,"s"));
		c.close();	
		
		
	}	
	
	
	public void testLoad() throws Exception {
		for (int i=0; i < 100;i++) {
			Form headers = new Form();  
			headers.add(OpenTox.params.dataset_uri.toString(), 
					"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/6?feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11938&feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11937&max=100000&feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11948&max=10");
			headers.add(OpenTox.params.target.toString(),
					"http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11948");
			testPost(
					String.format("http://localhost:%d/algorithm/J48", port),
					MediaType.TEXT_URI_LIST,
							//Reference.encode(String.format("http://localhost:%d/dataset/1",port))),
					headers.getWebRepresentation());
			
		}
	}
	/*
	@Test
	public void testSMSD() throws Exception {
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/mcss", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		//prediction

		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * from properties where name='CC'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * from properties join property_values using(idproperty) join catalog_references using(idreference) where name='CC' and type='Model'");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
	}		
*/


//	http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/R7798
	@Test
	public void testLocalRegressionRemoteDataset() throws Exception {
		
	
		
		Form headers = new Form();  
		Reference dataset = new Reference("http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/12?feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/21692&feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/21691&max=100");

		
		headers.add(OpenTox.params.dataset_uri.toString(),dataset.toString());

		headers.add(OpenTox.params.target.toString(),"http://ambit.uni-plovdiv.bg:8080/ambit2/feature/21692");
		
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/LR", port),
						//Reference.encode(String.format("http://localhost:%d/dataset/1",port))),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
	
		headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(),dataset.toString());
		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
						//Reference.encode(String.format("http://localhost:%d/dataset/1",port))),
				headers, Status.SUCCESS_OK,
				"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/12?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted");
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT * from properties where name='caco2'");
		Assert.assertEquals(2,table.getRowCount());
		
		ClientResourceWrapper client = new ClientResourceWrapper(new Reference(String.format("http://localhost:%d/model/3", port)));

		Representation r = client.get(MediaType.TEXT_PLAIN);
		logger.info(r.getText());
		r.release();
		client.release();
		
		table = 	c.createQueryTable("EXPECTED",
		"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name='caco2'");
		Assert.assertEquals(7,table.getRowCount());
   }	
		
	@Test
	public void testLocalRegressionDescriptors() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		Reference dataset = testAsyncTask(
				String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						));
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FXLogPorg.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor"));
		
		
		headers.removeAll(OpenTox.params.dataset_uri.toString());
		dataset.addQueryParameter(OpenTox.params.feature_uris.toString(), String.format("http://localhost:%d/feature/2", port));
		
		headers.add(OpenTox.params.dataset_uri.toString(),dataset.toString());

		headers.add(OpenTox.params.target.toString(),
				String.format("http://localhost:%d/feature/2",port));
		
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/LR", port),
						//Reference.encode(String.format("http://localhost:%d/dataset/1",port))),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"4"));
	
		headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(),dataset.toString());
		testAsyncTask(
				String.format("http://localhost:%d/model/4", port),
						//Reference.encode(String.format("http://localhost:%d/dataset/1",port))),
				headers, Status.SUCCESS_OK,
				"http://localhost:8181/dataset/1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F2&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F4%2Fpredicted");
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT * from properties where name='Property 2'");
		Assert.assertEquals(2,table.getRowCount());
		
		Assert.fail("second run, try if predictions already exists");
		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
				headers, Status.SUCCESS_OK,
				"http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F2&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted");
			
		

		table = 	c.createQueryTable("EXPECTED",
		"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name='Property 2'");
		Assert.assertEquals(7,table.getRowCount());
   }			
	@Test
	public void testLocalRegression() throws Exception {
		Form features = new Form();
		for (int i=1 ; i < 3; i++)
			features.add(OpenTox.params.feature_uris.toString(),String.format("http://localhost:%d/feature/%d",port,i));
		
		Form headers = new Form();  
		Reference dataset = new Reference(String.format("http://localhost:%d/dataset/1",port));
		dataset.setQuery(features.getQueryString());
		
		headers.add(OpenTox.params.dataset_uri.toString(),dataset.toString());

		headers.add(OpenTox.params.target.toString(),
				String.format("http://localhost:%d/feature/2",port));
		
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/LR", port),
						//Reference.encode(String.format("http://localhost:%d/dataset/1",port))),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
	
		headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(),dataset.toString());
		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
						//Reference.encode(String.format("http://localhost:%d/dataset/1",port))),
				headers, Status.SUCCESS_OK,
				"http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F2&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted");
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT * from properties where name='Property 2'");
		Assert.assertEquals(2,table.getRowCount());
		
		ClientResourceWrapper client = new ClientResourceWrapper(new Reference(String.format("http://localhost:%d/model/3", port)));

		Representation r = client.get(MediaType.TEXT_PLAIN);
		logger.info(r.getText());
		r.release();
		client.release();
		
		Assert.fail("second run, try if predictions already exists");
		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
				headers, Status.SUCCESS_OK,
				"http://localhost:8181/dataset/1?feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F1&feature_uris%5B%5D=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2F2&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted");
			
		

		table = 	c.createQueryTable("EXPECTED",
		"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name='Property 2'");
		Assert.assertEquals(7,table.getRowCount());
   }		
	


	@Test
	public void testClustering() throws Exception {
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(), 
				"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/12?feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/21692&feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/21691&max=100");
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/SimpleKMeans", port),
						//Reference.encode(String.format("http://localhost:%d/dataset/1",port))),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));

		
	}	
	
	@Test
	public void testRanges() throws Exception {
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/pcaRanges", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));

		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/1%s", port,
						String.format("%s","?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted")));

	}		
	@Test
	public void testDistanceEuclidean() throws Exception {
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:%d/dataset/1", port));
		headers.add(OpenTox.params.confidenceOf.toString(), 
				String.format("http://example.com/opentox/feature/xxx")); //completely fictious example
		
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/distanceEuclidean", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));

		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/1%s", port,
						String.format("%s","?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted")));

        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name='Euclidean distance'");
		Assert.assertEquals(4,table.getRowCount());
		
		table = 	c.createQueryTable("EXPECTED",
		"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name='AppDomain_Euclidean distance'");
		Assert.assertEquals(4,table.getRowCount());		
		
		table = 	c.createQueryTable("EXPECTED",
		"SELECT rdf_type,predicate,object FROM property_annotation join properties using(idproperty) where name='AppDomain_Euclidean distance'");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("ModelConfidenceFeature",table.getValue(0,"rdf_type"));
		Assert.assertEquals("confidenceOf",table.getValue(0,"predicate"));
		Assert.assertEquals("http://example.com/opentox/feature/xxx",table.getValue(0,"object"));		
	}			
	
	@Test
	public void testDistanceCityBlock() throws Exception {
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/distanceCityBlock", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));

		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/1%s", port,
						String.format("%s","?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted")));

	}		
	
	@Test
	public void testDistanceMahalanobis() throws Exception {
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/distanceMahalanobis", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));

		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/1%s", port,
						String.format("%s","?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted")));

	}	
	
	@Test
	public void testFingerprintsAD() throws Exception {
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:%d/dataset/1", port));
		Reference model = testAsyncTask(
				String.format("http://localhost:%d/algorithm/fptanimoto", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		System.out.println(model);
		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/1%s", port,
						String.format("%s","?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted")));

        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name='Tanimoto'");
		Assert.assertEquals(4,table.getRowCount());
		
		table = 	c.createQueryTable("EXPECTED",
		"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name='AppDomain_Tanimoto'");
		Assert.assertEquals(4,table.getRowCount());		
	}		
	
	@Test
	public void testFingerprintsADConfidence() throws Exception {
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:%d/dataset/1", port));
		headers.add(OpenTox.params.confidenceOf.toString(), 
				String.format("http://example.com/opentox/feature/xxx")); //completely fictious example
				
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/fptanimoto", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));

		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/1%s", port,
						String.format("%s","?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted")));

        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name='Tanimoto'");
		Assert.assertEquals(4,table.getRowCount());
		
		table = 	c.createQueryTable("EXPECTED",
		"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name='AppDomain_Tanimoto'");
		Assert.assertEquals(4,table.getRowCount());		
		
		table = 	c.createQueryTable("EXPECTED",
		"SELECT idproperty,rdf_type,predicate,object FROM property_annotation join properties using(idproperty) where name='AppDomain_Tanimoto'");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("ModelConfidenceFeature",table.getValue(0,"rdf_type"));
		Assert.assertEquals("confidenceOf",table.getValue(0,"predicate"));
		Assert.assertEquals("http://example.com/opentox/feature/xxx",table.getValue(0,"object"));
		
		String  uri = String.format("http://localhost:%d/feature/%d", port, table.getValue(0,"idproperty"));
		//String  uri = String.format("http://localhost:%d/model/3/predicted", port);
		/*
		ClientResource cli = new ClientResource(uri);
		Representation r = cli.get(MediaType.APPLICATION_RDF_XML);
		System.out.println(r.getText());
		r.release();
		cli.release();
		*/
		
		RDFPropertyIterator pi = new RDFPropertyIterator(new Reference(uri));
		while (pi.hasNext()) {
			Property p = pi.next();
			//System.out.println(p.getAnnotations());
			Assert.assertNotNull(p.getAnnotations());
		}
		pi.close();
		
	}	
	
	@Test
	public void testnparamdensity() throws Exception {
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/nparamdensity", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));

		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/1%s", port,
						String.format("%s","?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted")));

        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name='Probability density'");
		Assert.assertEquals(4,table.getRowCount());
		
		table = 	c.createQueryTable("EXPECTED",
		"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name='AppDomain_Probability density'");
		Assert.assertEquals(4,table.getRowCount());

		c.close();				
	}	
	//algorithm/PCA is commented 
	@Test
	public void testPCA() throws Exception {
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/PCA", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));

		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/1%s", port,
						String.format("%s","?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted")));
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name regexp '^PCA_'");
		Assert.assertEquals(4,table.getRowCount());
		
		table = 	c.createQueryTable("EXPECTED",
		"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name regexp '^PCA_'");
		Assert.assertEquals(4,table.getRowCount());

		c.close();			
		
	}	
	
	@Test
	public void testLeverage() throws Exception {
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:%d/dataset/1?max=3", port));
		Reference model = testAsyncTask(
				String.format("http://localhost:%d/algorithm/leverage", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		System.out.println(model);
		headers.removeAll(OpenTox.params.dataset_uri.toString());
		headers.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:%d/dataset/1", port));		
		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/1%s", port,
						String.format("%s","?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted")));
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name='Leverage'");
		Assert.assertEquals(4,table.getRowCount());
		
		table = 	c.createQueryTable("EXPECTED",
		"SELECT name,idstructure,idchemical FROM values_all join structure using(idstructure) where name='AppDomain_Leverage'");
		Assert.assertEquals(4,table.getRowCount());

		table = 	c.createQueryTable("INDOMAIN",
		"SELECT idproperty,name,value_num FROM property_values join properties using(idproperty) where name='AppDomain_Leverage' and value_num=0");
		Assert.assertEquals(3,table.getRowCount());	
		
		table = 	c.createQueryTable("OUTOFDOMAIN",
		"SELECT idproperty,name,value_num FROM property_values join properties using(idproperty) where name='AppDomain_Leverage' and value_num=1 and idstructure=129345");
		Assert.assertEquals(1,table.getRowCount());		
		c.close();			
		
	}		
	
	
	public void testLeverageLocal() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:8080/dataset/67?feature_uris[]=http://localhost:8080/feature/13719&feature_uris[]=http://localhost:8080/feature/13720", port));
				//String.format("http://localhost:8080/dataset/67/smarts?text=Tr&feature_uris[]=http://localhost:8080/feature/13719&feature_uris[]=http://localhost:8080/feature/13720", port));
		
		RemoteTask taskTrain = new RemoteTask(
					new Reference("http://localhost:8080/algorithm/leverage"),
					MediaType.APPLICATION_WWW_FORM,
					form.getWebRepresentation(),
					Method.POST);
		while (taskTrain.poll()) {
			logger.info(taskTrain.getStatus().toString());
		}

		Form testData = new Form();  
		testData.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:8080/dataset/67?feature_uris[]=http://localhost:8080/feature/13719&feature_uris[]=http://localhost:8080/feature/13720", port));
		//String.format("http://localhost:8080/dataset/67/smarts?text=Te&feature_uris[]=http://localhost:8080/feature/13719&feature_uris[]=http://localhost:8080/feature/13720", port));
		
		RemoteTask taskEstimate = new RemoteTask(
				taskTrain.getResult(),
				MediaType.APPLICATION_WWW_FORM,
				form.getWebRepresentation(),
				Method.POST);
		while (taskEstimate.poll()) {
			logger.info(taskTrain.getStatus().toString());
		}
		logger.info(taskTrain.getResult().toString());
		logger.info(taskTrain.getStatus().toString());	
	
		
	}			
	@Override
	public void testGetJavaObject() throws Exception {
	}
	
	public void testDescriptorsTUM() throws Exception {
		/*
		I ran a short test and 
		> > it seems to be the case, yes. 
		> > curl -X POST -d
		> > "dataset_uri=http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/157" -d
		> > "ALL=false" -d "GravitationalIndexDescriptor=true"
		> > http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/algorithm/CDKPhysChem
		> > 
		> > curl -X POST -d
		> > "dataset_uri=http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/23" -d
		> > "ALL=false" -d "GravitationalIndexDescriptor=true"
		> > http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/algorithm/CDKPhysChem
		> > 
		> > result e.g.:
		> > http://ambit.uni-plovdiv.bg:8080/ambit2/feature/83436
		> > for both datasets
		*/
		Assert.fail("Descriptor calculation with TUM services");
	}
	@Test
	public void testPost() throws Exception {
		Form headers = new Form();  
		for (int i=0; i < 2;i++) {

				//there is Cramer rules model already in the test database
			Reference model = testAsyncTask(
					String.format("http://localhost:%d/algorithm/toxtreecramer", port),
					headers, Status.SUCCESS_OK,
					String.format("http://localhost:%d/model/%s", port,"1"));
			//for some reason models table has autoincrement=4 
			OTModel otmodel = OTModel.model(model.toString());
			OTDataset dataset = otmodel.predict(OTDataset.dataset(String.format("http://localhost:%d/dataset/%s", port,"1")));
			
			testAsyncTask(
					String.format("http://localhost:%d/algorithm/toxtreecramer2", port),
					headers, Status.SUCCESS_OK,
					String.format("http://localhost:%d/model/4", port));
			
			
			IDatabaseConnection c = getConnection();
			ITable table = 	c.createQueryTable("p",
				"SELECT count(*) c,object FROM property_annotation join properties using(idproperty) group by object order by object");
			Assert.assertEquals(3,table.getRowCount());
			Assert.assertEquals("High (Class III)",table.getValue(0,"object"));
			Assert.assertEquals("Intermediate (Class II)",table.getValue(1,"object"));
			Assert.assertEquals("Low (Class I)",table.getValue(2,"object"));
			for (int j=0; j< 2;j++)
				Assert.assertEquals(new BigInteger("2"),table.getValue(j,"c"));
			c.close();			
		}

	}
	
	@Test
	public void testToxtreeSkinSensModel() throws Exception {
		Form headers = new Form();  

		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreeskinsens", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		
		OTModel model = OTModel.model(String.format("http://localhost:%d/model/%s", port,"3"));
		OTDataset dataset = model.predict(OTDataset.dataset(String.format("http://localhost:%d/dataset/%s", port,"1")));
		
		IDatabaseConnection c = getConnection();
		ITable table = 	c.createQueryTable("p",
			"SELECT count(*) c,object FROM property_annotation join properties using(idproperty) group by object order by object");
		Assert.assertEquals(2,table.getRowCount());
		Assert.assertEquals("NO",table.getValue(0,"object"));
		Assert.assertEquals("YES",table.getValue(1,"object"));
		Assert.assertEquals(new BigInteger("6"),table.getValue(0,"c"));
		Assert.assertEquals(new BigInteger("6"),table.getValue(1,"c"));		
		c.close();	
		
	}
	
	@Test
	public void testToxtreeDNABindingModel() throws Exception {
		Form headers = new Form();  

		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreednabinding", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		
		OTModel model = OTModel.model(String.format("http://localhost:%d/model/%s", port,"3"));
		OTDataset dataset = model.predict(OTDataset.dataset(String.format("http://localhost:%d/dataset/%s", port,"1")));
		
		IDatabaseConnection c = getConnection();
		ITable table = 	c.createQueryTable("p",
			"SELECT count(*) c,object FROM property_annotation join properties using(idproperty) group by object order by object");
		Assert.assertEquals(2,table.getRowCount());
		Assert.assertEquals("NO",table.getValue(0,"object"));
		Assert.assertEquals("YES",table.getValue(1,"object"));
		Assert.assertEquals(new BigInteger("6"),table.getValue(0,"c"));
		Assert.assertEquals(new BigInteger("6"),table.getValue(1,"c"));		
		c.close();	
		
		
	}
	
	@Test
	public void testToxtreeProteinBindingModel() throws Exception {
		Form headers = new Form();  

		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreeproteinbinding", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		
		OTModel model = OTModel.model(String.format("http://localhost:%d/model/%s", port,"3"));
		OTDataset dataset = model.predict(OTDataset.dataset(String.format("http://localhost:%d/dataset/%s", port,"1")));
		IDatabaseConnection c = getConnection();
		ITable table = 	c.createQueryTable("p",
			"SELECT count(*) c,object FROM property_annotation join properties using(idproperty) group by object order by object");
		Assert.assertEquals(2,table.getRowCount());
		Assert.assertEquals("NO",table.getValue(0,"object"));
		Assert.assertEquals("YES",table.getValue(1,"object"));
		Assert.assertEquals(new BigInteger("6"),table.getValue(0,"c"));
		Assert.assertEquals(new BigInteger("6"),table.getValue(1,"c"));		
		c.close();	
		
	}
	
	@Test
	public void testToxtreeCarcinogenicityISS() throws Exception {
		Form headers = new Form();  

		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreecarc", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		
		OTModel model = OTModel.model(String.format("http://localhost:%d/model/%s", port,"3"));
		OTDataset dataset = model.predict(OTDataset.dataset(String.format("http://localhost:%d/dataset/%s", port,"1")));
		logger.info(dataset.toString());
		
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
			"SELECT count(*) c,group_concat(distinct(status)) s  FROM property_values v join template_def t using(idproperty) join models on t.idtemplate=models.predicted and name='ToxTree: Benigni/Bossa rules for carcinogenicity and mutagenicity' group by `status` order by `status`");
		Assert.assertEquals(2,table.getRowCount());
		Assert.assertEquals("OK",table.getValue(0,"s"));
		Assert.assertEquals(new BigInteger("40"),table.getValue(0,"c"));
			//the explanation field
		Assert.assertEquals("TRUNCATED",table.getValue(1,"s"));
		Assert.assertEquals(new BigInteger("4"),table.getValue(1,"c"));
		c.close();
	}
	
	@Test
	public void testToxtreeAmesMutagenicity() throws Exception {
		//exception will be thrown on unknown atom type will, and error value stored in the db 
		Preferences.setProperty(Preferences.STOP_AT_UNKNOWNATOMTYPES,Boolean.TRUE.toString());
		Form headers = new Form();  

		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreeames", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		
		OTModel model = OTModel.model(String.format("http://localhost:%d/model/%s", port,"3"));
		OTDataset dataset = model.predict(OTDataset.dataset(String.format("http://localhost:%d/dataset/%s", port,"1")));
		logger.info(dataset.toString());
		
	    IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
					"SELECT count(*) c,group_concat(distinct(status)) s FROM property_values v join template_def t using(idproperty) join models on t.idtemplate=models.predicted and name='ToxTree: In vitro mutagenicity (Ames test) alerts by ISS' where idstructure=100211 group by idstructure,`status`");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("ERROR",table.getValue(0,"s"));
		Assert.assertEquals(new BigInteger("6") /*WTF*/,table.getValue(0,"c"));
		table = 	c.createQueryTable("EXPECTED",
		"SELECT count(*) c,group_concat(distinct(status)) s  FROM property_values v join template_def t using(idproperty) join models on t.idtemplate=models.predicted and name='ToxTree: In vitro mutagenicity (Ames test) alerts by ISS' where idstructure!=100211 group by `status` order by `status`");
		Assert.assertEquals(2,table.getRowCount());
		Assert.assertEquals("OK",table.getValue(0,"s"));
		Assert.assertEquals(new BigInteger("18"),table.getValue(0,"c"));
		//the explanation field
		Assert.assertEquals("TRUNCATED",table.getValue(1,"s"));
		Assert.assertEquals(new BigInteger("3"),table.getValue(1,"c"));
		
		table = 	c.createQueryTable("p",
			"SELECT count(*) c,predicate,object FROM property_annotation join properties using(idproperty) group by object order by object");
		Assert.assertEquals(2,table.getRowCount());
		Assert.assertEquals("acceptValue",table.getValue(0,"predicate"));
		Assert.assertEquals("acceptValue",table.getValue(1,"predicate"));
		Assert.assertEquals("NO",table.getValue(0,"object"));
		Assert.assertEquals("YES",table.getValue(1,"object"));
		Assert.assertEquals(new BigInteger("6"),table.getValue(0,"c"));
		Assert.assertEquals(new BigInteger("6"),table.getValue(1,"c"));		
		
		c.close();
		
	}
	
	
	@Test
	public void testToxtreeSmartCypModel() throws Exception {
		Form headers = new Form();  

		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreesmartcyp", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		
		OTModel model = OTModel.model(String.format("http://localhost:%d/model/%s", port,"3"));
		OTDataset dataset = model.predict(OTDataset.dataset(String.format("http://localhost:%d/dataset/%s", port,"1")));
		
		
		IDatabaseConnection c = getConnection();
		ITable table = 	c.createQueryTable("p",
			"SELECT count(*) c,object FROM property_annotation join properties using(idproperty) group by object order by object");
		Assert.assertEquals(2,table.getRowCount());
		Assert.assertEquals("NO",table.getValue(0,"object"));
		Assert.assertEquals("YES",table.getValue(1,"object"));
		Assert.assertEquals(new BigInteger("4"),table.getValue(0,"c"));
		Assert.assertEquals(new BigInteger("4"),table.getValue(1,"c"));
		c.close();
		
		
	}	
	

	
	@Test
	public void testToxtreeBiodegModel() throws Exception {
		Form headers = new Form();  

		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreebiodeg", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		
		IDatabaseConnection c = getConnection();
		ITable table = 	c.createQueryTable("p",
			"SELECT count(*) c,object FROM property_annotation join properties using(idproperty) group by object order by object");
		Assert.assertEquals(3,table.getRowCount());
		Assert.assertEquals("Class 1 (easily biodegradable chemical)",table.getValue(0,"object"));
		Assert.assertEquals("Class 2 (persistent chemical)",table.getValue(1,"object"));
		Assert.assertEquals("Class 3 (unknown biodegradability)",table.getValue(2,"object"));
		Assert.assertEquals(new BigInteger("1"),table.getValue(0,"c"));
		Assert.assertEquals(new BigInteger("1"),table.getValue(1,"c"));
		Assert.assertEquals(new BigInteger("1"),table.getValue(2,"c"));
		c.close();	
		
	}		

	@Test
	public void testToxtreeVerhaarModel() throws Exception {
		Form headers = new Form();  

		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreeverhaar", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		
	}
	
	@Test
	public void testToxtreeISSMICModel() throws Exception {
		Form headers = new Form();  

		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreemic", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		
		
		IDatabaseConnection c = getConnection();
		ITable table = 	c.createQueryTable("p",
			"SELECT count(*) c,object FROM property_annotation join properties using(idproperty) group by object order by object");
		Assert.assertEquals(2,table.getRowCount());
		Assert.assertEquals("At least one positive structural alerts for the  micronucleus assay (Class I)",table.getValue(0,"object"));
		Assert.assertEquals("No alerts for the  micronucleus assay (Class II)",table.getValue(1,"object"));
		for (int i=0; i< 2;i++)
			Assert.assertEquals(new BigInteger("1"),table.getValue(i,"c"));
		c.close();	
	}
	@Test
	public void testToxtreeEyeModel() throws Exception {
		Form headers = new Form();  

		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreeeye", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		
		IDatabaseConnection c = getConnection();
		ITable table = 	c.createQueryTable("p",
			"SELECT count(*) c,object FROM property_annotation join properties using(idproperty) group by object order by object");
		Assert.assertEquals(11,table.getRowCount());
		Assert.assertEquals("Moderate reversable irritation to the eye",table.getValue(0,"object"));
		Assert.assertEquals("NOT corrosion R34, R35 or R41",table.getValue(1,"object"));
		Assert.assertEquals("NOT eye irritation R36",table.getValue(2,"object"));
		for (int i=0; i< 11;i++)
			Assert.assertEquals(new BigInteger("1"),table.getValue(i,"c"));
		c.close();	
	}
	@Test
	public void testToxtreeSkinIrritationModel() throws Exception {
		Form headers = new Form();  

		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreeskinirritation", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		
	}
	@Test
	public void testToxtreeMichaelAcceptorsModel() throws Exception {
		Form headers = new Form();  

		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreemichaelacceptors", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));
		
		IDatabaseConnection c = getConnection();
		ITable table = 	c.createQueryTable("p",
			"SELECT count(*) c,object FROM property_annotation join properties using(idproperty) group by object order by object");
		Assert.assertEquals(2,table.getRowCount());
		Assert.assertEquals("Michael acceptor",table.getValue(0,"object"));
		Assert.assertEquals("Not reactive via Michael addition",table.getValue(1,"object"));
		for (int i=0; i< 2;i++)
			Assert.assertEquals(new BigInteger("1"),table.getValue(i,"c"));
		c.close();
	}		
}
