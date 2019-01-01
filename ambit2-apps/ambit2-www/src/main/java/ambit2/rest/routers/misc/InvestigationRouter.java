package ambit2.rest.routers.misc;

import org.restlet.Context;

import ambit2.rest.routers.MyRouter;
import ambit2.rest.substance.study.SubstanceStudyTableResource;

/**
 * /investigation resource
 * 
 * @author nina
 *
 */
public class InvestigationRouter extends MyRouter {
	public InvestigationRouter(Context context) {
		super(context);
		attachDefault(SubstanceStudyTableResource.class);
	}
}
