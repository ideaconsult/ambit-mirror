package ambit2.rest.rdf;

import java.io.InputStream;
import java.util.Iterator;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;

/**
 * Iterates over objects of a specified OT.class
 * @author nina
 *
 * @param <Item>
 */
public abstract class RDFObjectIterator<Item> implements Iterator<Item> {
	protected OntModel jenaModel;
	protected Item record;
	protected StmtIterator recordIterator;	
	protected String topObject;
	protected MediaType mediaType = MediaType.APPLICATION_RDF_XML;
	protected Reference baseReference;
	protected Template template;
	protected boolean iterateSubjects = true;
	protected Reference reference;
	public boolean isIterateSubjects() {
		return iterateSubjects;
	}

	public void setIterateSubjects(boolean iterateSubjects) {
		this.iterateSubjects = iterateSubjects;
	}

	public RDFObjectIterator(Representation representation, MediaType mediaType, String topObject) throws ResourceException {
		this(OT.createModel(representation,mediaType),topObject);
	}
	
	public RDFObjectIterator(Reference reference, String topObject) throws ResourceException {
		this(OT.createModel(reference, MediaType.APPLICATION_RDF_XML),topObject);
	}
	
	public RDFObjectIterator(Reference reference,MediaType mediaType, String topObject) throws ResourceException {
		this(OT.createModel(reference, mediaType),topObject);
		this.reference = reference;
	}
	
	public RDFObjectIterator(InputStream in,MediaType mediaType, String topObject) throws ResourceException {
		this(OT.createModel(in, mediaType),topObject);
	}	

	public RDFObjectIterator(OntModel model,String topObject, StmtIterator recordIterator) {
		this(model,topObject);
		this.recordIterator = recordIterator;
	}
	public RDFObjectIterator(OntModel model,String topObject) {
		super();
		this.jenaModel = model;
		this.topObject = topObject;
		record = createRecord();
		recordIterator = null;
		setBaseReference(null);

	}
	/**
	 * Verifies if there are more objects of the type
	 */
	public boolean hasNext() {
		recordIterator = initIterator(topObject);
		if ((recordIterator!=null) && recordIterator.hasNext()) {
				Statement st = recordIterator.next();
				Resource newEntry = null;
				if (iterateSubjects)
					newEntry = (Resource) st.getSubject();
				else {
					while (!st.getObject().isResource()) st = recordIterator.next();
					newEntry = (Resource) st.getObject();
				}
				record = parseRecord( newEntry,createRecord());
				return true;
		}
		return false;

	}
	/**
	 * Returns next object
	 */
	public Item next() {
		return record;
	}
	/**
	 * Not available
	 */
	public void remove() {
	}
	
	public void close() {
		recordIterator.close();
	}
	/**
	 * Initializes the iterator
	 * @param otclass
	 * @return
	 */
	private StmtIterator initIterator(String otclass) {
		if (recordIterator == null) {
			Resource s = createResource(otclass);
			if (s==null) return null;
			recordIterator =  jenaModel.listStatements(new SimpleSelector(null,null,s));
		}
		return recordIterator;
	}	
	protected Resource createResource(String otclass) {
		try {
			return OT.OTClass.valueOf(otclass).getOntClass(jenaModel);
		} catch (Exception x) {return null;} 
	}

	
	public MediaType getMediaType() {
		return mediaType;
	}
	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}
	public Template getTemplate() {
		return template;
	}
	public Reference getBaseReference() {
		return baseReference;
	}
	/**
	 * This is mainly used to create {@link Template} to match record uris and determine if they originate from this service
	 * @param baseReference
	 */
	public void setBaseReference(Reference baseReference) {
		this.baseReference = baseReference;
		template = createTemplate();
	}
	
	protected abstract Item parseRecord(Resource newEntry,Item record);
	protected abstract Item createRecord();
	protected abstract void parseObjectURI(RDFNode uri,Item record);
	protected abstract Template createTemplate();

	
	public static String getTitle(RDFNode rdfNode) throws Exception  {
		return getPropertyValue(DC.title,rdfNode);
	}
	public static String getIdentifier(RDFNode rdfNode) throws Exception  {
		return getPropertyValue(DC.identifier,rdfNode);
	}	
	public static String getCreator(RDFNode rdfNode) throws Exception  {
		return getPropertyValue(DC.creator,rdfNode);
	}		
	public static String getPropertyValue(Property property,RDFNode rdfNode) throws Exception  {
		if (rdfNode.isResource())
			return (((Literal) ((Resource)rdfNode).getProperty(property).getObject()).getString()); 
		else throw new Exception("Not a resource");
	}	
}
