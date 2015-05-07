package ambit2.user.policy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.update.CallableDBUpdateTask;
import net.idea.restnet.i.aa.RESTPolicy;
import net.idea.restnet.user.DBUser;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Bundle policy creator http://sourceforge.net/p/ambit/feature-requests/86/
 * 
 * @author nina
 * 
 */
public class CallablePolicyUsersCreator extends CallableDBUpdateTask<RESTPolicyUsers, Form, String> {
    protected CreateUsersPolicy updateQuery;
    protected String usersdbname;
    protected String defaultRole = "ambit_user";

    public String getDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
    }


    public CallablePolicyUsersCreator(Method method, Form input, String baseReference, Connection connection,
	    String token, String usersdbname) {
	super(method, input, baseReference, connection, token);
	this.usersdbname = usersdbname;
    }

    @Override
    protected RESTPolicyUsers getTarget(Form input) throws Exception {
	if (Method.POST.equals(method)) {
	    String bundle_number = input.getFirstValue("bundle_number");
	    try {
		UUID.fromString(bundle_number);
	    } catch (Exception x) {
		if (bundle_number == null)
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	    }
	    String[] u = input.getValuesArray("canRead");
	    updateQuery = createPolicyQuery(bundle_number, u, true);
	    if (updateQuery == null) {
		u = input.getValuesArray("canWrite");
		updateQuery = createPolicyQuery(bundle_number, u, false);
	    }
	    if (updateQuery != null && updateQuery.getObject() != null)
		return (RESTPolicyUsers) updateQuery.getObject();
	    else
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    protected CreateUsersPolicy createPolicyQuery(String bundle_number, String[] ug, boolean readonly) {
	if (ug == null || ug.length == 0 || bundle_number == null)
	    return null;
	String role = "B." + bundle_number.replace("-", "").toUpperCase() + "." + (readonly ? "R" : "W");
	String resource = baseReference + "/bundle/" + bundle_number.toUpperCase();
	List<DBUser> users = new ArrayList<DBUser>();
	RESTPolicy existingrole = null;
	for (String s : ug) {
	    // group
	    if (s.startsWith("g_")) {
		existingrole = new RESTPolicy();
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
	    existingrole = new RESTPolicy();
	    existingrole.setRole(getDefaultRole());
	    existingrole.setAllowGET(false);
	    existingrole.setAllowPOST(false);
	    existingrole.setAllowPUT(false);
	    existingrole.setAllowDELETE(false);
	    existingrole.setUri(resource);
	}
	RESTPolicyUsers policy = new RESTPolicyUsers(users);
	policy.setRole(role);
	policy.setAllowGET(readonly);
	policy.setAllowPOST(!readonly);
	policy.setAllowPUT(!readonly);
	policy.setAllowDELETE(false);
	policy.setUri(resource);
	CreateUsersPolicy q = new CreateUsersPolicy(existingrole, policy);
	q.setDatabaseName(usersdbname);
	return q;
    }

    @Override
    protected IQueryUpdate<? extends Object, RESTPolicyUsers> createUpdate(RESTPolicyUsers target) throws Exception {
	if (Method.POST.equals(method)) {
	    return updateQuery;
	}
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected String getURI(RESTPolicyUsers target) throws Exception {
	return target.getPolicyURI(updateQuery.getObject().getUri());
    }

}
