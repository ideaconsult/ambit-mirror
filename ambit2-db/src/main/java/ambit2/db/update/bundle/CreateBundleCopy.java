package ambit2.db.update.bundle;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IStoredProcStatement;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;
import ambit2.base.data.substance.SubstanceEndpointsBundle;

public class CreateBundleCopy extends AbstractObjectUpdate<SubstanceEndpointsBundle> implements IStoredProcStatement {
    public static final String[] create_bundle_copy = { "{call createBundleCopy(?,?,?)}" };

    public CreateBundleCopy() {
	super();
    }

    public CreateBundleCopy(SubstanceEndpointsBundle bundle) {
	super();
	setObject(bundle);
    }

    @Override
    public void registerOutParameters(CallableStatement statement) throws SQLException {
	statement.registerOutParameter(3, java.sql.Types.INTEGER);
    }

    @Override
    public List<QueryParam> getParameters(int arg0) throws AmbitException {
	List<QueryParam> params = new ArrayList<QueryParam>();
	if (getObject().getID() > 0) {
	    params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
	    params.add(new QueryParam<String>(String.class, getObject().getUserName()));
	    params.add(new QueryParam<Integer>(Integer.class, -1));

	} else
	    throw new AmbitException("No bundle id");
	// params.add(new QueryParam<Integer>(Integer.class,
	// (Integer)getGroup()));
	return params;
    }

    @Override
    public String[] getSQL() throws AmbitException {
	return create_bundle_copy;
    }

    @Override
    public void setID(int arg0, int arg1) {
    }

    enum _params {
	idbundle, username, id_new
    };

    @Override
    public void getStoredProcedureOutVars(CallableStatement statement) throws SQLException {
	try {
	    getObject().setID(statement.getInt(_params.id_new.ordinal() + 1));
	} catch (Exception x) {
	    logger.warning(x.getMessage());
	}
    }

    @Override
    public boolean isStoredProcedure() {
	return true;
    }

}