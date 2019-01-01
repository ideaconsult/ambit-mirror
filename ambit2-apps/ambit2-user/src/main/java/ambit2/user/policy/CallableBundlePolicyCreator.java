package ambit2.user.policy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.i.aa.RESTPolicy;
import net.idea.restnet.user.DBUser;

import org.restlet.data.Form;
import org.restlet.data.Method;

public class CallableBundlePolicyCreator extends CallablePolicyUsersCreator {
	public CallableBundlePolicyCreator(Method method, Form input,
			String baseReference, Connection connection, String token,
			String usersdbname,int HOMEPAGE_DEPTH) {
		super(method, input, baseReference, connection, token, usersdbname,HOMEPAGE_DEPTH);
		
	}

	@Override
	protected AbstractUpdate<RESTPolicy, RESTPolicyUsers> createPolicyQuery(
			String bundle_number, String[] ug, boolean readonly) {
		if (ug == null || ug.length == 0 || bundle_number == null)
			return null;
		String role = "B." + bundle_number.replace("-", "").toUpperCase() + "."
				+ (readonly ? "R" : "W");
		String resource = baseReference + "/bundle/"
				+ bundle_number.toUpperCase();
		List<DBUser> users = new ArrayList<DBUser>();
		RESTPolicy existingrole = null;
		for (String s : ug) {
			// group
			if (s.startsWith("g_")) {
				existingrole = new AmbitRESTPolicy(HOMEPAGE_DEPTH);
				existingrole.setRole(s.substring(2));
				existingrole.setAllowGET(readonly);
				existingrole.setAllowPOST(!readonly);
				existingrole.setAllowPUT(!readonly);
				// will allow delete to the owner only
				existingrole.setAllowDELETE(false);
				existingrole.setUri(resource);
			} else {
				DBUser user = new DBUser();
				user.setUserName(s.trim());
				users.add(user);
			}
		}
		if (existingrole == null) {
			// a way to remove the "all" policy
			existingrole = new AmbitRESTPolicy(HOMEPAGE_DEPTH);
			existingrole.setRole(getDefaultRole());
			existingrole.setAllowGET(false);
			existingrole.setAllowPOST(false);
			existingrole.setAllowPUT(false);
			existingrole.setAllowDELETE(false);
			existingrole.setUri(resource);
		}
		RESTPolicyUsers policy = new RESTPolicyUsers(HOMEPAGE_DEPTH,users);
		policy.setRole(role);
		policy.setAllowGET(readonly);
		policy.setAllowPOST(!readonly);
		policy.setAllowPUT(!readonly);
		policy.setAllowDELETE(false);
		policy.setUri(resource);
		CreateUsersBundlePolicy q = new CreateUsersBundlePolicy(existingrole, policy);
		q.setDatabaseName(usersdbname);
		return q;
	}
}
