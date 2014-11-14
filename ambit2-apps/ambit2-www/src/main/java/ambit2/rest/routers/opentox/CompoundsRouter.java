package ambit2.rest.routers.opentox;

import org.restlet.Context;
import org.restlet.routing.Router;

import ambit2.rest.routers.MyRouter;
import ambit2.rest.structure.CompoundResource;

/**
 * OpenTox compound
 * @author nina
 *
 */
public class CompoundsRouter extends MyRouter {
	
	public CompoundsRouter(Context context,FeaturesRouter featuresRouter,Router smartsRouter) {
		super(context);
		
		/**
		 * Compounds router   /compound
		 */
		attachDefault(CompoundResource.class);
		attach(String.format("/{%s}",CompoundResource.idcompound),
					new CompoundRouter(getContext(),featuresRouter,smartsRouter));
	}	
}
