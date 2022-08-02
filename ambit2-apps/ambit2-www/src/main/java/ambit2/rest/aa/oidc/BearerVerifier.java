package ambit2.rest.aa.oidc;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.security.User;
import org.restlet.security.Verifier;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import net.idea.restnet.db.aalocal.DBRole;

public class BearerVerifier implements Verifier {
	private JWTVerifier jwtverifier;

	public BearerVerifier(JWTVerifier jwtverifier) {
		this.jwtverifier = jwtverifier;
	}

	@Override
	public int verify(Request request, Response response) {

		int result = RESULT_VALID;

		if (request.getChallengeResponse() == null) {
			result = RESULT_MISSING;
		} else {
			try {
				ChallengeResponse authz = request.getChallengeResponse();
				System.out.println(authz.getRawValue());
				String token = authz.getRawValue().replaceAll("Bearer ", "");
				DecodedJWT jwt = jwtverifier.verify(token);
				request.getClientInfo()
						.setUser(new User(jwt.getClaim("preferred_username").asString(), "Bearer",
								jwt.getClaim("given_name").asString(), jwt.getClaim("family_name").asString(),
								jwt.getClaim("email").asString()));
				for (String role : jwt.getClaim("roles").asArray(String.class)) {
					request.getClientInfo().getRoles().add(new DBRole(role, role));
				}

			} catch (Exception x) {
				result = RESULT_INVALID;
				request.getClientInfo().setUser(new User(null, "Bearer"));
			}
		}

		return result;
	}

}
