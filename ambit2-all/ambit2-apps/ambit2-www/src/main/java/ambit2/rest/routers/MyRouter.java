package ambit2.rest.routers;

import org.restlet.Context;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

/**
 * For backward compatibility with Restlet <= 2.0-M5 
 */
public class MyRouter extends Router {
	public MyRouter(Context context) {
		 super(context);
	     setDefaultMatchingMode(Template.MODE_STARTS_WITH); 
	     setRoutingMode(Router.MODE_BEST_MATCH); 
	}
}