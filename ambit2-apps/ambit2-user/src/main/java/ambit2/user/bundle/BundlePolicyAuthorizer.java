package ambit2.user.bundle;

import java.sql.ResultSet;
import java.util.List;

import net.idea.modbcum.p.QueryExecutor;
import net.idea.restnet.db.aalocal.policy.PolicyAuthorizer;
import net.idea.restnet.db.aalocal.policy.PolicyQuery;
import net.idea.restnet.i.aa.RESTPolicy;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;

import ambit2.user.policy.AmbitRESTPolicy;

public class BundlePolicyAuthorizer extends PolicyAuthorizer<BundlePolicyQuery> {
	private BundleOwnerPolicy ownerPolicy;
	protected int HOMEPAGE_DEPTH = 1;

	public BundlePolicyAuthorizer(Context context, String configfile,
			String dbName, String usersdbname, int HOMEPAGE_DEPTH ) throws Exception {
		super(context, configfile, dbName, usersdbname);
		this.HOMEPAGE_DEPTH = HOMEPAGE_DEPTH;
		if (dbName == null)
			throw new Exception("Database not defined!");
		ownerPolicy = new BundleOwnerPolicy(dbName);
	}

	@Override
	public boolean authorizeSpecialCases(Request request, Response response,
			List<String> uri) {

		int depth = request.getResourceRef().getSegments().size();
		if (depth > (HOMEPAGE_DEPTH + 1))
			depth = HOMEPAGE_DEPTH + 2;
		StringBuilder resource = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			String segment = request.getResourceRef().getSegments().get(i);
			resource.append("/");
			resource.append(segment);
			if (i == (HOMEPAGE_DEPTH + 1)) {
				uri.add(resource.toString());
			}
		}
		return false;
	}

	@Override
	protected RESTPolicy initRESTPolicy() {
		return new AmbitRESTPolicy(HOMEPAGE_DEPTH);
	}

	@Override
	protected BundlePolicyQuery createPolicyQuery(String datadbname) {
		return new BundlePolicyQuery(datadbname);
	}

	@Override
	public boolean isOwner(String user, QueryExecutor<PolicyQuery> executor,
			RESTPolicy policy) {
		ResultSet rs = null;
		try {
			ownerPolicy.setFieldname(policy);
			ownerPolicy.setValue(user);

			rs = executor.process(ownerPolicy);
			int found = 0;
			while (rs.next()) {
				found++;
			}
			return (found > 0);
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {
				executor.closeResults(rs);
				rs = null;
			} catch (Exception x) {
			}
		}
		return false;
	}
}
