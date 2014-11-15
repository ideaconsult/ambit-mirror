package ambit2.rest.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Iterator;

import net.idea.restnet.rdf.ns.OT;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Iterates over objects of a specified OT.class
 * TODO make use of http://jena.sourceforge.net/ARP/standalone.html
 * @author nina
 *
 * @param <Item>
 */
public abstract class RDFObjectIterator<Item> implements Iterator<Item> {
	protected boolean forceReadRDFLocalObjects = false;
	public boolean isForceReadRDFLocalObjects() {
		return forceReadRDFLocalObjects;
	}

	public void setForceReadRDFLocalObjects(boolean forceReadRDFLocalObjects) {
		this.forceReadRDFLocalObjects = forceReadRDFLocalObjects;
	}
	protected OntModel jenaModel;
	public OntModel getJenaModel() {
		return jenaModel;
	}
	protected Item record;
	protected StmtIterator recordIterator;	
	protected String topObject;
	protected MediaType mediaType = MediaType.APPLICATION_RDF_XML;
	protected Reference baseReference;
	protected Template template;
	protected boolean iterateSubjects = true;
	protected Reference reference;
	boolean closeModel = false;
	public boolean isCloseModel() {
		return closeModel;
	}

	public void setCloseModel(boolean closeModel) {
		this.closeModel = closeModel;
	}

	public boolean isIterateSubjects() {
		return iterateSubjects;
	}

	public void setIterateSubjects(boolean iterateSubjects) {
		this.iterateSubjects = iterateSubjects;
	}

	public RDFObjectIterator(Representation representation, MediaType mediaType, String topObject) throws ResourceException {
		this(OT.createModel(null,representation,mediaType),topObject);
	}
	
	public RDFObjectIterator(Reference reference, String topObject) throws ResourceException,MalformedURLException,IOException {
		this(OT.createModel(null,reference, MediaType.APPLICATION_RDF_XML),topObject);
		this.reference = reference;
	}
	
	public RDFObjectIterator(Reference reference,MediaType mediaType, String topObject) throws ResourceException,MalformedURLException,IOException {
		this(OT.createModel(null,reference, mediaType),topObject);
		this.reference = reference;
	}
	
	public RDFObjectIterator(InputStream in,MediaType mediaType, String topObject) throws ResourceException {
		this(OT.createModel(null,in, mediaType),topObject);
	}	

	public RDFObjectIterator(Model model,String topObject, StmtIterator recordIterator) {
		this(model,topObject);
		this.recordIterator = recordIterator;
	}
	public RDFObjectIterator(Model model,String topObject) {
		super();
		this.jenaModel = (OntModel)model;
		this.topObject = topObject;
		record = createRecord();
		recordIterator = null;
		setBaseReference(null);

	}
	protected boolean skip(Resource newEntry) {
		return newEntry.isURIResource() && (
				   newEntry.getURI().equals("http://www.opentox.org/api/1.1#NumericFeature") ||
				   newEntry.getURI().equals("http://www.opentox.org/api/1.1#NominalFeature") ||
				   newEntry.getURI().equals("http://www.opentox.org/api/1.1#StringFeature") 
				   );
	}
	/**
	 * Verifies if there are more objects of the type
	 */
	public boolean hasNext() {
		recordIterator = initIterator(topObject);
		if ((recordIterator!=null) && recordIterator.hasNext()) {
				Statement st = recordIterator.next();
				Resource newEntry = null;
				if (iterateSubjects) {
					newEntry = (Resource) st.getSubject();
					while (skip(newEntry)) {
						if ( recordIterator.hasNext() ) 	st = recordIterator.next();
						else return false;
						newEntry = (Resource) st.getSubject();
					}
					
				} else {
					while (!st.getObject().isResource() ) st = recordIterator.next();
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
		if (closeModel) jenaModel.close();
		
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
			recordIterator =  jenaModel.listStatements(new SimpleSelector(null,RDF.type,s));
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
	
	protected abstract Item parseRecord(RDFNode newEntry,Item record);
	protected abstract Item createRecord();
	protected abstract void parseObjectURI(RDFNode uri,Item record);
	protected abstract Template createTemplate();

	
	public static String getTitle(RDFNode rdfNode) throws Exception  {
		return getPropertyValue(DC.title,rdfNode);
	}
	public static String getURI(RDFNode rdfNode) throws Exception  {
		return rdfNode.isURIResource()?((Resource)rdfNode).getURI():null;
	}	
	public static String getCreator(RDFNode rdfNode) throws Exception  {
		return getPropertyValue(DC.creator,rdfNode);
	}		
	public static String getPropertyValue(Property property,RDFNode rdfNode) throws Exception  {
		if (rdfNode.isResource()) {
			Statement st = ((Resource)rdfNode).getProperty(property);
			if ((st==null) || st.getObject()==null) return null;
			return (((Literal) st.getObject()).getString());
		}
		else throw new Exception("Not a resource");
	}
	public static RDFNode getPropertyNode(Property property,RDFNode rdfNode)  {
		if (rdfNode.isResource()) {
			Statement st = ((Resource)rdfNode).getProperty(property);
			if (st!= null)	return ((Resource)rdfNode).getProperty(property).getObject();
			else return null;
		} else return null;
	}	
	
	public static String removeDatasetFragment(String uri) {
		int posDataset = uri.indexOf("/dataset/");
		if (posDataset<0) return uri;
		
		int posCompound = uri.indexOf("/compound/");
		if (posCompound > 0) return String.format("%s%s", uri.substring(0,posDataset),uri.substring(posCompound));
		return uri;
	}
	
}
