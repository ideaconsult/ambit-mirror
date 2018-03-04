package net.idea.ambit.app.router;

import org.restlet.Context;

import ambit2.rest.algorithm.AlgorithmListResource;
import ambit2.rest.algorithm.MLResources;
import ambit2.rest.routers.MyRouter;

/**
 * OpenTox Algorithms /algorithm
 */
public class AlgorithmRouter extends MyRouter {
	
	public AlgorithmRouter(Context context) {
		super(context);

		attachDefault(AlgorithmListResource.class);
		attach(String.format("/{%s}",MLResources.algorithmKey),AlgorithmListResource.class);
		attach(String.format("/{%s}/{%s}",MLResources.algorithmKey,
				AlgorithmListResource._param.level0.name()),
				AlgorithmListResource.class);
		attach(String.format("/{%s}/{%s}/{%s}",MLResources.algorithmKey,
				AlgorithmListResource._param.level0.name(),AlgorithmListResource._param.level1.name()),
				AlgorithmListResource.class);
		attach(String.format("/{%s}/{%s}/{%s}/{%s}",MLResources.algorithmKey,
				AlgorithmListResource._param.level0.name(),AlgorithmListResource._param.level1.name(),AlgorithmListResource._param.level2.name()),
				AlgorithmListResource.class);

	}
}
