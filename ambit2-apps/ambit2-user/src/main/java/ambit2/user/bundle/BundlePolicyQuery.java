package ambit2.user.bundle;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.restnet.db.aalocal.policy.PolicyQuery;

/**
 * Used by {@link BundlePolicyAuthorizer}
 * @author nina
 *
 */
public class BundlePolicyQuery extends PolicyQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2537056642893303624L;
	//if searching by bundle_number
	private static final String sql_byhexnumber = "select prefix,resource,sum(m%s) from %spolicy_bundle join %suser_roles using(role_name) where user_name=? and prefix=? and resource=unhex(?) and m%s=1 group by prefix,resource\n";
	//if searching by bundle id
	private static final String sql_byintid = "select prefix,hex(resource),sum(m%s) from %spolicy_bundle join %suser_roles using(role_name) where user_name=? and prefix=? and m%s=1 and resource in (select bundle_number from %s.bundle where idbundle=?) group by prefix,resource";
	protected String ambitdbname;
	
	public BundlePolicyQuery(String ambitdbname) {
		super();
		this.ambitdbname = ambitdbname;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		// username
		params.add(new QueryParam<String>(String.class, getValue()));
		try {
			// parse the uri, we expect /bundle/{integer}
			String[] uri = getFieldname().splitURI(getFieldname().getUri());
			params.add(new QueryParam<String>(String.class, uri[0]));

			try {
				uri[1] = uri[1].replace("/bundle/", "").replace("-", "");
				int id = Integer.parseInt(uri[1]);
				params.add(new QueryParam<Integer>(Integer.class, id));
			} catch (Exception x) {
				throw new AmbitException("Invalid bundle id");
			}
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		String dbname = databaseName == null ? "" : String.format("`%s`.",
				databaseName);
		return String.format(sql_byintid, getMethod().getName().toLowerCase(),
				dbname, dbname, getMethod().getName().toLowerCase(),ambitdbname);
	}
}
