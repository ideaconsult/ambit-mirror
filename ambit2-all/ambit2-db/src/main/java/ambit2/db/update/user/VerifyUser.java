package ambit2.db.update.user;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

/**
 * Verifies if username {link {@link #getFieldname()} and password {link {@link #getValue()}} exist.
 * @author nina
 *
 */
public class VerifyUser extends AbstractQuery<String, String, EQCondition, Boolean> implements IQueryRetrieval<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6017803463536586392L;

	public double calculateMetric(Boolean object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		
		if ((getValue() == null) || (getFieldname() == null)) throw new AmbitException("Empty parameters");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getFieldname()));
		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}

	public String getSQL() throws AmbitException {
		return "select user_name from users where user_name = ? and password = md5(?)";
	}
	/**
	 * If found, will return true always. 
	 */
	public Boolean getObject(ResultSet rs) throws AmbitException {
		return rs!=null;
	}

}
