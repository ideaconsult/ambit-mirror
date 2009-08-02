package ambit2.rest.algorithm.descriptors;

import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.rest.algorithm.AlgorithmResource;

/**
 * Descriptor calculation resources
 * @author nina
 *
 */
public class AlgorithmDescriptorTypesResource extends AlgorithmResource {
	public static final String iddescriptor = "iddescriptor";
	protected IMolecularDescriptor descriptor = null;
	public enum descriptortypes  {
		constitutionalDescriptor,geometricalDescriptor,topologicalDescriptor,quantumchemical,physicochemical,patternmining,pharmacophore,simdist
	};
	public AlgorithmDescriptorTypesResource(Context context, Request request,
			Response response) {
		super(context, request, response);
		setCategory(AlgorithmResource.algorithmtypes.descriptorcalculation.toString());
		query.clear();
		for (descriptortypes d : descriptortypes.values())
			query.add(String.format("algorithm/%s/%s",getCategory(),d.toString()));

	}

}
