package ambit2.rest.aa.opensso;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;

/**
 * Allows GET to any logged in user; POST/PUT/DELETE as allowed by the parent class
 * @author nina
 *
 */
public class OpenSSOMethodAuthorizer extends OpenSSOAuthorizer {

	@Override
	protected boolean authorize(Request request, Response response) {
		if (request.getClientInfo().getUser()!=null) {
			if (Method.GET.equals(request.getMethod())) return true;
			else return super.authorize(request, response);
		} else return false;
	}
}
