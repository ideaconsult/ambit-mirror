package ambit2.db.update.bundle.matrix;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.study.ValueAnnotated;
import ambit2.base.data.substance.SubstanceEndpointsBundle;

/**
 * Mark a value in the buncle_substance_experiments table (values copied from
 * substance_experiment) as deleted (along with a comment why)
 * 
 * @author nina
 * 
 */
public class DeleteMatrixValue extends AbstractUpdate<SubstanceEndpointsBundle, ValueAnnotated> {

    private final static String[] sql = new String[] { "update bundle_substance_experiment set deleted=? ,remarks=? where idbundle=? and idresult=?" };

    public DeleteMatrixValue() {
	this(null,null);
    }
    public DeleteMatrixValue(SubstanceEndpointsBundle bundle,ValueAnnotated value) {
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
	if (getGroup() == null || getGroup().getID() <= 0 || getObject() == null || getObject().getIdresult() <= 0)
	    throw new AmbitException("Value not defined");
	List<QueryParam> params = new ArrayList<QueryParam>();
	params.add(new QueryParam<Boolean>(Boolean.class, getObject().isDeleted()));
	params.add(new QueryParam<String>(String.class, getObject().getRemark()));
	params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
	params.add(new QueryParam<Integer>(Integer.class, getObject().getIdresult()));
	return params;
    }

    @Override
    public void setID(int index, int id) {
    }

}
