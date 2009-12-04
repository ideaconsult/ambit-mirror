package ambit2.descriptors.processors;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.qsar.IMolecularDescriptor;

import ambit2.core.data.model.Algorithm;

/**
 * Creates a list of DescriptorSpecifications for available descriptors
 * @author nina
 *
 */
public class AlgorithmDescriptorFactory extends	AbstractDescriptorFactory<List<Algorithm<String>>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3353935246195424387L;

	@Override
	protected void addToResult(String name, boolean enabled, int order,
			List<Algorithm<String>> result) throws Exception {
		IMolecularDescriptor descriptor = DescriptorsFactory.createDescriptor(name);
		if (descriptor != null) {
			Algorithm<String> alg = new Algorithm<String>(descriptor.getSpecification().getSpecificationReference());
			alg.setContent(descriptor.getSpecification().getImplementationIdentifier()); 
			alg.setId(descriptor.getSpecification().getSpecificationReference());
			result.add(alg);
		}
	}
 
	@Override
	protected List<Algorithm<String>> createResult() {
		return new ArrayList<Algorithm<String>>();
	}

}
