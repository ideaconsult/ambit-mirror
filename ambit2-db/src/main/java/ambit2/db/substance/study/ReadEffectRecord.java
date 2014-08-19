package ambit2.db.substance.study;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.I5Utils;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.ProtocolApplication;

/**
 * Reads effects records given document uuid
 * @author nina
 *
 */
public class ReadEffectRecord extends ReadEffectRecordAbstract<ProtocolApplication,EffectRecord<String, String, String>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5430387137460722198L;

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()==null || getFieldname().getDocumentUUID()==null) throw new AmbitException("Empty document id");
		String[] uuid = new String[]{null,getFieldname().getDocumentUUID()};
		uuid = I5Utils.splitI5UUID(getFieldname().getDocumentUUID());
		params.add(new QueryParam<String>(String.class, uuid[0]));
		params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
		return params;
	}

	

}
