package ambit2.rest.aa.oidc;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.Verifier;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.io.ByteStreams;

public class ChallengeAuthenticatorBearer extends ChallengeAuthenticator {
	 public static final ChallengeScheme HTTP_BEARER = new ChallengeScheme(
	            "HTTP_BEARER", "Bearer", "Bearer HTTP authentication");
	 
	public ChallengeAuthenticatorBearer(Context context, boolean optional, 
			String realm, String keyid) throws Exception {
		super(context, optional, HTTP_BEARER, realm);
		
		Map<String, Object> discoveryPayload = ChallengeAuthenticatorBearer.getConfig(realm);
		setVerifier(new BearerVerifier(createJWTVerifier(discoveryPayload, keyid)));

		//setEnroler(new BearerEnroller(context, realm));
		setRechallenging(false);
	}
	public static Map<String, Object> getConfig(String domain) throws Exception {
		URL url = new URI(domain + "/.well-known/openid-configuration").normalize().toURL();

		URLConnection connection = url.openConnection();
		connection.setRequestProperty("Accept", "application/json");
		ObjectReader reader = new ObjectMapper().readerFor(Map.class);

		try (InputStream inputStream = connection.getInputStream()) {
			return reader.readValue(ByteStreams.toByteArray(inputStream));
			
		} catch (Exception x) {
			throw x;
		}

	}	
	protected JWTVerifier createJWTVerifier(Map<String, Object> discoveryPayload, String key_id) throws Exception {

		UrlJwkProvider p = new UrlJwkProvider(
				new URL(discoveryPayload.get("jwks_uri").toString()));
		Jwk jwk = p.get(key_id);
		Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
		return JWT.require(algorithm) 
				.withIssuer(discoveryPayload.get("issuer").toString()).withClaim("typ", "Bearer").build();
	}
	
	@Override
	protected int unauthenticated(Request request, Response response) {
		
		if (isOptional()) {
			//if we got here because of wrong token, stop
			if (request.getClientInfo() != null && request.getClientInfo().getUser() != null
					&& request.getClientInfo().getUser().getIdentifier() == null && "Bearer"
							.equals(String.valueOf(request.getClientInfo().getUser().getSecret()))) {
				//that's it
				//getLogger().fine(request.getClientInfo().getUser().toString());
			} else
				return CONTINUE;
		}

		// Update the challenge response accordingly
		if (request.getChallengeResponse() != null) {
			request.getChallengeResponse().setAuthenticated(false);
		}

		// Update the client info accordingly
		if (request.getClientInfo() != null) {
			request.getClientInfo().setAuthenticated(false);
		}

		// Stop the filtering chain
		return STOP;
	}

	/**
	 * Missing Bearer token will be handled by the next authenticator, otherwise stop
	 * 
	 * @param verifier_result
	 * @return
	 */
	protected boolean isOptional(int verifier_result) {
		return isOptional() && (Verifier.RESULT_MISSING == verifier_result);
	}

	@Override
	protected boolean authenticate(Request request, Response response) {
		boolean result = false;
		final boolean loggable = getLogger().isLoggable(Level.FINE);

		if (getVerifier() != null) {
			switch (getVerifier().verify(request, response)) {
			case Verifier.RESULT_VALID:
				// Valid credentials provided
				result = true;

				if (loggable) {
					ChallengeResponse challengeResponse = request.getChallengeResponse();

					if (challengeResponse != null) {
						getLogger().fine("Authentication succeeded. Valid credentials provided for identifier: "
								+ request.getChallengeResponse().getIdentifier() + ".");
					} else {
						getLogger().fine("Authentication succeeded. Valid credentials provided.");
					}
				}
				//this is to avoid messing up with the buggy auth filters down the chain
				request.setChallengeResponse(null);
				break;
			case Verifier.RESULT_MISSING:
				// No credentials provided
				if (loggable) {
					getLogger().fine("Authentication failed. No token provided.");
				}

				if (!isOptional(Verifier.RESULT_MISSING)) {
					challenge(response, false);
				}
				break;
			case Verifier.RESULT_INVALID:
				// Invalid credentials provided
				if (loggable) {
					getLogger().fine("Authentication failed. Invalid token provided.");
				}

				if (!isOptional(Verifier.RESULT_INVALID)) {
					if (isRechallenging()) {
						challenge(response, false);
					} else {
						forbid(response);
					}
				}
				break;
			case Verifier.RESULT_STALE:
				if (loggable) {
					getLogger().fine("Authentication failed. Stale credentials provided.");
				}

				if (!isOptional(Verifier.RESULT_STALE)) {
					challenge(response, true);
				}
				break;
			}
		} else {
			getLogger().warning("Authentication failed. No verifier provided.");
		}

		return result;
	}
	

}
