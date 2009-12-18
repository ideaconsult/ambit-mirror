package ambit2.rest;

import java.util.Iterator;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.processors.AbstractBatchProcessor;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Parent class for all RDF parsers
 * @author nina
 *
 * @param <Item>
 */
public abstract class RDFBatchParser<Item>  extends AbstractBatchProcessor<Reference, Item> {
	protected MediaType mediaType = MediaType.APPLICATION_RDF_XML;
	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}
	protected OntModel jenaModel;
	protected String baseReference;
	protected Item record;
	protected StmtIterator recordIterator;
	protected String topObject;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8921517639325859050L;


	public RDFBatchParser(String baseReference,String objectToParse) {
		super();
		this.baseReference = baseReference;
		topObject = objectToParse;
		
	}
	
	public String getBaseReference() {
		return baseReference;
	}
	public void setBaseReference(String baseReference) {
		this.baseReference = baseReference;
	}

	@Override
	public void beforeProcessing(Reference target) throws AmbitException {
		super.beforeProcessing(target);
		try {
			ClientResource client = new ClientResource(target);
			Representation r = client.get(mediaType);
			if (r==null)
				throw new AmbitException("Null representation");
			if (client.getStatus().equals(Status.SUCCESS_OK)) {
				record = createRecord();
				jenaModel = OT.createModel();
				jenaModel.read(r.getStream(),null);
			} else throw new AmbitException(client.getStatus().getDescription());
			
		} catch (Exception x) {
			throw new AmbitException(x);
		}		
	}	
	protected abstract Item createRecord();
	@Override
	public void afterProcessing(Reference target,
			Iterator<Item> iterator) throws AmbitException {
		try {recordIterator.close();} catch (Exception x){};
		super.afterProcessing(target, iterator);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ambit2.base.interfaces.IBatchProcessor#getIterator(java.lang.Object)
	 */
		public Iterator<Item> getIterator(Reference target)
				throws AmbitException {
				
			return new Iterator<Item>() {
				public boolean hasNext() {
					recordIterator = initIterator(topObject);
					if ((recordIterator!=null) && recordIterator.hasNext()) {
							Statement st = recordIterator.next();
							Resource newEntry = (Resource) st.getSubject();
							record = parseRecord( newEntry,createRecord());
							return true;
					}
					return false;
				}
				public Item next() {
					return record;
				}
				public void remove() {
				}
			};
		}
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
		protected abstract Item parseRecord(Resource newEntry,Item record);
}
