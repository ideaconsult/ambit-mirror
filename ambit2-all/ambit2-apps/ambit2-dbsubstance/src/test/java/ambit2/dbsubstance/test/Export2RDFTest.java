package ambit2.dbsubstance.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;
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
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.StructureRecordValidator;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
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
		OutputStream writer = null;

		
		StructureRecordValidator validator = new StructureRecordValidator() {
			int idresult = 1;
			int idcompound = 1;
			Map<String, Integer> smileslookup = new TreeMap<String, Integer>();
			@Override
			public IStructureRecord validate(SubstanceRecord record,
					CompositionRelation rel) throws Exception {
				/**
				 * compounds will be assigned ids if imported into database
				 * and resource url depends on the ids
				 */
				if (rel.getSecondStructure().getIdchemical() <= 0) {
					String smi = rel.getSecondStructure().getSmiles();
					if (smi != null) {
						Integer index = smileslookup.get(smi);
						if (index != null)
							rel.getSecondStructure().setIdchemical(index);
						else {
							smileslookup.put(smi, idcompound);
							rel.getSecondStructure().setIdchemical(idcompound);
							idcompound++;
						}
					} else {
						rel.getSecondStructure().setIdchemical(idcompound);
						idcompound++;
					}
				}
				return super.validate(record, rel);
			}

			@Override
			public IStructureRecord validate(
					SubstanceRecord record,
					ProtocolApplication<Protocol, IParams, String, IParams, String> papp)
					throws Exception {
				/*
				 * EffectRecords resource URL depends on th eidresult, when
				 * imported into DB it's automatically assigned, here it's set
				 * to subsequent numbers
				 */
				for (EffectRecord<String, IParams, String> effect : papp
						.getEffects()) {
					if (effect.getIdresult() <= 0)
						effect.setIdresult(idresult++);
				}
				return super.validate(record, papp);
			}
		};
		int records = 0;
		try {
			reader = new NanoWikiRDFReader(new InputStreamReader(
					new GZIPInputStream(new FileInputStream(nanowiki))));
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				Assert.assertTrue(record instanceof SubstanceRecord);
				validator.validate((SubstanceRecord) record);
				r.processItem((SubstanceRecord) record);
				records++;
			}
			r.footer(model, null);

			// one substance
			ResIterator substances = model.listSubjectsWithProperty(RDF.type,
					RDFTermsSubstance.CHEBI_59999.getResource(model));
			Assert.assertEquals(404, countResources(substances));
			// asssay
			ResIterator assays = model.listSubjectsWithProperty(RDF.type,
					RDFTermsSubstance.BAO_0000015.getResource(model));
			Assert.assertEquals(867, countResources(assays));
			// measure groups
			ResIterator measuregroups = model.listSubjectsWithProperty(
					RDF.type, RDFTermsSubstance.BAO_0000040.getResource(model));
			Assert.assertEquals(867, countResources(measuregroups));
			// protocols
			ResIterator protocols = model.listSubjectsWithProperty(RDF.type,
					RDFTermsSubstance.OBI_0000272.getResource(model));
			Assert.assertEquals(65, countResources(protocols));
			// endpoint
			ResIterator endpoints = model.listSubjectsWithProperty(RDF.type,
					RDFTermsSubstance.BAO_0000179.getResource(model));
			// thanks to the validator result ids are set and we have > 1 entry,
			// otherwise all endpoints collapse into one
			Assert.assertEquals(867, countResources(endpoints));

			File output = new File(System.getProperty("java.io.tmpdir") + "/"
					+ "nanowiki_export.ttl");
			System.out.println("Exported to " + output.getAbsolutePath());
			writer = new FileOutputStream(output);

			RDFDataMgr.write(writer, model, RDFFormat.TURTLE);
			
		} finally {
			reader.close();
			if (writer != null)
				writer.close();

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
