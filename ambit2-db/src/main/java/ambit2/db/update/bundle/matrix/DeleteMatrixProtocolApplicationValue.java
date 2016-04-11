package ambit2.db.update.bundle.matrix;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.I5Utils;
import ambit2.base.data.study.ValueAnnotated;
import ambit2.base.data.substance.SubstanceEndpointsBundle;

/**
 * Mark a value in the buncle_substance_experiments table (values copied from
 * substance_experiment) as deleted (along with a comment why)
 * 
 * @author nina
 * 
 */
public class DeleteMatrixProtocolApplicationValue<VALUE> extends
		AbstractUpdate<SubstanceEndpointsBundle, ValueAnnotated<VALUE, String>> {

	private final static String[] sql = new String[] { "update bundle_substance_protocolapplication set deleted=? ,remarks=? where document_prefix=? and document_uuid=unhex(?)" };

	public DeleteMatrixProtocolApplicationValue() {
		this(null, null);
	}

	public DeleteMatrixProtocolApplicationValue(
			SubstanceEndpointsBundle bundle, ValueAnnotated<VALUE, String> value) {
		super();
		setGroup(bundle);
		setObject(value);
	}

	@Override
	public String[] getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getGroup() == null || getGroup().getID() <= 0
				|| getObject() == null || getObject().getIdresult() == null)
			throw new AmbitException("Value not defined");
		String[] doc_uuid = I5Utils.splitI5UUID(getObject().getIdresult());

		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Boolean>(Boolean.class, getObject()
				.isDeleted()));
		params.add(new QueryParam<String>(String.class, truncate(getObject().getRemark(),45)));
		params.add(new QueryParam<String>(String.class, doc_uuid[0]));
		params.add(new QueryParam<String>(String.class, doc_uuid[1].replace("-", "")));
		return params;
	}

	@Override
	public void setID(int index, int id) {
	}

}
