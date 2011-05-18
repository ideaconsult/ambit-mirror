package ambit2.rest.routers.misc;

import org.restlet.Context;

import ambit2.rest.routers.MyRouter;
import ambit2.rest.structure.diagram.AbstractDepict;
import ambit2.rest.structure.diagram.CDKDepict;
import ambit2.rest.structure.diagram.CSLSDepict;
import ambit2.rest.structure.diagram.DaylightDepict;

/**
 * 2Dstructure diagram demo
 * /depict
 * /depict/cdk
 * /depict/daylight
 * /depict/cactvs
 * @author nina
 *
 */
public class DepictDemoRouter extends MyRouter {

	public DepictDemoRouter(Context context) {
		super(context);
		attachDefault(AbstractDepict.class);
		attach("/daylight",DaylightDepict.class);
		attach("/cdk",CDKDepict.class);
		attach("/cactvs",CSLSDepict.class);
	}

}
