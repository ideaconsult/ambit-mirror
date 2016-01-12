package ambit2.rest.substance;

import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.rest.dataset.AbstractStaxRDFWriter;

import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * RDF output for substances and studies designed after BAO
 * 
 * @author nina
 * 
 */
public class SubstanceRecordStaxWriter extends
		AbstractStaxRDFWriter<SubstanceRecord, SubstanceRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5591294383381439253L;
	protected final static String obo_uri = "http://purl.obolibrary.org/obo/";
	protected final static String sio_uri = "http://semanticscience.org/resource/";
	protected final static String rdfs = "rdfs";

	public SubstanceRecordStaxWriter(
			QueryURIReporter<SubstanceRecord, IQueryRetrieval<SubstanceRecord>> urireporter) {
		super();
		setUriReporter(urireporter);
	}

	@Override
	public void header(javax.xml.stream.XMLStreamWriter writer) {
		try {
			writer.writeStartDocument();
			
			writer.setPrefix(rdf, RDF.getURI());
			writer.writeStartElement(RDF.getURI(),"RDF");
			writer.setPrefix(rdfs, RDFS.getURI());
			writer.setPrefix(owl, OWL.getURI());
			writer.setPrefix(dc, DC.getURI());
			writer.setPrefix(dcterms, DCTerms.getURI());
			writer.setPrefix("sio", sio_uri);
			writer.setPrefix("obo", obo_uri);

			writer.writeNamespace(rdf, RDF.getURI());
			writer.writeNamespace(rdfs, RDFS.getURI());
			writer.writeNamespace(owl, OWL.getURI());
			writer.writeNamespace(dc, DC.getURI());
			writer.writeNamespace(dcterms, DCTerms.getURI());
			writer.writeNamespace("sio",sio_uri);
			writer.writeNamespace("obo",obo_uri);
			

			writer.setDefaultNamespace(rdf);
			
		} catch (Exception x) {

		}
	}

	@Override
	public SubstanceRecord process(SubstanceRecord record) throws Exception {
		getOutput().writeStartElement(RDF.getURI(), "Description");
		try {
			String substanceURI = getUriReporter().getURI(record);
			getOutput().writeAttribute(RDF.getURI(), "about", substanceURI);
			if (record.getMeasurements() != null) {
				writeMeasurementgroups(substanceURI, record.getMeasurements());
			}
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			getOutput().writeEndElement();
		}
		return record;
	}

	/**
	 * <pre>
	 *  <rdf:Description rdf:about="http://rdf.ncbi.nlm.nih.gov/pubchem/substance/SID170466782">
	 *     <dcterms:modified rdf:datatype="http://www.w3.org/2001/XMLSchema#date">2015-06-13-05:00</dcterms:modified>
	 *     <sio:has-attribute rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/synonym/MD5_8ff2a02f9c4c4bc9bada6afc24fdcffe" />
	 *     <sio:has-attribute rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/descriptor/SID170466782_Substance_Version" />
	 *     <sio:has-attribute rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/synonym/MD5_5325945dacb5c911a737e47466aa4edd" />
	 *     <sio:has-attribute rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/synonym/MD5_d4cf6213e53cd7b6a4db242052f9c07c" />
	 *     <sio:has-attribute rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/synonym/MD5_132b06d005425a47fb7e9b45764609cb" />
	 *     <sio:has-attribute rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/synonym/MD5_a3c75206678096bf80e5fcdf9beedaf6" />
	 *     <sio:has-attribute rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/synonym/MD5_e7485bf29b50918c71f251b7a67f8338" />
	 *     <obo:BFO_0000056 rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/measuregroup/AID743242" />
	 *     <obo:BFO_0000056 rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/measuregroup/AID743240" />
	 *     ...
	 *     <obo:BFO_0000056 rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/measuregroup/AID1117298" />
	 *     <dcterms:source rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/source/ID824" />
	 *     <sio:CHEMINF_000477 rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/compound/CID158793" />
	 *     <dcterms:available rdf:datatype="http://www.w3.org/2001/XMLSchema#date">2013-12-04-05:00</dcterms:available>
	 *   </rdf:Description>
	 *   <rdf:Description rdf:about="http://rdf.ncbi.nlm.nih.gov/pubchem/endpoint/SID170466782_AID1159528">
	 *     <obo:IAO_0000136 rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/substance/SID170466782" />
	 *   </rdf:Description>
	 * </pre>
	 * 
	 * @param record
	 * @throws Exception
	 */
	protected void writeMeasurementgroups(String substanceURI,
			List<ProtocolApplication> measurements) throws Exception {
		for (ProtocolApplication<Protocol, String, String, IParams, String> pa : measurements) {
			String measurementGroupURI = "http://example.com/measuregroup/"
					+ pa.getDocumentUUID();
			getOutput().writeStartElement(obo_uri, "BFO_0000056");
			try {
				getOutput().writeAttribute(RDF.getURI(), "resource",
						measurementGroupURI);
			} catch (Exception x) {
				x.printStackTrace();
			} finally {
				getOutput().writeEndElement();
			}

			for (EffectRecord<String, IParams, String> effect : pa.getEffects()) {
				String endpointURI = writeEndpoint(substanceURI,
						measurementGroupURI, effect);

			}
		}

	}

	/**
	 * <pre>
	 * <?xml version="1.0" encoding="utf-8" ?>
	 * <rdf:RDF
	 * 	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	 * 	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	 * 	xmlns:sio="http://semanticscience.org/resource/"
	 * 	xmlns:vocab="http://rdf.ncbi.nlm.nih.gov/pubchem/vocabulary#"
	 * 	xmlns:obo="http://purl.obolibrary.org/obo/" >
	 *   <rdf:Description rdf:about="http://rdf.ncbi.nlm.nih.gov/pubchem/measuregroup/AID743222">
	 *     <obo:OBI_0000299 rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/endpoint/SID170466782_AID743222" />
	 *   </rdf:Description>
	 *   <rdf:Description rdf:about="http://rdf.ncbi.nlm.nih.gov/pubchem/endpoint/SID170466782_AID743222">
	 *     <rdf:type rdf:resource="http://www.bioassayontology.org/bao#BAO_0000186" /> <!-- AC50 -->
	 *     <sio:has-value rdf:datatype="http://www.w3.org/2001/XMLSchema#float">33.4889</sio:has-value>
	 *     <vocab:PubChemAssayOutcome rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/vocabulary#inconclusive" />
	 *     <rdfs:label xml:lang="en">Potency</rdfs:label>
	 *     <sio:has-unit rdf:resource="http://purl.obolibrary.org/obo/UO_0000064" />
	 *     <obo:IAO_0000136 rdf:resource="http://rdf.ncbi.nlm.nih.gov/pubchem/substance/SID170466782" />
	 *   </rdf:Description>
	 * </rdf:RDF>
	 * </pre>
	 * 
	 * @param measurementGroupURI
	 * @param effect
	 * @throws Exception
	 */
	protected String writeEndpoint(String substanceURI,
			String measurementGroupURI,
			EffectRecord<String, IParams, String> effect) throws Exception {

		String endpointURI = "http://example.com/endpoint/"
				+ effect.getSampleID();

		getOutput().writeStartElement(RDF.getURI(), "Description");
		try {
			getOutput().writeAttribute(RDF.getURI(), "about", endpointURI);
			// label
			try {
				getOutput().writeStartElement(RDFS.getURI(), "label");
				getOutput().writeCharacters(effect.getEndpoint());
			} finally {
				getOutput().writeEndElement();
			}			
			// value
			if (effect.getLoValue() != null)
				try {
					getOutput().writeStartElement(sio_uri, "has-value");
					getOutput().writeAttribute(RDF.getURI(), "datatype",
							"http://www.w3.org/2001/XMLSchema#float");
					getOutput().writeCharacters(
							String.format("%e", effect.getLoValue()));
				} finally {
					getOutput().writeEndElement();
				}
			// units
			if (effect.getUnit() != null)
				try {
					getOutput().writeStartElement(sio_uri, "has-unit");
					getOutput().writeCharacters(effect.getUnit());
				} finally {
					getOutput().writeEndElement();
				}
		} finally {
			getOutput().writeEndElement();
		}
		return endpointURI;

	}
}
