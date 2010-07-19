package ambit2.rest.aa.opensso;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.Authorizer;

public class OpenSSOAuthorizer extends Authorizer {

	@Override
	protected boolean authorize(Request request, Response response) {
		String token = OpenSSOToken.getToken(request);
		
		return OpenSSOToken.verifyWithSSOServer(request.getResourceRef().toString(),request.getMethod(),token);

	}

}
