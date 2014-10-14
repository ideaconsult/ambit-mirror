package ambit2.rest.routers.misc;

import org.restlet.Context;

import ambit2.rest.algorithm.chart.ChartResource;
import ambit2.rest.routers.MyRouter;

/**
 * Visualisation
 * Chart /chart/xy , /chart/pie , /chart/bar - TODO - move to /algorithm ?
 */
public class ChartRouter extends MyRouter {

	public ChartRouter(Context context) {
		super(context);
		attachDefault(ChartResource.class);
		attach(String.format("/{%s}",ChartResource.resourceKey),ChartResource.class);

	}

}
