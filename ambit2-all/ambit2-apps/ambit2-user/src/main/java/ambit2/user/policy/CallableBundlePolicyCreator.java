package ambit2.user.policy;

import java.sql.Connection;

import org.restlet.data.Form;
import org.restlet.data.Method;

public class CallableBundlePolicyCreator extends CallablePolicyUsersCreator {

	public CallableBundlePolicyCreator(Method method, Form input,
			String baseReference, Connection connection, String token,
			String usersdbname) {
		super(method, input, baseReference, connection, token, usersdbname);
	}

}
