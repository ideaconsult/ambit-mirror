package ambit2.rest.algorithm.util;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.rest.algorithm.AlgorithmResource;

/**
 * Descriptor calculation resources
 * @author nina
 *
 */
public class AlgorithmUtilTypesResource extends AlgorithmResource {
	public static enum utiltypes  {
		depict,name2structure,build3d
	};
	public AlgorithmUtilTypesResource(Context context, Request request,
			Response response) {
		super(context, request, response);
		setCategory("util");
		query.clear();
		for (utiltypes d : utiltypes.values())
			query.add(String.format("algorithm/%s/%s",getCategory(),d.toString()));

	}

}
