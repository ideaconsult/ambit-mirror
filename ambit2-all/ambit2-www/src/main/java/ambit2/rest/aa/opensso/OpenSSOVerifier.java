package ambit2.rest.aa.opensso;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.Verifier;

/**
 * 1) looks for query parameter ?tokenid=<token><br>
 * 2)If not available, looks if tokenid=<token> is available in the headers<br>
 * 3)If token is not available neither as query param, nor as headers, looks for ?username=<username>&password=<password><br> 
 * 4)If credentials available, requests a token via OpenSSO authn service <br>
 * 5)If token is <br>
 * @author nina
 *
 */
public class OpenSSOVerifier implements Verifier {

	public int verify(Request request, Response response) {
			String token = OpenSSOToken.getToken(request);
			if (token == null) {
				token = OpenSSOToken.getTokenByUserPass(request);
				if (token != null) {
					OpenSSOToken.addTokenToHeaders(token, request);
					return Verifier.RESULT_VALID;
				} else return Verifier.RESULT_MISSING; 
			} else 
				return OpenSSOToken.isValid(token)?Verifier.RESULT_VALID:Verifier.RESULT_INVALID;
	}

}
