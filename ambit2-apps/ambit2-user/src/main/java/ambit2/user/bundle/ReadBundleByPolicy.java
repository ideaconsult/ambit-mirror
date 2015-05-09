package ambit2.user.bundle;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import ambit2.db.update.bundle.ReadBundle;

/**
 * a bit of a hack to get bundles with R/W rights for a given user
 * 
 * @author nina
 * 
 */
public class ReadBundleByPolicy extends ReadBundle implements IDBConfig {

    /**
     * 
     */
    private static final long serialVersionUID = 7217707028379108173L;
    private static final String sql_policy = "where hex(bundle_number) in (select replace(substring(resource,9),'-','') from %s.policy\n"
	    + "join %s.user_roles using(role_name) where  user_name=? and resource like '/bundle/%%' and %s)";
    protected String databaseName;
    protected boolean mode_write = false;

    public boolean isMode_write() {
	return mode_write;
    }

    public void setMode_write(boolean mode_write) {
	this.mode_write = mode_write;
    }

    @Override
    public java.util.List<QueryParam> getParameters() throws AmbitException {
	if (getFieldname() != null) {
	    List<QueryParam> params = new ArrayList<QueryParam>();
	    params.add(new QueryParam<String>(String.class, getFieldname()));
	    return params;
	} else
	    throw new AmbitException("Empty query");

    }

    public String getSQL() throws AmbitException {
	try {
	    String w = String.format(sql_policy, getDatabaseName(), getDatabaseName(),
		    isMode_write() ? " (mput=1 || mpost=1) " : " mget=1");
	    String sql = String.format(select_datasets, w, "");
	    return sql;
	} catch (Exception x) {
	    throw new AmbitException(x);
	}
    }

    @Override
    public void setDatabaseName(String name) {
	databaseName = name;
    }

    @Override
    public String getDatabaseName() {
	return databaseName;
    }
}
