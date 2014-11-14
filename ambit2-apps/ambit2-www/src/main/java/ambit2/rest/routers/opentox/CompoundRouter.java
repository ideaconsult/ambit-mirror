package ambit2.rest.routers.opentox;

import org.restlet.Context;
import org.restlet.routing.Router;

import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.propertyvalue.PropertyTemplateResource;
import ambit2.rest.query.SmartsQueryResource;
import ambit2.rest.routers.MyRouter;
import ambit2.rest.structure.CompoundImageJSONResource;
import ambit2.rest.structure.CompoundImageResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;
import ambit2.rest.structure.dataset.DatasetsByStructureResource;
import ambit2.rest.structure.quality.ConsensusLabelResource;
import ambit2.rest.structure.quality.QualityLabelResource;

/**
 * Single compound /compound/id
 * @author nina
 *
 */
public class CompoundRouter extends MyRouter {

	public CompoundRouter(Context context,
			FeaturesRouter featuresRouter,
			Router smartsRouter) {
		super(context);
		attachDefault();


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
						new ConformerRouter(getContext(),featuresRouter,templateRouter));	
	
		
		/**
		* Consensus labels   /consensus  
		*/
		attach(String.format("%s",ConsensusLabelResource.resource),ConsensusLabelResource.class);
		/**
		*  Comparison between different sources  /comparison
		*/
		attach(String.format("%s",QualityLabelResource.resource),QualityLabelResource.class);		
		/**
		*  Datasets  /dataset
		*/
		attach(String.format("%s",DatasetsResource.datasets),DatasetsByStructureResource.class);
		/**
		*  Image  /image
		*/
		attach(String.format("%s",CompoundImageResource.resource),CompoundImageResource.class);
		attach(String.format("%s",CompoundImageJSONResource.resource),CompoundImageJSONResource.class);
		
		/**
		*  /compound/{id}/conformer
		*/
		attach(ConformerResource.conformerKey,conformersRouter);
		
		/**
		*  Set of features  /template
		*/
		attach(PropertyTemplateResource.resource,templateRouter);
		/**
		*  Features per compound / conformer
		*  /compound/{id}/feature
		*/
		attach(PropertyResource.featuredef,featuresRouter);		
		
		/**
		* SMARTS search, restricted to a compound (with highlighting)
		*/
		attach(SmartsQueryResource.resource,smartsRouter);
		
	}
	
	public void attachDefault() {
		attachDefault(CompoundResource.class);
	}
	
}
