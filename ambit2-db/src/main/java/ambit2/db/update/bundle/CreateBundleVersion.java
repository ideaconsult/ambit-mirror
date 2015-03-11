package ambit2.db.update.bundle;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.idea.modbcum.i.IStoredProcStatement;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;
import ambit2.base.data.I5Utils;
import ambit2.base.data.substance.SubstanceEndpointsBundle;

public class CreateBundleVersion extends AbstractObjectUpdate<SubstanceEndpointsBundle> implements IStoredProcStatement {
    public static final String[] create_bundle_version = { "{call createBundleVersion(?,?,?,?)}" };

    public CreateBundleVersion() {
	super();
    }

    public CreateBundleVersion(SubstanceEndpointsBundle bundle) {
	super();
	setObject(bundle);
    }

    @Override
    public void registerOutParameters(CallableStatement statement) throws SQLException {
	statement.registerOutParameter(2, java.sql.Types.INTEGER);
	statement.registerOutParameter(3, java.sql.Types.VARCHAR);
	statement.registerOutParameter(4, java.sql.Types.INTEGER);
    }

    @Override
    public List<QueryParam> getParameters(int arg0) throws AmbitException {
	List<QueryParam> params = new ArrayList<QueryParam>();
	if (getObject().getID() > 0) {
	    params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
	    params.add(new QueryParam<Integer>(Integer.class, -1));
	    params.add(new QueryParam<String>(String.class, null));
	    params.add(new QueryParam<Integer>(Integer.class, -1));

	} else
	    throw new AmbitException("No bundle id");
	// params.add(new QueryParam<Integer>(Integer.class,
	// (Integer)getGroup()));
	return params;
    }

    @Override
    public String[] getSQL() throws AmbitException {
	return create_bundle_version;
    }

    @Override
    public void setID(int arg0, int arg1) {
    }

    enum _params {
	idbundle, version, bundle_number, id_new
    };

    @Override
    public void getStoredProcedureOutVars(CallableStatement statement) throws SQLException {
	getObject().setVersion(statement.getInt(_params.version.ordinal() + 1));
	try {
	    getObject().setBundle_number(
		    UUID.fromString(I5Utils.addDashes(statement.getString(_params.bundle_number.ordinal() + 1))));
	} catch (Exception x) {
	    logger.warning(x.getMessage());
	}
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
