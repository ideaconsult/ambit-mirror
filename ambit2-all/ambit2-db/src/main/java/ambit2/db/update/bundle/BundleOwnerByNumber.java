package ambit2.db.update.bundle;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;

/**
 * Query to check bundle owner by bundle number
 * 
 * @author nina
 * 
 */
public class BundleOwnerByNumber extends
		AbstractQuery<String, String, EQCondition, Integer> implements
		IQueryRetrieval<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4045644505492454127L;

	private static final String sql_bynumber = "select idbundle,hex(bundle_number) from bundle where bundle_number=unhex(?) and user_name=?";

	public BundleOwnerByNumber() {
		super();
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		try {
			params.add(new QueryParam<String>(String.class, getFieldname().replace("-", "")));

		} catch (Exception x) {
			throw new AmbitException(x);
		}
		// username
		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		return String.format(sql_bynumber);
	}

	@Override
	public double calculateMetric(Integer arg0) {
		return 1;
	}

	@Override
	public Integer getObject(ResultSet rs) throws AmbitException {
		try {
			return rs.getInt(1);
		} catch (Exception x) {
			return -1;
		}
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}
}
