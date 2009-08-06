package ambit2.rest.algorithm.descriptors;

import java.util.Iterator;

import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.w3c.dom.Document;

import ambit2.base.processors.batch.ListReporter;
/**
 * XML representation of a descriptor calculation algorithm
 * @author nina
 *
 */
public class DescriptorDOMReporter extends ListReporter<IMolecularDescriptor, Document> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5295250216607367845L;

	@Override
	public void footer(Document output, Iterator<IMolecularDescriptor> query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void header(Document output, Iterator<IMolecularDescriptor> query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processItem(IMolecularDescriptor item, Document output) {
		// TODO Auto-generated method stub
		
	}

	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
