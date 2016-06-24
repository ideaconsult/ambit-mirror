package ambit2.dbsubstance.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import junit.framework.Assert;
import net.idea.loom.nm.nanowiki.NanoWikiRDFReader;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.substance.RDFTermsSubstance;
import ambit2.rest.substance.SubstanceRDFReporter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class Export2RDFTest {
	@Test
	public void testNanoWiki2RDF() throws Exception {
		File nanowiki = NanoWikiRDFTest.getNanoWikiFile();
		Assert.assertTrue(nanowiki.exists());
		Request hack = new Request();
		hack.setRootRef(new Reference("http://localhost/ambit2"));
		SubstanceRDFReporter r = new SubstanceRDFReporter(hack,
				MediaType.TEXT_RDF_N3);
		Model model = ModelFactory.createDefaultModel();
		r.header(model, null);
		r.setOutput(model);

		NanoWikiRDFReader reader = null;
		int records = 0;
		try {
			reader = new NanoWikiRDFReader(new InputStreamReader(
					new GZIPInputStream(new FileInputStream(nanowiki))));
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				Assert.assertTrue(record instanceof SubstanceRecord);
				r.processItem((SubstanceRecord) record);
				records++;
			}
			r.footer(model, null);

			// one substance
			ResIterator substances = model.listSubjectsWithProperty(RDF.type,
					RDFTermsSubstance.CHEBI_59999.getResource(model));
			Assert.assertEquals(403, countResources(substances));
			// asssay
			ResIterator assays = model.listSubjectsWithProperty(RDF.type,
					RDFTermsSubstance.BAO_0000015.getResource(model));
			Assert.assertEquals(854, countResources(assays));
			// measure groups
			ResIterator measuregroups = model.listSubjectsWithProperty(
					RDF.type, RDFTermsSubstance.BAO_0000040.getResource(model));
			Assert.assertEquals(854, countResources(measuregroups));
			// protocols
			ResIterator protocols = model.listSubjectsWithProperty(RDF.type,
					RDFTermsSubstance.OBI_0000272.getResource(model));
			Assert.assertEquals(64, countResources(protocols));
			// endpoint
			ResIterator endpoints = model.listSubjectsWithProperty(RDF.type,
					RDFTermsSubstance.BAO_0000179.getResource(model));
			//doesn't seem right
			Assert.assertEquals(1, countResources(endpoints));

		} finally {
			reader.close();
			//todo change to write to a file
			RDFDataMgr.write(System.out, model, RDFFormat.TURTLE);

		}
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
