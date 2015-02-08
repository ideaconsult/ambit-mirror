package ambit2.rest.reference;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.MediaType;

import ambit2.base.data.ILiteratureEntry;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.rdf.BibTex;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.vocabulary.DC;
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
	protected QueryURIReporter createURIReporter(Request reference,ResourceDoc doc) {
		return new ReferenceURIReporter(reference);
	}
	@Override
	public Object processItem(ILiteratureEntry item) throws AmbitException {
		return addToModel(getJenaModel(), item,uriReporter);
	}
	public static Individual addToModel(OntModel jenaModel,ILiteratureEntry item, 
				QueryURIReporter<ILiteratureEntry, IQueryRetrieval<ILiteratureEntry>> uriReporter) {
		Individual entry = null;
		if ((uriReporter==null) || (uriReporter.getBaseReference()==null)) {
			entry = jenaModel.createIndividual(BibTex.BTClass.Entry.getOntClass(jenaModel));
		} else {
			entry = jenaModel.createIndividual(uriReporter.getURI(item),BibTex.BTClass.Entry.getOntClass(jenaModel));
	
		}
		entry.addProperty(DC.title, item.getName());
		entry.addProperty(RDFS.seeAlso,item.getURL());
		return entry;
		
	}
	public void open() throws DbAmbitException {
		
	}

}
