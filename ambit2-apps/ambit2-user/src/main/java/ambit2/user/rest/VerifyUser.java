package ambit2.user.rest;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;

/**
 * Verifies if username {link {@link #getFieldname()} exist.
 * @author nina
 *
 */
public class VerifyUser extends AbstractQuery<Integer, String, EQCondition, Boolean> implements IQueryRetrieval<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6017803463536586392L;
	protected String usertable = "user";
	
	public String getUsertable() {
		return usertable;
	}

	public void setUsertable(String usertable) {
		this.usertable = usertable;
	}
	public double calculateMetric(Boolean object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		
		if ((getValue() == null) || (getFieldname() == null)) throw new AmbitException("Empty parameters");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getFieldname()));
		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}

	public String getSQL() throws AmbitException {
		return String.format("select iduser from %s where iduser=? and username = ?",getUsertable());
	}
	/**
	 * If found, will return true always. 
	 */
	public Boolean getObject(ResultSet rs) throws AmbitException {
		return rs!=null;
	}

}
