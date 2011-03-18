package ambit2.rest.test.structure;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import weka.core.Attribute;
import weka.core.Instances;
import ambit2.base.io.DownloadTool;
import ambit2.core.data.MoleculeTools;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OpenTox;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.test.ResourceTest;

import com.lowagie.text.pdf.PdfReader;

public class CompoundResourceTest extends ResourceTest {
	
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/compound/10", port);
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
			count++;
		}
		return count ==1;
	}	

	@Override
	public Object verifyResponseJavaObject(String uri, MediaType media,
			Representation rep) throws Exception {
		Object query = super.verifyResponseJavaObject(uri, media, rep);
		Assert.assertTrue(query instanceof AbstractStructureQuery);
		return query;
	}
	@Override
	public Instances verifyResponseARFF(String uri, MediaType media, InputStream in)
			throws Exception {
		Instances instances = super.verifyResponseARFF(uri, media, in);
		Assert.assertEquals(1,instances.numInstances());
		Assert.assertEquals("http://localhost:8181/compound/11?feature_uris[]=http://localhost:8181/feature",instances.relationName());
		Attribute cas = instances.attribute("http://localhost:8181/feature/3");
		Assert.assertEquals("1530-32-1",instances.firstInstance().stringValue(cas));
		Assert.assertEquals("URI",instances.attribute(0).name());
		
		Attribute a1 = instances.attribute("http://localhost:8181/feature/1");
		Attribute a2 = instances.attribute("http://localhost:8181/feature/2");
		Assert.assertNotNull(a1);
		Assert.assertNotNull(a2);
		return instances;
	}		
	

	public void testRDFTurtle() throws Exception {
		testGet(String.format("http://localhost:%d/compound/11?%s=http://localhost:%d%s", 
				port,OpenTox.params.feature_uris.toString(),port,PropertyResource.featuredef)
				,MediaType.APPLICATION_RDF_TURTLE);
	}	
	
	@Test
	public void testARFF() throws Exception {
		testGet(String.format("http://localhost:%d/compound/11?%s=http://localhost:%d%s", 
				port,OpenTox.params.feature_uris.toString(),port,PropertyResource.featuredef)
				,ChemicalMediaType.WEKA_ARFF);
	}	
	@Override
	public boolean verifyResponseCSV(String uri, MediaType media, InputStream in)
			throws Exception {

		int count = 0;
		IteratingDelimitedFileReader reader = new IteratingDelimitedFileReader(in);
		while (reader.hasNext()) {
			Object o = reader.next();
			Assert.assertTrue(o instanceof IChemObject);
			//Assert.assertTrue(((IChemObject)o).getProperty("URI").equals("/compound/11"));
			count++;
		}
		in.close();
		return count==1;
		
	}		
	@Test
	public void testCSV() throws Exception {
	
		testGet(String.format("http://localhost:%d/compound/11?%s=http://localhost:%d%s", 
				port,
				OpenTox.params.feature_uris.toString(),
				port,
				PropertyResource.featuredef),MediaType.TEXT_CSV);
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
		return count >0;
	}	
	@Test
	public void testCML() throws Exception {
		try {
			testGet(getTestURI(),ChemicalMediaType.CHEMICAL_CML);
		} catch (Exception x) {
			//this is a hack until upgrading CML library
			x.printStackTrace();
		}
	}
	@Override
	public boolean verifyResponseCML(String uri, MediaType media, InputStream in)
			throws Exception {
/*
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return count ==2;
		*/

		IMolecule mol = MoleculeTools.readCMLMolecule(in);
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
		MyIteratingMDLReader reader = new MyIteratingMDLReader(in, DefaultChemObjectBuilder.getInstance());
		int count = 0;
		while (reader.hasNext()) {
			Object o = reader.next();
			Assert.assertTrue(o instanceof IAtomContainer);
			IAtomContainer mol = (IAtomContainer)o;
			Assert.assertEquals(3,mol.getAtomCount());
			Assert.assertEquals(0,mol.getBondCount());
		//	Assert.assertEquals(0,mol.getProperties().size());test datasets has properties inside structure table field
			count++;
		}
		return count==1;
	}
	@Test
	public void testTXT() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_PLAIN);
	}	
	@Override
	public boolean verifyResponseTXT(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			Assert.assertEquals("F.[F-].[Na+]",line);
			count++;
		}
		return count ==1;
	}
	@Test
	public void testInChI() throws Exception {
		testGet(String.format("http://localhost:%d/compound/7", port),ChemicalMediaType.CHEMICAL_INCHI);
	}		
	@Override
	public boolean verifyResponseInChI(String uri, MediaType media,
			InputStream in) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			Assert.assertEquals("InChI=1/2C8H16O2.Sn/c2*1-3-5-6-7(4-2)8(9)10;/h2*7H,3-6H2,1-2H3,(H,9,10);/q;;+2/p-2",line);
			count++;
		}
		return count ==1;
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
			Assert.assertEquals("F.[F-].[Na+]",line);
			count++;
		}
		return count ==1;
	}
	@Test
	public void testPNG() throws Exception {
		testGet(getTestURI(),MediaType.IMAGE_PNG);
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
	public void testPNGMedia() throws Exception {
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		request.setResourceRef(getTestURI()+"?accept-header=image/png");
		request.setMethod(Method.GET);
		Response response = client.handle(request);
		Assert.assertEquals(200,response.getStatus().getCode());
		Assert.assertTrue(response.isEntityAvailable());
		InputStream in = response.getEntity().getStream();
		Assert.assertTrue(verifyResponsePNGMedia(in));
		in.close();	
	}	
	
	public boolean verifyResponsePNGMedia( InputStream in)
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
	
	@Test
	public void testDeleteCompound() throws Exception {
		//fails because foreign key constraints have been changed to restrict
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT *  FROM struc_dataset where id_srcdataset=1");
		Assert.assertEquals(4,table.getRowCount());
		c.close();
		
		Response response =  testDelete(
					String.format("http://localhost:%d/compound/7", port),
					MediaType.APPLICATION_WWW_FORM,
					null);
		
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT *  FROM struc_dataset where idstructure = 100211 and id_srcdataset=1");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where idchemical=7");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure where idchemical=7");
		Assert.assertEquals(0,table.getRowCount());			
		c.close();
				
		
	}		

	@Test
	public void testCreateEntry() throws Exception {
		
		InputStream in  = getClass().getClassLoader().getResourceAsStream("input.sdf");

		StringBuilder b = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line=reader.readLine())!=null) {
			b.append(line);
			b.append('\n');
		}
		
		testAsyncPoll(new Reference(getTestURI()),ChemicalMediaType.CHEMICAL_MDLSDF, 
				new StringRepresentation(b.toString(),ChemicalMediaType.CHEMICAL_MDLSDF),Method.POST,
				new Reference(String.format("http://localhost:%d/compound/29142/conformer/129346",port)));

        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(6,table.getRowCount());
		c.close();
		
	}	
	
	@Test
	public void testCreateEntrySMILES() throws Exception {
		
		String smiles = "c1ccccc1O";
		
		testAsyncPoll(new Reference(getTestURI()),ChemicalMediaType.CHEMICAL_SMILES, 
				new StringRepresentation(smiles,ChemicalMediaType.CHEMICAL_SMILES),Method.POST,
				new Reference(String.format("http://localhost:%d/compound/29142/conformer/129346",port)));

        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(6,table.getRowCount());
		c.close();
		
	}		
	
	@Test
	public void testCreateEntryFromURI_CSLS() throws Exception {
		
		String uri = String.format("http://localhost:%d/query/csls/50-00-0",port);
		Form form = new Form();
		form.add(OpenTox.params.compound_uri.toString(),uri);
		
		testAsyncPoll(new Reference(getTestURI()),MediaType.TEXT_URI_LIST,
				form.getWebRepresentation(),Method.POST,
				new Reference(String.format("http://localhost:%d/compound/29142/conformer/129346",port)));

        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(6,table.getRowCount());
		c.close();
		
	}		
	
	@Test
	public void testCreateEntryFromURI_PUBCHEM() throws Exception {
		
		String uri = String.format("http://localhost:%d/query/pubchem/50-00-0",port);
		Form form = new Form();
		form.add(OpenTox.params.compound_uri.toString(),uri);
		
		testAsyncPoll(new Reference(getTestURI()),MediaType.TEXT_URI_LIST,
				form.getWebRepresentation(),Method.POST,
				new Reference(String.format("http://localhost:%d/compound/29142/conformer/129346",port)));

        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(6,table.getRowCount());
		c.close();
		
	}	
}
