package ambit2.rest.rdf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.util.Template;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.rest.OpenTox;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Parses RDF representatiion of {@link LiteratureEntry}
 * @author nina
 *
 */
public class RDFReferenceIterator extends RDFObjectIterator<ILiteratureEntry> {
	
	public RDFReferenceIterator(Representation representation,MediaType mediaType) throws ResourceException {
		super(representation,mediaType,BibTex.BTClass.Entry.toString());
	}
		
	public RDFReferenceIterator(Reference reference) throws ResourceException {
		super(reference,BibTex.BTClass.Entry.toString());
	}	
	public RDFReferenceIterator(Reference reference,MediaType mediaType) throws ResourceException {
		super(reference,mediaType,BibTex.BTClass.Entry.toString());
	}
	
	public RDFReferenceIterator(InputStream in,MediaType mediaType) throws ResourceException {
		super(in,mediaType,BibTex.BTClass.Entry.toString());
	}	
	
	public RDFReferenceIterator(OntModel model,StmtIterator recordIterator) {
		super (model,BibTex.BTClass.Entry.toString(),recordIterator);
	}	
	public RDFReferenceIterator(OntModel model) {
		super(model, BibTex.BTClass.Entry.toString());
	}
	

	@Override
	protected ILiteratureEntry createRecord() {
		return null;
	}

	@Override
	protected ILiteratureEntry parseRecord(Resource newEntry, ILiteratureEntry record) {
		ILiteratureEntry le = parseRecord(jenaModel,newEntry, record);
		parseObjectURI(newEntry,le);
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
	@Override
	protected Resource createResource(String otclass) {
		try {
			return BibTex.BTClass.valueOf(otclass).getOntClass(jenaModel);
		} catch (Exception x) {
			x.printStackTrace();
			return null;} 
	}
	@Override
	protected Template createTemplate() {
		return new Template(String.format("%s%s",
				baseReference==null?"":baseReference,OpenTox.URI.reference.getResourceID()));

	}
	@Override
	protected void parseObjectURI(RDFNode uri, ILiteratureEntry record) {
		Map<String, Object> vars = new HashMap<String, Object>();
		try {
			getTemplate().parse(getIdentifier(uri), vars);
			record.setId(Integer.parseInt(vars.get(OpenTox.URI.reference.getKey()).toString())); } 
		catch (Exception x) {record.setId(-1);};
	}

	public static ILiteratureEntry readReference(OntModel jenaModel,Resource newEntry,Reference baseReference,Property property) {
		String url = null;
		try {	
			
			RDFReferenceIterator iterator = null;
			RDFNode reference = newEntry.getProperty(property).getObject();
			if (reference.isResource()) {
				try {
					url = getIdentifier(reference);
					StmtIterator st = jenaModel.listStatements(new SimpleSelector(newEntry,property,(RDFNode)null));
					iterator = new RDFReferenceIterator(jenaModel,st);
					iterator.setBaseReference(baseReference);
					iterator.setIterateSubjects(false);
				} catch (Exception x) {
					return RDFReferenceIterator.parseRecord(jenaModel,(Resource) reference, null);
				}
			} else {
				url = ((Literal)reference).getString();
				iterator = new RDFReferenceIterator(new Reference(url));
			}
			
			try {
				
				iterator.setBaseReference(baseReference);
				while (iterator.hasNext()) {
					return iterator.next();
				}
				
			} catch (Exception x) {
				
				
			} finally {
				try { iterator.close();} catch (Exception x) {}
			}			
		} catch (Exception x) {
			logger.warn(x);

		}				
		return new LiteratureEntry(url,url);
	}
	}
