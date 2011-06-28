package ambit2.rest.routers.opentox;

import org.restlet.Context;
import org.restlet.routing.Router;

import ambit2.rest.propertyvalue.PropertyTemplateResource;
import ambit2.rest.routers.MyRouter;
import ambit2.rest.routers.misc.DataEntryRouter;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

/**
 * OpenTox compound
 * @author nina
 *
 */
public class CompoundsRouter extends MyRouter {
	
	public CompoundsRouter(Context context,FeaturesRouter featuresRouter,DataEntryRouter tupleRouter,Router smartsRouter) {
		super(context);
		
		/**
		 * Compounds router   /compound
		 */
		attachDefault(CompoundResource.class);
		attach(String.format("/{%s}",CompoundResource.idcompound),
					new CompoundRouter(getContext(),featuresRouter,tupleRouter,smartsRouter));
	}	
}
