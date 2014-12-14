package ambit2.db.substance.study;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.I5Utils;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;

/**
 * 
 * @author nina
 *
 */
public class DeleteStudy extends AbstractUpdate<String,ProtocolApplication<Protocol, Params, String, Params, String>> {
	public static final String[] delete_sql = {"delete from substance_protocolapplication where substance_prefix =? and substance_uuid =unhex(?)"};
	
	public DeleteStudy() {
		super();
	}
	@Override
	public String[] getSQL() throws AmbitException {
		return delete_sql;
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getGroup()==null) throw new AmbitException("Empty substance id");
		String[] uuid = new String[]{null,getGroup()};
		uuid = I5Utils.splitI5UUID(getGroup());
		params.add(new QueryParam<String>(String.class, uuid[0]));
		params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
		return params;
	}

	@Override
	public void setID(int index, int id) {
	} 

}
