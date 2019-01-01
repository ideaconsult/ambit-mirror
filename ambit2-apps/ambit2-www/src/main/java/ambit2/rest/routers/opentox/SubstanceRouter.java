package ambit2.rest.routers.opentox;

import org.restlet.Context;

import ambit2.rest.routers.MyRouter;
import ambit2.rest.substance.SubstanceResource;
import ambit2.rest.substance.composition.SubstanceCompositionResource;
import ambit2.rest.substance.composition.SubstanceStructuresResource;
import ambit2.rest.substance.study.SubstanceStudyFacetResource;
import ambit2.rest.substance.study.SubstanceStudyResource;

/**
 * Everything under /substance resource
 * @author nina
 *
 */
public class SubstanceRouter extends MyRouter {

	public SubstanceRouter(Context context) {
		super(context);
		/*
			Moved from application class
			router.attach(SubstanceResource.substance, SubstanceResource.class);
			router.attach(SubstanceResource.substanceID,
					SubstanceResource.class);
			router.attach(String.format("%s%s", SubstanceResource.substanceID,
					SubstanceStructuresResource.structure),
					SubstanceStructuresResource.class);
			router.attach(String.format("%s%s/{%s}",
					SubstanceResource.substanceID,
					SubstanceStructuresResource.structure,
					SubstanceStructuresResource.compositionType),
					SubstanceStructuresResource.class);
			router.attach(String.format("%s/%s", SubstanceResource.substanceID,
					SubstanceStudyFacetResource.resource),
					SubstanceStudyFacetResource.class);
			router.attach(String.format("%s%s", SubstanceResource.substanceID,
					SubstanceStudyResource.study), SubstanceStudyResource.class);

			router.attach(String.format("%s%s", SubstanceResource.substanceID,
					SubstanceCompositionResource.composition),
					SubstanceCompositionResource.class);
			router.attach(String.format("%s%s", SubstanceResource.substanceID,
					SubstanceCompositionResource.compositionID),
					SubstanceCompositionResource.class);
		
		*/
		attachDefault(SubstanceResource.class);
		/**
		 * /substance/ /substance/{id}
		 */
		attach(String.format("/{%s}",SubstanceResource.idsubstance), SubstanceResource.class);

		/**
		 * /substance/{id}/structure
		 */
		attach(String.format("/{%s}%s", SubstanceResource.idsubstance,
				SubstanceStructuresResource.structure),
				SubstanceStructuresResource.class);
		attach(String.format("/{%s}%s/{%s}",
				SubstanceResource.idsubstance,
				SubstanceStructuresResource.structure,
				SubstanceStructuresResource.compositionType),
				SubstanceStructuresResource.class);
		/**
		 * /substance/{id}/studysummary
		 */
		attach(String.format("/{%s}/%s", SubstanceResource.idsubstance,
				SubstanceStudyFacetResource.resource),
				SubstanceStudyFacetResource.class);

		/**
		 * /substance/{id}/study
		 */
		attach(String.format("/{%s}%s", SubstanceResource.idsubstance,
				SubstanceStudyResource.study), SubstanceStudyResource.class);

		/**
		 * /substance/{id}/composition/{type}
		 */
		attach(String.format("/{%s}%s", SubstanceResource.idsubstance,
				SubstanceCompositionResource.composition),
				SubstanceCompositionResource.class);
		attach(String.format("/{%s}%s", SubstanceResource.idsubstance,
				SubstanceCompositionResource.compositionID),
				SubstanceCompositionResource.class);

		
	}

}