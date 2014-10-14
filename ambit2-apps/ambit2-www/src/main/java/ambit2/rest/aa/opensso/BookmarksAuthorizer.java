package ambit2.rest.aa.opensso;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Request;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.routing.Template;

import ambit2.rest.OpenTox;
import ambit2.rest.bookmark.BookmarkResource;

public class BookmarksAuthorizer extends OpenSSOAuthorizer {
	
	@Override
	protected boolean authorize(OpenSSOToken ssoToken, Request request)
			throws Exception {
		if (super.authorize(ssoToken, request)) {
			//parent method only retrieves user name for non-GET
			if (Method.GET.equals(request.getMethod())){
				try {retrieveUserAttributes(ssoToken, request);} 
				catch (Exception x) {}
			}
			return true;
		}
		
		Template template1 = new Template(String.format("%s/bookmark/{%s}",request.getRootRef(),BookmarkResource.creator));

		Template template2 = OpenTox.URI.bookmark.getTemplate(request.getRootRef());
		Template template3 = new Template(String.format("%s/bookmark/{%s}/entries",request.getRootRef(),BookmarkResource.creator));
		Template template4 = new Template(String.format("%s/bookmark/{%s}/topics",request.getRootRef(),BookmarkResource.creator));
		Map<String, Object> vars = new HashMap<String, Object>();
		Reference ref = request.getResourceRef().clone();
		ref.setQuery(null);
		template1.parse(ref.toString(),vars);
		template2.parse(ref.toString(),vars);
		template3.parse(ref.toString(),vars);
		template4.parse(ref.toString(),vars);
		
		try {retrieveUserAttributes(ssoToken, request);} catch (Exception x) { logger.log(Level.WARNING,x.getMessage(),x);}
		return request.getClientInfo().getUser().getIdentifier().equals(vars.get(BookmarkResource.creator));
	}
	@Override
	protected boolean isEnabled() {
		return true;
	}
	
}
