package ambit2.user.bundle;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.restnet.db.aalocal.policy.PolicyQuery;

public class BundleOwnerPolicy extends PolicyQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 761328793726648871L;
	// if searching by bundle id
	private static final String sql_byintid = "select ?,hex(bundle_number),1 from %s.bundle where idbundle=? and user_name=?";
	protected String ambitdbname;

	public BundleOwnerPolicy(String ambitdbname) {
		super();
		this.ambitdbname = ambitdbname;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
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
		// username
		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		return String.format(sql_byintid, ambitdbname);
	}
}
