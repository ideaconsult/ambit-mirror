package ambit2.rest.test.bundle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.test.ProtectedResourceTest;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class BundleResourceTest extends ProtectedResourceTest {
	protected String dbFile = "descriptors-datasets.xml";

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/bundle", port);
	}

	@Test
	public void testURI() throws Exception {
		setUpDatabaseFromResource(dbFile);
		testGet(getTestURI(), MediaType.TEXT_URI_LIST);
	}

	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			count++;
		}
		return count == 1;
	}

	@Test
	public void testJSON() throws Exception {
		setUpDatabaseFromResource(dbFile);
		testGet(getTestURI(), MediaType.APPLICATION_JSON);
	}

	@Test
	public void testRDF() throws Exception {
		setUpDatabaseFromResource(dbFile);
		testGet(getTestURI(), MediaType.APPLICATION_RDF_XML);
	}


	@Override
	public OntModel verifyResponseRDFXML(String uri, MediaType media,
			InputStream in) throws Exception {
		OntModel ontmodel = super.verifyResponseRDFXML(uri, media, in);
		Assert.assertNotSame(0, ontmodel.size());
		ExtendedIterator<Individual> i = ontmodel.listIndividuals();
		while (i.hasNext()) {
			Individual r = i.next();
			System.out.println(r.getURI());
		}
		return ontmodel;
	}

	@Test
	public void testCreateEntry() throws Exception {
		/*
		 * 
		 * InputStream in =
		 * getClass().getClassLoader().getResourceAsStream("input.sdf");
		 * 
		 * StringBuilder b = new StringBuilder(); BufferedReader reader = new
		 * BufferedReader(new InputStreamReader(in)); String line = null; while
		 * ((line=reader.readLine())!=null) { b.append(line); b.append('\n'); }
		 * 
		 * testAsyncPoll(new
		 * Reference(getTestURI()),ChemicalMediaType.CHEMICAL_MDLSDF, new
		 * StringRepresentation
		 * (b.toString(),ChemicalMediaType.CHEMICAL_MDLSDF),Method.POST, new
		 * Reference(String.format("http://localhost:%d/dataset/4",port)));
		 * 
		 * IDatabaseConnection c = getConnection(); ITable table =
		 * c.createQueryTable("EXPECTED","SELECT * FROM structure");
		 * Assert.assertEquals(12,table.getRowCount()); c.close();
		 */

	}

}