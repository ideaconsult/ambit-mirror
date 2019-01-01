package ambit2.rest.routers.opentox;

import org.restlet.Context;

import ambit2.rest.algorithm.AllAlgorithmsResource;
import ambit2.rest.algorithm.MLResources;
import ambit2.rest.routers.MyRouter;

/**
 * OpenTox Algorithms /algorithm
 */
public class AlgorithmRouter extends MyRouter {
	
	public AlgorithmRouter(Context context) {
		super(context);
		attachDefault(AllAlgorithmsResource.class);
		attach(String.format("/{%s}",MLResources.algorithmKey),AllAlgorithmsResource.class);
		attach(String.format("/{%s}/{%s}",MLResources.algorithmKey,
					AllAlgorithmsResource._param.level0.name()),
					AllAlgorithmsResource.class);
		attach(String.format("/{%s}/{%s}/{%s}",MLResources.algorithmKey,
				AllAlgorithmsResource._param.level0.name(),AllAlgorithmsResource._param.level1.name()),
					AllAlgorithmsResource.class);
		attach(String.format("/{%s}/{%s}/{%s}/{%s}",MLResources.algorithmKey,
				AllAlgorithmsResource._param.level0.name(),AllAlgorithmsResource._param.level1.name(),AllAlgorithmsResource._param.level2.name()),
				AllAlgorithmsResource.class);
	}
}
