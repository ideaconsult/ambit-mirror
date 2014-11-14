package ambit2.rest;

import java.util.Iterator;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.rest.rdf.RDFObjectIterator;

/**
 * Parent class for all RDF parsers
 * @author nina
 *
 * @param <Item>
 */
public abstract class RDFBatchParser<Item>  extends AbstractBatchProcessor<Reference, Item> {
	
	protected RDFObjectIterator<Item> rdfObjectIterator;
	
	protected String baseReference;
	protected String objectToParse;
	protected MediaType mediaType = MediaType.APPLICATION_RDF_XML;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8921517639325859050L;


	public RDFBatchParser(String baseReference,String objectToParse) {
		super();
		this.baseReference = baseReference;
		this.objectToParse = objectToParse;		
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
			rdfObjectIterator = createObjectIterator(target, mediaType);
			
		} catch (Exception x) {
			throw new AmbitException(x);
		}		
	}	
	
	protected abstract RDFObjectIterator<Item> createObjectIterator(Reference target, MediaType mediaType) throws ResourceException;

	@Override
	public void afterProcessing(Reference target,
			Iterator<Item> iterator) throws AmbitException {
		try {rdfObjectIterator.close();} catch (Exception x){};
		super.afterProcessing(target, iterator);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ambit2.base.interfaces.IBatchProcessor#getIterator(java.lang.Object)
	 */
	public Iterator<Item> getIterator(Reference target)
				throws AmbitException {
		return rdfObjectIterator;
	}

}
