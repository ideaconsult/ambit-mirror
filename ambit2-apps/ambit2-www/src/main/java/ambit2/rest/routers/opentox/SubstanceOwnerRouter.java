package ambit2.rest.routers.opentox;

import org.restlet.Context;

import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.routers.MyRouter;
import ambit2.rest.substance.SubstanceDatasetResource;
import ambit2.rest.substance.SubstanceResource;
import ambit2.rest.substance.composition.SubstanceCompositionResource;
import ambit2.rest.substance.composition.SubstanceStructuresResource;
import ambit2.rest.substance.owner.OwnerStructuresResource;
import ambit2.rest.substance.owner.OwnerSubstanceFacetResource;
import ambit2.rest.substance.owner.SubstanceByOwnerResource;
import ambit2.rest.substance.study.SubstanceStudyFacetResource;
import ambit2.rest.substance.study.SubstanceStudyResource;


/**
 * Everything under /substanceowner resource
 * @author nina
 *
 */
public class SubstanceOwnerRouter extends MyRouter {
	public SubstanceOwnerRouter(Context context) {
		super(context);
		/*
			Moved from application class
		
			// legal entity - substance owner (as in IUC5)
			router.attach(
					String.format("%s", OwnerSubstanceFacetResource.owner),
					OwnerSubstanceFacetResource.class);
			// router.attach(String.format("%s%s",OwnerSubstanceFacetResource.owner,SubstanceCompositionResource.composition),OwnerStructuresResource.class);
			router.attach(String.format("%s%s",
					OwnerSubstanceFacetResource.ownerID,
					SubstanceResource.substance),
					SubstanceByOwnerResource.class);
			router.attach(String.format("%s%s",
					OwnerSubstanceFacetResource.ownerID,
					DatasetResource.dataset), SubstanceDatasetResource.class);

			router.attach(String.format("%s%s",
					OwnerSubstanceFacetResource.ownerID,
					SubstanceStructuresResource.structure),
					OwnerStructuresResource.class);

			router.attach(String.format("%s%s/{%s}",
					OwnerSubstanceFacetResource.ownerID,
					SubstanceStructuresResource.structure,
					SubstanceStructuresResource.compositionType),
					OwnerStructuresResource.class);
		
		*/
		attachDefault(OwnerSubstanceFacetResource.class);
		/**
		 * /substanceowner/{id}/substance
		 */
		attach(String.format("/{%s}%s",OpenTox.URI.substanceowner.getKey(),SubstanceResource.substance), SubstanceByOwnerResource.class);
		attach(String.format("/{%s}%s",OpenTox.URI.substanceowner.getKey(),DatasetResource.dataset), SubstanceDatasetResource.class);
		attach(String.format("/{%s}%s",OpenTox.URI.substanceowner.getKey(),SubstanceStructuresResource.structure), OwnerStructuresResource.class);
		attach(String.format("/{%s}%s",OpenTox.URI.substanceowner.getKey(),SubstanceStructuresResource.structure,SubstanceStructuresResource.compositionType), OwnerStructuresResource.class);

		
	}

}