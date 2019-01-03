package ambit2.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Header;
import org.restlet.data.Method;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.routing.Filter;
import org.restlet.util.Series;

/**
 * CORS
 * 
 * @author nina
 *
 */
public class OriginFilter extends Filter {
	protected List<String> allowedOrigins = null;

	public List<String> getAllowedOrigins() {
		return allowedOrigins;
	}

	public OriginFilter(Context context, String allowedOrigins) {
		super(context);
		String[] origins = allowedOrigins == null ? new String[] {} : allowedOrigins.split(" ");
		this.allowedOrigins = new ArrayList<String>();
		for (String origin : origins)
			this.allowedOrigins.add(origin.trim());
	}

	public OriginFilter(Context context, List<String> allowedOrigins) {
		super(context);
		this.allowedOrigins = allowedOrigins;
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		if (Method.OPTIONS.equals(request.getMethod())) {
			Series requestHeaders = (Series) request.getAttributes().get("org.restlet.http.headers");
			String origin = requestHeaders.getFirstValue("Origin", true);
			boolean all = getAllowedOrigins().contains("*");
			if (all || getAllowedOrigins().contains(origin)) {
				response.setAccessControlAllowOrigin(origin != null ? origin : (all ? "*" : origin));
				Set set = new TreeSet<Method>();
				//WARNING: Addition of the standard header "Access-Control-Allow-Origin" is not allowed. Please use the equivalent property in the Restlet API.
				set.add(Method.GET);
				set.add(Method.POST);
				set.add(Method.PUT);
				set.add(Method.DELETE);
				set.add(Method.OPTIONS);
				response.setAccessControlAllowMethods(set);
				set = new TreeSet<String>();
				set.add("Origin");
				set.add("X-Requested-With");
				set.add("Content-Type");
				set.add("Accept");
				response.setAccessControlAllowHeaders(set);
				response.setAccessControlAllowCredentials(true);
				response.setAccessControlMaxAge(60);
				response.setEntity(new EmptyRepresentation());
				return SKIP;
			}
		}

		return super.beforeHandle(request, response);
	}

	@Override
	protected void afterHandle(Request request, Response response) {
		if (!Method.OPTIONS.equals(request.getMethod())) {
			Series requestHeaders = (Series) request.getAttributes().get("org.restlet.http.headers");
			String origin = requestHeaders.getFirstValue("Origin", true);
			boolean all = getAllowedOrigins().contains("*");
			if (all || getAllowedOrigins().contains(origin)) {
				
				//responseHeaders.add("Access-Control-Allow-Origin", origin != null ? origin : (all ? "*" : origin));
				response.setAccessControlAllowOrigin(origin != null ? origin : (all ? "*" : origin));
				Set set = new TreeSet<Method>();
				set.add(Method.GET);
				set.add(Method.POST);
				set.add(Method.PUT);
				set.add(Method.DELETE);
				set.add(Method.OPTIONS);
				response.setAccessControlAllowMethods(set);				
				//responseHeaders.add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
				//responseHeaders.add("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
				set = new TreeSet<String>();
				set.add("Origin");
				set.add("X-Requested-With");
				set.add("Content-Type");
				set.add("Accept");
				response.setAccessControlAllowHeaders(set);
				response.setAccessControlAllowCredentials(true);
				response.setAccessControlMaxAge(60);
			}
		}
		super.afterHandle(request, response);
	}
}
