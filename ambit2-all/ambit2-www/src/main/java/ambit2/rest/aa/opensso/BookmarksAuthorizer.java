package ambit2.rest.aa.opensso;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Request;
import org.restlet.routing.Template;

import ambit2.rest.OpenTox;
import ambit2.rest.bookmark.BookmarkResource;

public class BookmarksAuthorizer extends OpenSSOAuthorizer {
	
	@Override
	protected boolean authorize(OpenSSOToken ssoToken, Request request)
			throws Exception {
		if (super.authorize(ssoToken, request)) return true;
		
		Template template1 = new Template(String.format("%s/bookmark/{%s}",request.getRootRef(),BookmarkResource.creator));
		Template template2 = OpenTox.URI.bookmark.getTemplate(request.getRootRef());
		Map<String, Object> vars = new HashMap<String, Object>();
		template1.parse(request.getResourceRef().toString(),vars);
		template2.parse(request.getResourceRef().toString(),vars);

		Hashtable<String,String> results = new Hashtable<String, String>();
		
		ssoToken.getAttributes(new String[] {"uid"}, results);
		try {request.getClientInfo().getUser().setIdentifier(results.get("uid"));} catch (Exception x) {}
		return results.get("uid").equals(vars.get(BookmarkResource.creator));
	}
	@Override
	protected boolean isEnabled() {
		return true;
	}
	
}
