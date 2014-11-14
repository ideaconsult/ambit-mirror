package ambit2.rest.routers.opentox;

import org.restlet.Context;
import org.restlet.routing.Router;

import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.propertyvalue.PropertyTemplateResource;
import ambit2.rest.routers.MyRouter;
import ambit2.rest.structure.CompoundImageJSONResource;
import ambit2.rest.structure.CompoundImageResource;
import ambit2.rest.structure.ConformerResource;
import ambit2.rest.structure.dataset.DatasetsByStructureResource;
import ambit2.rest.structure.quality.ConsensusLabelResource;
import ambit2.rest.structure.quality.QualityLabelResource;

public class ConformerRouter extends MyRouter {

	public ConformerRouter(Context context,FeaturesRouter featuresRouter,Router templateRouter) {
		super(context);
		attachDefault(ConformerResource.class);
		attach(String.format("%s",ConsensusLabelResource.resource),ConsensusLabelResource.class);
		attach(String.format("%s",QualityLabelResource.resource),QualityLabelResource.class);
		attach(String.format("%s",DatasetsResource.datasets),DatasetsByStructureResource.class);
		attach(String.format("%s",CompoundImageResource.resource),CompoundImageResource.class);
		attach(String.format("%s",CompoundImageJSONResource.resource),CompoundImageJSONResource.class);
		
		/**
		 *  /compound/{id}/conformer/{id}/feature
		 */
		attach(PropertyResource.featuredef,featuresRouter);
		attach(PropertyTemplateResource.resource,templateRouter);

	}
}
