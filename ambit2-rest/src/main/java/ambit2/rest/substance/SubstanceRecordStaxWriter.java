package ambit2.rest.substance;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;
import ambit2.base.data.SubstanceRecord;
import ambit2.rest.dataset.AbstractStaxRDFWriter;

import com.hp.hpl.jena.vocabulary.RDF;

/**
 * RDF output for substances and studies
 * designed after BAO
 * @author nina
 *
 */
public class SubstanceRecordStaxWriter extends
		AbstractStaxRDFWriter<SubstanceRecord, SubstanceRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5591294383381439253L;

	public SubstanceRecordStaxWriter(QueryURIReporter<SubstanceRecord, IQueryRetrieval<SubstanceRecord>> urireporter) {
		super();
		setUriReporter(urireporter);
	}
	@Override
	public SubstanceRecord process(SubstanceRecord record) throws Exception {
		String substanceURI = getUriReporter().getURI(record);
		getOutput().writeStartElement(RDF.getURI(), "Description"); // property
		getOutput().writeAttribute(RDF.getURI(), "about", substanceURI);
		getOutput().writeEndElement();
		return record;
	}

}
