package ambit2.rest.dataset;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import weka.core.Instance;
import weka.core.Instances;
import ambit2.rest.RDFBatchParser;
import ambit2.rest.rdf.OT;
import ambit2.rest.rdf.RDFInstancesIterator;
import ambit2.rest.rdf.RDFObjectIterator;


/**
 * Reads dataset into weka instances
 * @author nina
 *
 */
public class RDFInstancesParser extends RDFBatchParser<Instance> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5276450707875380321L;
	
	public RDFInstancesParser(String baseReference) {
		super(baseReference, OT.OTClass.DataEntry.toString());
	}

	@Override
	protected RDFObjectIterator<Instance> createObjectIterator(
			Reference target, MediaType mediaType) throws ResourceException {
		RDFInstancesIterator i = new RDFInstancesIterator(target,mediaType);
		i.setBaseReference(new Reference(baseReference));
		return i;
	}
	public Instances getInstances() {
		return ((RDFInstancesIterator)rdfObjectIterator).getInstances();
	}
}
