package ambit2.rest.routers.opentox;

import org.restlet.Context;

import ambit2.rest.property.PropertyResource;
import ambit2.rest.property.annotations.PropertyAnnotationResource;
import ambit2.rest.routers.MyRouter;

/**
 * OpenTox Features  /feature
 */
public class FeaturesRouter extends MyRouter {

	public FeaturesRouter(Context context) {
		super(context);
		init();
	}

	protected void init() {
		/*
		router.attach(PropertyResource.CompoundFeaturedef,PropertyResource.class);
		router.attach(PropertyResource.ConformerFeaturedef,PropertyResource.class);
		router.attach(PropertyResource.CompoundFeaturedefID,PropertyResource.class);
		router.attach(PropertyResource.ConformerFeaturedefID,PropertyResource.class);
		

		//API 1.0 remnants
		//compoundRouter.attach(PropertyValueResource.featureKey,PropertyValueResource.class);

		//Router featureByAlias = new MyRouter(getContext());
		//featureByAlias.attachDefault(PropertyValueResource.class);
		
		//compoundRouter.attach(String.format("%s/{name}",PropertyValueResource.featureKey),featureByAlias);
		//conformerRouter.attach(String.format("%s/{name}",PropertyValueResource.featureKey),featureByAlias);
		
		//router.attach(PropertyValueResource.compoundFeatureName,PropertyValueResource.class);
		//router.attach(PropertyValueResource.conformerFeatureName,PropertyValueResource.class);
		
		//router.attach(PropertyValueResource.FeatureNameCompound,PropertyValueResource.class);
		//router.attach(PropertyValueResource.FeatureNameConformer,PropertyValueResource.class);
		 
	 	//  /feature_value as per API 1.0
		router.attach(FeatureResource.CompoundFeaturedefID,FeatureResource.class);
		router.attach(FeatureResource.ConformerFeaturedefID,FeatureResource.class);		
		*/

		attachDefault(PropertyResource.class);
		attach(PropertyResource.featuredefID,PropertyResource.class);
		attach(String.format("%s%s",PropertyResource.featuredefID,PropertyAnnotationResource.annotation),
									PropertyAnnotationResource.class);
	}

}
