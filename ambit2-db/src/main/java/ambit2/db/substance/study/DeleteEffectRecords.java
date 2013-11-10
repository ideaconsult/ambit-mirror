package ambit2.db.substance.study;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.I5Utils;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

/**
 * deletes effects records given document uuid
 * @author nina
 *
 */
public class DeleteEffectRecords extends AbstractUpdate<String,EffectRecord> {

	public static final String[] delete_sql = {"delete from substance_experiment where document_prefix =? and hex(document_uuid) =?"};

	public DeleteEffectRecords(String documentuuid) {
		super(null);
		setGroup(documentuuid);
	}
	public DeleteEffectRecords() {
		this(null);
	}		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getGroup()!=null)  {
			String[] uuid = new String[]{null,getGroup()};
			uuid = I5Utils.splitI5UUID(getGroup());
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