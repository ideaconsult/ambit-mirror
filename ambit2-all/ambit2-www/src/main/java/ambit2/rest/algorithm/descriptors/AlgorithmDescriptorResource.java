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
	public static final String descriptorcalculation="descriptorcalculation";
	public AlgorithmDescriptorResource(Context context, Request request,
			Response response) {
		super(context, request, response);
		query.clear();
		query.add("quantumchemical");
		query.add("physicochemical");
		query.add("patternmining");
		query.add("pharmacopore");
		query.add("simdist");
		setCategory(descriptorcalculation);
	}

}
