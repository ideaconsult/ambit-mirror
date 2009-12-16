package ambit2.rest.reference;

import org.restlet.data.MediaType;
import org.restlet.data.Request;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.property.AbstractPropertyRetrieval;
import ambit2.rest.BibTex;
import ambit2.rest.OT;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.QueryURIReporter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ReferenceRDFReporter<Q extends IQueryRetrieval<ILiteratureEntry>> extends QueryRDFReporter<ILiteratureEntry, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8857789530109166243L;

	public ReferenceRDFReporter(Request request,MediaType mediaType) {
		super(request,mediaType);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference) {
		return new ReferenceURIReporter(reference);
	}
	@Override
	public void processItem(ILiteratureEntry item) throws AmbitException {
		Individual entry = getJenaModel().createIndividual(uriReporter.getURI(item),
				BibTex.BTClass.Entry.getOntClass(getJenaModel()));
		entry.addProperty(DC.title, item.getName());
		entry.addProperty(RDFS.seeAlso,item.getURL());
		entry.addLiteral(DC.identifier,
				 getJenaModel().createTypedLiteral(uriReporter.getURI(item),XSDDatatype.XSDanyURI));

	}

	public void open() throws DbAmbitException {
		
	}

}
