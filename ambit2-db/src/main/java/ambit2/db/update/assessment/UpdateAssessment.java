package ambit2.db.update.assessment;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.AmbitUser;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.SessionID;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

public class UpdateAssessment extends AbstractUpdate<AmbitUser,SessionID> {
	public static final String sql = "update sessions set title=? where idsessions=? and user_name=?" ;
	public static final String sql_current_user = "update sessions set title=? where idsessions=? and user_name=(SUBSTRING_INDEX(user(),'@',1))";
	
	public UpdateAssessment(AmbitUser user,SessionID id) {
		super();
		setGroup(user);
		setObject(id);
	}
	public UpdateAssessment() {
		this(null,null);
	}	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> param = new ArrayList<QueryParam>();
		if (getObject()==null) throw new AmbitException("Empty ID");
		param.add(new QueryParam<String>(String.class,getObject().getName()));
		param.add(new QueryParam<Integer>(Integer.class,getObject().getId()));
		if (getGroup()!=null)
			param.add(new QueryParam<String>(String.class,getGroup().getName()));
		
		return param;
	}

	public String[] getSQL() throws AmbitException {
		return new String[] {getGroup()==null?sql_current_user:sql};
	}

	public void setID(int index, int id) {
		getObject().setId(id);
	}
	


}
