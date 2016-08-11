package ambit2.rest.test.substance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.UUID;

import junit.framework.Assert;
import net.idea.i5.io.I5_ROOT_OBJECTS;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.RawIteratingCSVReader;
import ambit2.rest.substance.RDFTermsSubstance;
import ambit2.rest.substance.study.Substance2BucketJsonReporter;
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
		// asssay
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

	public static void main(String[] args) {
		Substance2BucketJsonReporter reporter = new Substance2BucketJsonReporter(
				null, null,
				Substance2BucketJsonReporter._JSON_MODE.substance, null);
		Reader in = null;

		try {
			OutputStreamWriter writer;
			if (args.length < 2)
				writer = new OutputStreamWriter(System.out);
			else
				writer = new OutputStreamWriter(new FileOutputStream(new File(
						args[1])));
			reporter.setOutput(writer);
			in = new FileReader(new File(args[0]));
			RawIteratingCSVReader reader = new RawIteratingCSVReader(in,
					CSVFormat.TDF) {
				protected SubstanceRecord prevrecord = null;

				protected String getRecordid(CSVRecord record) {
					return record.get(0);
				}

				protected String getExternalId(CSVRecord record) {
					return record.get(1);
				}

				protected String getExternalIdSystem(CSVRecord record) {
					return record.get(5);
				}

				protected String getAssayid(CSVRecord record) {
					return record.get(6);
				}

				protected String getSummaryActivity(CSVRecord record) {
					return record.get(3);
				}

				protected double getActivityValue(CSVRecord record)
						throws NumberFormatException {
					return Double.parseDouble(record.get(4));
				}

				protected String getOrthologgroup(CSVRecord record) {
					return record.get(9);
				}

				protected String getGeneSymbol(CSVRecord record) {
					return record.get(8);
				}

				protected String getSpecies(CSVRecord record) {
					return record.get(7);
				}

				@Override
				protected IStructureRecord transform(CSVRecord record) {
					SubstanceRecord substance = prevrecord;
					String id = getRecordid(record);
					if (prevrecord == null
							|| !id.equals(prevrecord.getSubstanceName())) {
						substance = new SubstanceRecord();
						substance.setSubstancetype("standardized");
						substance.setSubstanceUUID(I5Utils.getPrefixedUUID(
								"PC", UUID.nameUUIDFromBytes(id.getBytes())));
						substance.setSubstanceName(id);
						prevrecord = substance;
					}
					structureRecord = substance;
					String externalid = getExternalId(record);
					String externaldb = getExternalIdSystem(record);

					ExternalIdentifier eid = new ExternalIdentifier(externaldb,
							externalid);

					boolean newid = true;
					if (substance.getExternalids() == null)
						substance
								.setExternalids(new ArrayList<ExternalIdentifier>());
					else
						for (ExternalIdentifier e : substance.getExternalids())
							if (e.getSystemDesignator().equals(
									eid.getSystemDesignator())
									&& e.getSystemIdentifier().equals(
											eid.getSystemIdentifier())) {
								newid = false;
								break;
							}
					if (newid)
						substance.getExternalids().add(eid);

					String assayid = getAssayid(record);
					Protocol p = new Protocol(String.format("%s:AID%s:%s",
							externaldb, assayid, externalid));
					p.setTopCategory("TOX");
					p.setCategory(I5_ROOT_OBJECTS.UNKNOWN_TOXICITY.name());
					p.addGuideline(String.format("%s:AID%s", externaldb,
							assayid));
					ProtocolApplication<Protocol, IParams, String, IParams, String> papp = new ProtocolApplication<Protocol, IParams, String, IParams, String>(
							p);
					papp.setDocumentUUID(I5Utils.getPrefixedUUID("PC",
							UUID.nameUUIDFromBytes(p.getEndpoint().getBytes())));
					papp.setInterpretationResult(getSummaryActivity(record));
					IParams params = new Params();
					params.put("Target gene", getGeneSymbol(record));
					params.put("Species", "TaxId:" + getSpecies(record));
					params.put("OG_GENE", String.format("%s/%s", record.get(9),
							record.get(8)));
					params.put("OrthlogGroup", "OG" + getOrthologgroup(record));
					params.put("Entrez_ID", "entrez:" + record.get(2));

					papp.setParameters(params);

					try {
						EffectRecord<String, IParams, String> effect = new EffectRecord<String, IParams, String>();
						effect.setEndpoint("pXC50");
						effect.setIdresult(1);
						double value = getActivityValue(record);
						effect.setLoValue(value);
						effect.setUnit("nM");
						papp.addEffect(effect);

					} catch (Exception x) {
						//x.printStackTrace();
					}
					substance.addMeasurement(papp);
					return structureRecord;
				}
			};
			int n = 0;
			IStructureRecord prevrecord = null;
			reporter.header(writer, null);
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				if (prevrecord != null && (prevrecord != record)) {
					reporter.processItem((SubstanceRecord) prevrecord);
					writer.flush();
					n++;
				}
				prevrecord = record;
				// System.out.println(((SubstanceRecord)record).toJSON(null));
				//if (n > 1) 	break;
			}
			reporter.processItem((SubstanceRecord) prevrecord);
			reporter.footer(writer, null);
			writer.flush();
		} catch (Exception x) {
			x.printStackTrace();
		} finally {

			try {
				in.close();
			} catch (Exception x) {
			}
			try {
				reporter.close();
			} catch (Exception x) {
			}
		}
	}
}
