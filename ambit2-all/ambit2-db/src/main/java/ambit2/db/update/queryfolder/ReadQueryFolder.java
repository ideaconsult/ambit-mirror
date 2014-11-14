package ambit2.db.update.queryfolder;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.AmbitUser;
import ambit2.db.SessionID;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.StringCondition;

/**
 * Retrieve query folder header by id or name
 * @author nina
 *
 */
public class ReadQueryFolder  extends AbstractQuery<AmbitUser, SessionID, StringCondition, SessionID>  implements IQueryRetrieval<SessionID> {
	protected static String sql = "select idsessions,title from sessions %s";
	protected static String whereID = "idsessions = ?";
	protected static String whereName = "title %s ?";
	protected static String whereUser = "user_name = ?";
	/**
	 * 
	 */
	private static final long serialVersionUID = -8595708937489941431L;

	public ReadQueryFolder(AmbitUser user, SessionID id) {
		super();
		setValue(id);
		setFieldname(user);
		setCondition(StringCondition.getInstance("like"));
	}
	public ReadQueryFolder() {
		this(null,null);
	}
		
	public double calculateMetric(SessionID object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> param = new ArrayList<QueryParam>();
		
		if ((getValue()!=null) && (getValue().getId()>0)) {
			param.add(new QueryParam<Integer>(Integer.class,getValue().getId()));
		}
		if ((getValue()!=null) && (getValue().getName()!=null)) {
			param.add(new QueryParam<String>(String.class,getValue().getName()));
		}
		if ((getFieldname()!=null) && (getFieldname().getName()!=null)) {
			param.add(new QueryParam<String>(String.class,getFieldname().getName()));
		} 
		return param;
	}

	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		String w = "where\n";
		if ((getValue()!=null) && (getValue().getId()>0)) {
			b.append(w);
			b.append(whereID);
			w = " and ";
		}
		if ((getValue()!=null) && (getValue().getName()!=null)) {
			b.append(w);
			b.append(String.format(whereName,getCondition()));
			w = " and ";
		}
		b.append(w);
		if ((getFieldname()!=null) && (getFieldname().getName()!=null)) 		
			b.append(whereUser);
		else
			b.append("user_name = SUBSTRING_INDEX(user(),'@',1)");
			
		return String.format(sql,getValue()==null?"":b.toString());
	}
	
	public SessionID getObject(ResultSet rs) throws AmbitException {
		try {
			SessionID le =  new SessionID(rs.getInt(1));
			le.setName(rs.getString(2));

			return le;
		} catch (Exception x) {
			return null;
		}
	}
	@Override
	public String toString() {
		return getValue()==null?"QueryFolder":String.format("QueryFolder %s",getValue().getName());
	}
}