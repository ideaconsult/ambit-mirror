package ambit2.rest.routers.misc;

import org.restlet.Context;

import ambit2.rest.routers.MyRouter;
import ambit2.rest.ui.UIResource;

/**
 * /ui and descendants
 * @author nina
 *
 */
public class UIRouter extends MyRouter {

	public UIRouter(Context context) {
		super(context);
		init();
	}
	protected void init() {
		attachDefault(UIResource.class);
		attach("/{key}", UIResource.class);
	}
}
