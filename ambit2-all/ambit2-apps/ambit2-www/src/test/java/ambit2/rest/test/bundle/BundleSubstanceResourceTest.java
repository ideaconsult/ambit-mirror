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

public class BundleSubstanceResourceTest extends ProtectedResourceTest {
	protected String dbFile = "src/test/resources/descriptors-datasets.xml";

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/bundle/1/substance", port);
	}

	@Test
	public void testJSON() throws Exception {
		setUpDatabase(dbFile);
		testGet(getTestURI(), MediaType.APPLICATION_JSON);
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
	
	@Override
	public boolean verifyResponseJSON(String uri, MediaType media,
			InputStream in) throws Exception {
		// TODO Auto-generated method stub
		return super.verifyResponseJSON(uri, media, in);
	}
	@Test
	public void testRDF() throws Exception {
		setUpDatabase(dbFile);
		testGet(getTestURI()+"?media=application/rdf+xml", MediaType.APPLICATION_RDF_XML);
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

	}

}
