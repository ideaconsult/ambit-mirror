package ambit2.rest.algorithm.descriptors;

import java.util.ArrayList;
import java.util.logging.Logger;

import ambit2.core.data.model.Algorithm;
import ambit2.descriptors.processors.AlgorithmDescriptorFactory;
import ambit2.rest.algorithm.AllAlgorithmsResource;

/**
 * List of descriptors, retrieved from BO descriptor ontology
 * @author nina
 *
 */
public class DescriptorResource extends AllAlgorithmsResource {
	public DescriptorResource() {
		super();
		setCategory("test");
	}
	@Override
	protected void initList() {
		if (algorithmList==null) {
			algorithmList = new ArrayList<Algorithm<String>>();
			AlgorithmDescriptorFactory f = new AlgorithmDescriptorFactory();
			try {
				algorithmList = f.process(null);
			} catch (Exception x) {
				Logger.getAnonymousLogger().severe(x.getMessage());
			}
		}
	}
}
