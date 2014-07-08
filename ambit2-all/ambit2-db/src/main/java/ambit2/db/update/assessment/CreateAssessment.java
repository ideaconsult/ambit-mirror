package ambit2.db.update.assessment;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.AmbitUser;
import ambit2.db.SessionID;
import ambit2.db.update.AbstractUpdate;

/**
 * TODO Session class to be renamed to Assessment
 * @author nina
 *
 */
public class CreateAssessment extends AbstractUpdate<AmbitUser,SessionID> {
	public static final String sql = "insert into sessions (idsessions,user_name,title,completed) values (?,?,?,current_timestamp) on duplicate key update completed=current_timestamp";
	public static final String sql_current_user = "insert into sessions (idsessions,user_name,title,completed) values (?,SUBSTRING_INDEX(user(),'@',1),?,current_timestamp)  on duplicate key update completed=current_timestamp";
	
	public CreateAssessment(AmbitUser user,SessionID id) {
		super();
		setGroup(user);
		setObject(id);
	}	
	public CreateAssessment(AmbitUser user) {
		this(user,null);
	}
	public CreateAssessment() {
		this(null);
	}	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> param = new ArrayList<QueryParam>();
		if ((getObject()==null) || (getObject().getId()==null) || (getObject().getId()<=0))
			param.add(new QueryParam<Integer>(Integer.class,null));
		else
			param.add(new QueryParam<Integer>(Integer.class,getObject().getId()));

		if (getGroup()!=null)
			param.add(new QueryParam<String>(String.class,getGroup().getName()));
		
		if (getObject()==null) setObject(new SessionID());
		if (getObject().getName()==null) getObject().setName("Default");
		param.add(new QueryParam<String>(String.class,(getObject()==null) || (getObject().getName()==null)?"Default":getObject().getName()));
		return param;
	}

	public String[] getSQL() throws AmbitException {
		return new String[] {getGroup()==null?sql_current_user:sql};
	}
	@Override
	public boolean returnKeys(int index) {
		return false;
	}

	public void setID(int index, int id) {
		if (getObject()==null) setObject(new SessionID(id));
		else getObject().setId(id);
	}
	


}
