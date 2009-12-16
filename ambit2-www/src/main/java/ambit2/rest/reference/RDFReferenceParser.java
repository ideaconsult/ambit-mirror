package ambit2.rest.reference;

import java.util.Iterator;

import org.restlet.data.Reference;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.rest.BibTex;
import ambit2.rest.RDFBatchParser;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;

public class RDFReferenceParser extends RDFBatchParser<ILiteratureEntry> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1257623392289416245L;

	public RDFReferenceParser(String baseReference) {
		super(baseReference, BibTex.BTClass.Entry.toString());
	}
	@Override
	protected Resource createResource(String otclass) {
		try {
			return BibTex.BTClass.valueOf(otclass).getOntClass(jenaModel);
		} catch (Exception x) {
			x.printStackTrace();
			return null;} 
	}
	@Override
	protected ILiteratureEntry createRecord() {
		return null;
	}

	@Override
	public void afterProcessing(Reference target,
			Iterator<ILiteratureEntry> iterator) throws AmbitException {
		super.afterProcessing(target, iterator);
	}
/*
 * (non-Javadoc)
 * @see ambit2.rest.RDFBatchParser#parseRecord(com.hp.hpl.jena.rdf.model.Resource, java.lang.Object)
 */
	@Override
	protected ILiteratureEntry parseRecord(Resource newEntry, ILiteratureEntry record) {
		String name = newEntry.toString();
		RDFNode value = newEntry.getProperty(DC.title).getObject();
		if ((value!=null) && value.isLiteral())	name = ((Literal)value).getString();
		value = newEntry.getProperty(RDFS.seeAlso).getObject();
		record = new LiteratureEntry(name,value==null?name:value.toString());
		return record;
	}

}
