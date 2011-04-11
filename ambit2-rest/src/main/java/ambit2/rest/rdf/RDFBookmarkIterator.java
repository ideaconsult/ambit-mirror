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

import ambit2.base.data.Bookmark;
import ambit2.rest.OpenTox;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;

public class RDFBookmarkIterator  extends RDFObjectIterator<Bookmark> {
	
	public RDFBookmarkIterator(Representation representation,MediaType mediaType) throws ResourceException {
		super(representation,mediaType,Annotea.Bookmark.Bookmark.toString());
	}
		
	public RDFBookmarkIterator(Reference reference) throws ResourceException,MalformedURLException,IOException {
		super(reference,Annotea.Bookmark.Bookmark.toString());
	}	
	public RDFBookmarkIterator(Reference reference,MediaType mediaType) throws ResourceException,MalformedURLException,IOException {
		super(reference,mediaType,Annotea.Bookmark.Bookmark.toString());
	}
	
	public RDFBookmarkIterator(InputStream in,MediaType mediaType) throws ResourceException,MalformedURLException,IOException {
		super(in,mediaType,Annotea.Bookmark.Bookmark.toString());
	}	
	
	public RDFBookmarkIterator(Model model,StmtIterator recordIterator) {
		super (model,Annotea.Bookmark.Bookmark.toString(),recordIterator);
	}	
	public RDFBookmarkIterator(Model model) {
		super(model, Annotea.Bookmark.Bookmark.toString());
	}
	

	@Override
	protected Bookmark createRecord() {
		return new Bookmark();
	}

	@Override
	protected Bookmark parseRecord(RDFNode newEntry, Bookmark record) {
		
		if (record==null) record = createRecord();
		Reference thisurl = reference==null?baseReference:reference;
		
		parseObjectURI(newEntry, record);
		
		try { record.setTitle(getTitle(newEntry)); } catch (Exception x) { record.setTitle(thisurl.toString());}
		try { record.setCreator(getCreator(newEntry)); } catch (Exception x) { record.setCreator(thisurl.toString());}
		try { record.setDescription(getPropertyValue(DC.description,newEntry)); } catch (Exception x) 
			{ record.setDescription(thisurl.toString());}
		try { record.setRecalls(getPropertyValue(Annotea.BookmarkProperty.recalls.createProperty(jenaModel),newEntry)); } catch (Exception x) 
		{ record.setRecalls(thisurl.toString());}
		try { record.setHasTopic(getPropertyValue(Annotea.BookmarkProperty.hasTopic.createProperty(jenaModel),newEntry)); } catch (Exception x) 
		{ record.setHasTopic(thisurl.toString());}
		
				
		return record;
		
	}
	@Override
	protected Resource createResource(String otclass) {
		try {
			return Annotea.Bookmark.Bookmark.valueOf(otclass).getOntClass(jenaModel);
		} catch (Exception x) {
			x.printStackTrace();
			return null;} 
	}
	@Override
	protected Template createTemplate() {
		return OpenTox.URI.bookmark.getTemplate(baseReference);

	}
	@Override
	protected void parseObjectURI(RDFNode uri, Bookmark record) {
		Map<String, Object> vars = new HashMap<String, Object>();
		try {
			getTemplate().parse(getURI(uri), vars);
			record.setId(Integer.parseInt(vars.get(OpenTox.URI.bookmark.getKey()).toString())); } 
		catch (Exception x) {record.setId(-1);};
	}
	
	}