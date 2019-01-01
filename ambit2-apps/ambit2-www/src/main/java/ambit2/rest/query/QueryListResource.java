package ambit2.rest.query;

import java.util.ArrayList;
import java.util.Iterator;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.resource.Finder;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Filter;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.util.RouteList;

import ambit2.rest.algorithm.CatalogResource;

/**
 * Query types
 * @author nina
 *
 */
public class QueryListResource extends CatalogResource {

	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		
		ArrayList<String> q = new ArrayList<String>();
		
		Restlet root = getApplication().getRoot();
		while ((root instanceof Filter)) 
			root = ((Filter)root).getNext();
		RouteList list = ((Router)root).getRoutes();
 		 	for (Route r : list) 
 		 		if ("/query".equals(r.getTemplate().getPattern())) {
 		 			printRoutes(r,"query",q);
 		 			break;
 		 		}	
		//printRoutes(getApplication().getRoot(), "/root", q);
	
		return q.iterator();
	}
	  public static void printRoutes(Restlet re,String delimiter,ArrayList<String> b) {
	   		
	 		while (re != null) {
	 			
	 			if (re instanceof Finder) {
	 				re = null;
	 			} else if (re instanceof Filter)
		 			re = ((Filter)re).getNext();
		 		else if (re instanceof Router) {

		 			RouteList list = ((Router)re).getRoutes();
		 		 	for (Route r : list) { 
		 		 		
		 		 		b.add(delimiter+r.getTemplate().getPattern());
		 		 		printRoutes(r.getNext(),delimiter+r.getTemplate().getPattern(),b);
		 		 	}	
		 		 	
		 			break;
		 		} else {
		 			break;
		 		}
		 		
		 		
	 		}
	 	}



}
