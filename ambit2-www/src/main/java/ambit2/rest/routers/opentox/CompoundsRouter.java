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
		 *  Sets of features 
		 *  /template 
		 *  /compound/{id}/template
		 *  /compound/{id}/conformer/{id}/template
		 */
		Router templateRouter = new MyRouter(getContext());
		templateRouter.attachDefault(PropertyTemplateResource.class);
		templateRouter.attach(PropertyTemplateResource.resourceID,PropertyTemplateResource.class);
		
		/**
		 * Conformers router   /compound/id/conformer
		 */
		Router conformersRouter = new MyRouter(getContext());
		conformersRouter.attachDefault(ConformerResource.class);
		conformersRouter.attach(String.format("/{%s}",ConformerResource.idconformer),
						new ConformerRouter(getContext(),featuresRouter,templateRouter,tupleRouter));			
		/**
		 * Compounds router   /compound
		 */
		attachDefault(CompoundResource.class);
		attach(String.format("/{%s}",CompoundResource.idcompound),
					new CompoundRouter(getContext(),conformersRouter,featuresRouter,templateRouter,tupleRouter,smartsRouter));
	}	
}
