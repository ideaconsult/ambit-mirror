package ambit2.rest.reference;

import java.util.HashMap;
import java.util.Map;

import org.restlet.util.Template;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.rest.BibTex;
import ambit2.rest.RDFBatchParser;

import com.hp.hpl.jena.ontology.OntModel;
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
	protected Template referenceTemplate;
	public RDFReferenceParser(String baseReference) {
		super(baseReference, BibTex.BTClass.Entry.toString());
		referenceTemplate = new Template(String.format("%s%s/{%s}",baseReference==null?"":baseReference,ReferenceResource.reference,ReferenceResource.idreference));
	}
	
	protected void parseReferenceURI(String uri,ILiteratureEntry entry) {
		Map<String, Object> vars = new HashMap<String, Object>();
		referenceTemplate.parse(uri, vars);
		try {entry.setId(Integer.parseInt(vars.get(ReferenceResource.idreference).toString())); } 
		catch (Exception x) {entry.setId(-1);};

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

/*
 * (non-Javadoc)
 * @see ambit2.rest.RDFBatchParser#parseRecord(com.hp.hpl.jena.rdf.model.Resource, java.lang.Object)
 */
	@Override
	protected ILiteratureEntry parseRecord(Resource newEntry, ILiteratureEntry record) {
		ILiteratureEntry le = parseRecord(jenaModel,newEntry, record);
		parseReferenceURI(newEntry.toString(),le);
		return le;
	}
	
	public static ILiteratureEntry parseRecord(OntModel jenaModel, Resource newEntry, ILiteratureEntry record) {
		String name = newEntry.toString();
		RDFNode value = newEntry.getProperty(DC.title).getObject();
		if ((value!=null) && value.isLiteral())	name = ((Literal)value).getString();
		value = newEntry.getProperty(RDFS.seeAlso).getObject();
		record = new LiteratureEntry(name,value==null?name:value.toString());
		return record;
	}
}
