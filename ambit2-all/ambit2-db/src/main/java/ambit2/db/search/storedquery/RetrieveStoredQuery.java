package ambit2.db.search.storedquery;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SessionID;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.StringCondition;

public class RetrieveStoredQuery extends  AbstractQuery<SessionID, IStoredQuery, EQCondition, IStoredQuery> 
									implements IQueryRetrieval<IStoredQuery>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1485698692883271843L;
	public final static String sqlField = "select idquery,idsessions,name,content from query join sessions using(idsessions)\nwhere user_name=SUBSTRING_INDEX(user(),'@',1) %s %s";
	public static final String where_query = "and idquery=?";
	public static final String where_session = "and idsessions %s ?";	
	
	public RetrieveStoredQuery() {
		setCondition(EQCondition.getInstance());
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()!=null)
			params.add(new QueryParam<Integer>(Integer.class, getFieldname().getId()));
		if (getValue()!=null)
			params.add(new QueryParam<Integer>(Integer.class, getValue().getId()));
		if (params.size()==0) throw new AmbitException("No query or name defined!");
		else return params;
	}

	public String getSQL() throws AmbitException {
		String a1 = (getFieldname()==null)?"":String.format(where_session,getCondition().getSQL());
		String a2 = (getValue()==null)?"":where_query;

		return String.format(sqlField,a1,a2);
	}

	public IStoredQuery getObject(ResultSet rs) throws AmbitException {
		try {
			StoredQuery q = new StoredQuery();
			q.setId(rs.getInt(1));
			q.setName(rs.getString(3));
			return q;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	
	public double calculateMetric(IStoredQuery object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}

}
