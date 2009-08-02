package ambit2.rest.algorithm.descriptors;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.rest.algorithm.AlgorithmResource;

/**
 * Descriptor calculation resources
 * @author nina
 *
 */
public class AlgorithmDescriptorResource extends AlgorithmResource {
	public enum descriptortypes  {
		quantumchemical,physicochemical,patternmining,pharmacophore,simdist
	};
	public AlgorithmDescriptorResource(Context context, Request request,
			Response response) {
		super(context, request, response);
		setCategory(AlgorithmResource.alsgorithmtypes.descriptorcalculation.toString());
		query.clear();
		for (descriptortypes d : descriptortypes.values())
			query.add(String.format("algorithm/%s/%s",getCategory(),d.toString()));

	}

}
