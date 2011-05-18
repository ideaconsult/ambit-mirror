package ambit2.rest.routers.opentox;

import org.restlet.Context;

import ambit2.rest.algorithm.AllAlgorithmsResource;
import ambit2.rest.routers.MyRouter;

/**
 * OpenTox Algorithms /algorithm
 */
public class AlgorithmRouter extends MyRouter {
	
	public AlgorithmRouter(Context context) {
		super(context);
		attachDefault(AllAlgorithmsResource.class);
		attach(String.format("/{%s}",AllAlgorithmsResource.algorithmKey),AllAlgorithmsResource.class);
	}
}
