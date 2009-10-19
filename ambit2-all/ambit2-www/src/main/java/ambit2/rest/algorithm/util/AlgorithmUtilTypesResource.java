package ambit2.rest.algorithm.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.rest.StatusException;
import ambit2.rest.algorithm.AlgorithmCatalogResource;

/**
 * Descriptor calculation resources  http://opentox.org/development/wiki/Algorithms
 * Utility algorithms 
 * @author nina
 *
 */
public class AlgorithmUtilTypesResource extends AlgorithmCatalogResource {
	public static enum utiltypes  {
		name2structure,build3d
	};

	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws StatusException {
		setCategory("util");
		ArrayList<String> q = new ArrayList<String>();
 		for (utiltypes d : utiltypes.values())
			q.add(String.format("algorithm/%s/%s",getCategory(),d.toString()));
 		return q.iterator();
	}

}
