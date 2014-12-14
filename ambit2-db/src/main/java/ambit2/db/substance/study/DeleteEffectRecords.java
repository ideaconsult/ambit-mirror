package ambit2.db.substance.study;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.I5Utils;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;

/**
 * deletes effects records given document uuid
 * @author nina
 *
 */
public class DeleteEffectRecords extends AbstractUpdate<ProtocolApplication<Protocol, Params,String,Params,String>,EffectRecord> {

	public static final String[] delete_sql = {"delete from substance_experiment where document_prefix =? and document_uuid =unhex(?)"};

	public DeleteEffectRecords(ProtocolApplication<Protocol, Params,String,Params,String> papp) {
		super(null);
		setGroup(papp);
	}
	public DeleteEffectRecords() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getGroup()!=null && getGroup().getDocumentUUID()!=null)  {
			String[] uuid = new String[]{null,getGroup().getDocumentUUID()};
			uuid = I5Utils.splitI5UUID(getGroup().getDocumentUUID());
			params.add(new QueryParam<String>(String.class, uuid[0]));
			params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
		} else
			throw new AmbitException("no document uuid specified");
		return params.size()==0?null:params;		
	}

	public String[] getSQL() throws AmbitException {
		if (getGroup()!=null)  {
			return delete_sql;
		}
		throw new AmbitException("no document uuid specified");
	}
	public void setID(int index, int id) {
			
	}
}