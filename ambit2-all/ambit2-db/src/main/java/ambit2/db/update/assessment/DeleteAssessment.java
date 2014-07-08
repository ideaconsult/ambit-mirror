package ambit2.db.update.assessment;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.AmbitUser;
import ambit2.db.SessionID;
import ambit2.db.update.AbstractUpdate;

/**
 * Delete an assessment
 * @author nina
 *
 */
public class DeleteAssessment extends AbstractUpdate<AmbitUser, SessionID> {
	public static final String sql = "delete from sessions where user_name=? and idsessions=?";
	public static final String sql_current_user = "delete from sessions where user_name=(SUBSTRING_INDEX(user(),'@',1)) and idsessions=?";
	
	public DeleteAssessment(AmbitUser user, SessionID id) {
		super();
		setGroup(user);
		setObject(id);
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> param = new ArrayList<QueryParam>();
		if (getGroup()!=null)
			param.add(new QueryParam<String>(String.class,getGroup().getName()));
		if (getObject()!=null)
			param.add(new QueryParam<Integer>(Integer.class,getObject().getId()));
		else throw new AmbitException("Empty ID");
		return param;
	}

	public String[] getSQL() throws AmbitException {
		return new String[] {getGroup()==null?sql_current_user:sql};
	}

	public void setID(int index, int id) {

	}

}
