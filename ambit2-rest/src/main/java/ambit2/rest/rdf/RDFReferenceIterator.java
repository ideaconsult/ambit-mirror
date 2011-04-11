package ambit2.rest.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.rest.OpenTox;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
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
		
	public RDFReferenceIterator(Reference reference) throws ResourceException , MalformedURLException,IOException {
		super(reference,BibTex.BTClass.Entry.toString());
	}	
	public RDFReferenceIterator(Reference reference,MediaType mediaType) throws ResourceException , MalformedURLException,IOException {
		super(reference,mediaType,BibTex.BTClass.Entry.toString());
	}
	
	public RDFReferenceIterator(InputStream in,MediaType mediaType) throws ResourceException  , MalformedURLException,IOException{
		super(in,mediaType,BibTex.BTClass.Entry.toString());
	}	
	
	public RDFReferenceIterator(Model model,StmtIterator recordIterator) {
		super (model,BibTex.BTClass.Entry.toString(),recordIterator);
	}	
	public RDFReferenceIterator(Model model) {
		super(model, BibTex.BTClass.Entry.toString());
	}
	

	@Override
	protected ILiteratureEntry createRecord() {
		return null;
	}

	@Override
	protected ILiteratureEntry parseRecord(RDFNode newEntry, ILiteratureEntry record) {
		if (record==null) record = createRecord();
		Reference thisurl = reference==null?baseReference:reference;
		String title = thisurl==null?"Default":thisurl.toString();
		String seeAlso = title;
		if (newEntry.isLiteral()) {
			title = ((Literal)newEntry).getString();
		} else if (newEntry.isResource()) {
			
			thisurl = new Reference(((Resource)newEntry).getURI());
			Resource newResource = (Resource) newEntry;

			try { title = getTitle(newEntry); } catch (Exception x) {title=thisurl.toString();}
			if (newResource.getProperty(RDFS.seeAlso)!= null) {
				RDFNode value = newResource.getProperty(RDFS.seeAlso).getObject();
				if (value!=null)
					if (value.isLiteral())	seeAlso = ((Literal)value).getString();
					else seeAlso = ((Resource)value).getURI();
	
			} else seeAlso = title;
		}
		record = new LiteratureEntry(title==null?thisurl.toString():title,seeAlso==null?thisurl.toString():seeAlso);
		parseObjectURI(newEntry,record);
		if ((record.getId()>0) && !thisurl.equals(reference)) {
			RDFReferenceIterator iterator = null;
			
			try {
				iterator = new RDFReferenceIterator(new Reference(thisurl));
				iterator.setCloseModel(true);	
				iterator.setBaseReference(baseReference);
				while (iterator.hasNext()) {
					return iterator.next();
				}
				
			} catch (Exception x) {

			} finally {
				try { iterator.close();} catch (Exception x) {}
			}
		} 
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
		return OpenTox.URI.reference.getTemplate(baseReference);

	}
	@Override
	protected void parseObjectURI(RDFNode uri, ILiteratureEntry record) {
		Map<String, Object> vars = new HashMap<String, Object>();
		try {
			getTemplate().parse(getURI(uri), vars);
			record.setId(Integer.parseInt(vars.get(OpenTox.URI.reference.getKey()).toString())); } 
		catch (Exception x) {record.setId(-1);};
	}
	/*
	public static ILiteratureEntry readReference(OntModel jenaModel,Resource feature,Reference baseReference,Property property) {
		
		Statement p = feature.getProperty(property);
		if (p==null) return null;
		
		RDFNode target = p.getObject();
		if(target == null) return null;
		
		if (target.isResource()) { 
			return RDFReferenceIterator.parseRecord(jenaModel,(Resource) target, null);
		} else {
			String value = ((Literal)target).getString();
			return new LiteratureEntry(value,value);
		}
	}
	*/
/*
	public static ILiteratureEntry readReference(OntModel jenaModel,Resource newEntry,Reference baseReference,Property property) {
		String url = newEntry.isURIResource()?newEntry.getURI():newEntry.getLocalName();
			
		try {	
			
			RDFReferenceIterator iterator = null;
			Statement p = newEntry.getProperty(property);
			if (p==null) return new LiteratureEntry(url,url);
			
			RDFNode reference = p.getObject();
			if (reference.isResource()) {
				try {
					url = getURI(reference);
					StmtIterator st = jenaModel.listStatements(new SimpleSelector(newEntry,property,(RDFNode)null));
					iterator = new RDFReferenceIterator(jenaModel,st);
					iterator.setBaseReference(baseReference);
					iterator.setIterateSubjects(false);
				} catch (Exception x) {
					return RDFReferenceIterator.parseRecord(jenaModel,(Resource) reference, null);
				}
			} else {
				url = ((Literal)reference).getString();
				//iterator = new RDFReferenceIterator(new Reference(url));
				//not good idea to retrieve foreign references
				return new LiteratureEntry(url,url);
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
			x.printStackTrace();

		}				
		return new LiteratureEntry(url,url);
	}
	*/
	}
