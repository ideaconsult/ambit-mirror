package ambit2.rest.substance;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.rdf.ns.OT;
import ambit2.base.data.SubstanceRecord;
import ambit2.rest.dataset.AbstractStaxRDFWriter;

import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

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
			writer.writeStartElement(RDF.getURI(), "RDF");
			//writer.setPrefix(ot, OT.NS);
			writer.setPrefix(owl, OWL.getURI());
			writer.setPrefix(dc, DC.getURI());
			writer.setPrefix(dcterms, DCTerms.getURI());
			
			writer.setPrefix("sio", "http://semanticscience.org/resource/");
			writer.setPrefix("obo", "http://purl.obolibrary.org/obo/");

			writer.setDefaultNamespace(rdf);
		} catch (Exception x) {

		}
	}

	@Override
	public SubstanceRecord process(SubstanceRecord record) throws Exception {
		String substanceURI = getUriReporter().getURI(record);
		getOutput().writeStartElement(RDF.getURI(), "Description");
		getOutput().writeAttribute(RDF.getURI(), "about", substanceURI);
		getOutput().writeEndElement();
		return record;
	}

	/**
	 * http://purl.obolibrary.org/obo/BFO_0000056
	 */
}
