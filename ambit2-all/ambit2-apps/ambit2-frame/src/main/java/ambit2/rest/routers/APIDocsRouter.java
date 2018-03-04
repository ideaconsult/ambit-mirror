package ambit2.rest.routers;

import org.restlet.Context;

import ambit2.rest.ui.APIdocsResource;

public class APIDocsRouter extends MyRouter {

	public APIDocsRouter(Context context) {
		super(context);
		init();
	}

	protected void init() {
		attachDefault(APIdocsResource.class);
		attach("/{key1}", APIdocsResource.class);
		attach("/{key1}/{key2}", APIdocsResource.class);
	}
}