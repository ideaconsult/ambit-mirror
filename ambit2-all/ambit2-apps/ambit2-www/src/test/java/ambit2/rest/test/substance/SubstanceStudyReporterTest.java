package ambit2.rest.test.substance;

import java.io.InputStream;

import junit.framework.Assert;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.substance.RDFTermsSubstance;
import ambit2.rest.test.ResourceTest;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * https://sourceforge.net/p/ambit/feature-requests/99/
 * 
 * @author nina
 * 
 */
public class SubstanceStudyReporterTest extends ResourceTest {

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/substance/1", port);

	}

	@Override
	protected void setDatabase() throws Exception {
		setUpDatabaseFromResource("descriptors-datasets.xml");
	}

	@Test
	public void testN3() throws Exception {
		testGet(getTestURI(), MediaType.TEXT_RDF_N3);

	}

	@Override
	public boolean verifyResponseRDFN3(String uri, MediaType media,
			InputStream in) throws Exception {

		Model model = ModelFactory.createDefaultModel();
		model.read(in, String.format("http://localhost:%d/", port), "N3");
		RDFDataMgr.write(System.out, model, RDFFormat.TURTLE);
		// one substance
		ResIterator substances = model.listSubjectsWithProperty(RDF.type,
				RDFTermsSubstance.CHEBI_59999.getResource(model));
		Assert.assertEquals(1, countResources(substances));
		// measure groups
		ResIterator measuregroups = model.listSubjectsWithProperty(RDF.type,
				RDFTermsSubstance.BAO_0000040.getResource(model));
		Assert.assertEquals(4, countResources(measuregroups));
		// protocols
		ResIterator protocols = model.listSubjectsWithProperty(RDF.type,
				RDFTermsSubstance.OBI_0000272.getResource(model));
		Assert.assertEquals(4, countResources(protocols));
		// endpoint
		ResIterator endpoints = model.listSubjectsWithProperty(RDF.type,
				RDFTermsSubstance.BAO_0000179.getResource(model));
		Assert.assertEquals(4, countResources(endpoints));
		//asssay
		ResIterator assays = model.listSubjectsWithProperty(RDF.type,
				RDFTermsSubstance.BAO_0000015.getResource(model));
		Assert.assertEquals(4, countResources(assays));
		return true;
	}

	protected int countResources(ResIterator res) {
		int n = 0;
		while (res.hasNext()) {
			Resource r = res.next();
			n++;
		}
		return n;
	}
}
