package net.idea.ambit.app.router;

import org.restlet.Context;

import ambit2.rest.routers.MyRouter;
import ambit2.rest.ui.UIResourceBase;

/**
 * /ui and descendants
 * 
 * @author nina
 *
 */
public class UIRouter extends MyRouter {

	public UIRouter(Context context) {
		super(context);
		init();
	}

	protected void init() {
		attachDefault(UIResourceBase.class);
		attach("/{key}", UIResourceBase.class);
	}
}
