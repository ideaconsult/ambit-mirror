package ambit2.rest.dataset;

import net.idea.restnet.rdf.ns.OT;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.RDFBatchParser;
import ambit2.rest.rdf.RDFObjectIterator;
import ambit2.rest.rdf.RDFStructuresIterator;

/**
 * Reads RDF representation of a dataset and converts it into iterator of IStructureRecord
 * @author nina
 *
 */
public class RDFStructuresReader extends RDFBatchParser<IStructureRecord> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7079139854857529405L;

	public RDFStructuresReader(String baseReference) {
		super(baseReference, OT.OTClass.DataEntry.toString());
	}

	@Override
	protected RDFObjectIterator<IStructureRecord> createObjectIterator(
			Reference target, MediaType mediaType) throws ResourceException {
		try {
			RDFStructuresIterator i = new RDFStructuresIterator(target,mediaType);
			i.setBaseReference(new Reference(baseReference));
			return i;
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(x);
		}
	}
}
