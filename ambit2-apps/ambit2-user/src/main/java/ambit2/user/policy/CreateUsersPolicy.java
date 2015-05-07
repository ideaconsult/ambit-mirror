package ambit2.user.policy;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.policy.CreatePolicy;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.i.aa.RESTPolicy;
import net.idea.restnet.user.DBUser;

/**
 * Create a role, a policy and add users to the role
 * 
 * @author nina
 * 
 */
public class CreateUsersPolicy extends AbstractUpdate<RESTPolicy, RESTPolicyUsers> implements IDBConfig {
    protected String databaseName = null;
    private final static String createRole = "insert ignore into %sroles values (?)";
    private final static String removeRolefromUsers = "delete from %suser_roles where role_name=?";
    private final static String assignRole2User = "insert into %suser_roles values %s";
    protected CreatePolicy createPolicyQuery;
    protected CreatePolicy createExistingRolePolicy;

    protected int i_createrole = 0;
    protected int i_removeroles = 1;
    protected int i_assignroles = 2;
    protected int i_createpolicy = 3;
    protected int i_createpolicy_existingrole = 4;

    protected StringBuilder users_sql;
    private static final String policy_sql = "insert into %spolicy (idpolicy,role_name,prefix,resource,level,mget,mput,mpost,mdelete) values (null,?,?,?,?,?,?,?,?) on duplicate key update level=values(level),mget=values(mget),mput=values(mput),mpost=values(mpost),mdelete=values(mdelete)";

    public CreateUsersPolicy(RESTPolicy existingrole, RESTPolicyUsers policy) {
	super(policy);
	createPolicyQuery = new CreatePolicy() {
	    @Override
	    public String[] getSQL() throws AmbitException {
		return new String[] { String.format(policy_sql,
			databaseName == null ? "" : String.format("`%s`.", databaseName)) };
	    }
	};
	createExistingRolePolicy = new CreatePolicy() {
	    @Override
	    public String[] getSQL() throws AmbitException {
		return new String[] { String.format(policy_sql,
			databaseName == null ? "" : String.format("`%s`.", databaseName)) };
	    }
	};
	setGroup(existingrole);
	setObject(policy);
    }

    @Override
    public void setGroup(RESTPolicy group) {
	super.setGroup(group);
	if (group != null && createExistingRolePolicy != null) {
	    createExistingRolePolicy.setObject(group);
	}
	initIndices();
    }

    @Override
    public void setObject(RESTPolicyUsers object) {
	super.setObject(object);
	if (createPolicyQuery != null) {
	    createPolicyQuery.setObject(object);
	    createPolicyQuery.setGroup(new DBRole(object.getRole(), object.getRole()));
	}
	initIndices();
    }

    protected void initIndices() {
	if (object.getUsers() == null || object.getUsers().size() == 0) {
	    i_createrole = 0;
	    i_removeroles = 1;
	    i_assignroles = -1;
	    i_createpolicy = 2;
	    i_createpolicy_existingrole = 3;
	    users_sql = null;
	} else {
	    i_createrole = 0;
	    i_removeroles = 1;
	    i_assignroles = 2;
	    i_createpolicy = 3;
	    i_createpolicy_existingrole = 4;
	    users_sql = new StringBuilder();
	    String d = "";
	    for (DBUser user : getObject().getUsers()) {
		users_sql.append(d);
		users_sql.append("(?,?)");
		d = ",";
	    }
	}
	if (getGroup() == null)
	    i_createpolicy_existingrole = -1;
    }

    @Override
    public List<QueryParam> getParameters(int index) throws AmbitException {
	if (index == i_createrole) {
	    List<QueryParam> params2 = new ArrayList<QueryParam>();
	    params2.add(new QueryParam<String>(String.class, getObject().getRole()));
	    return params2;
	} else if (index == i_removeroles) {
	    List<QueryParam> params2 = new ArrayList<QueryParam>();
	    params2.add(new QueryParam<String>(String.class, getObject().getRole()));
	    return params2;
	} else if (index == i_assignroles) {
	    List<QueryParam> params2 = new ArrayList<QueryParam>();
	    for (DBUser user : getObject().getUsers()) {
		params2.add(new QueryParam<String>(String.class, user.getUserName()));
		params2.add(new QueryParam<String>(String.class, getObject().getRole()));
	    }
	    return params2;
	} else if (index == i_createpolicy) {
	    return createPolicyQuery.getParameters(0);
	} else if (index == i_createpolicy_existingrole) {
	    return createExistingRolePolicy.getParameters(0);
	}
	throw new AmbitException();
    }

    @Override
    public String[] getSQL() throws AmbitException {
	int n = 3;
	if (i_assignroles > 0)
	    n++;
	if (i_createpolicy_existingrole > 0)
	    n++;
	String[] sql = new String[n];
	sql[i_createrole] = String.format(createRole, databaseName == null ? "" : String.format("`%s`.", databaseName));
	sql[i_removeroles] = String.format(removeRolefromUsers,
		databaseName == null ? "" : String.format("`%s`.", databaseName));

	sql[i_createpolicy] = createPolicyQuery.getSQL()[0];
	if (i_assignroles > 0) {
	    sql[i_assignroles] = String.format(assignRole2User,
		    databaseName == null ? "" : String.format("`%s`.", databaseName), users_sql.toString());
	}
	if (i_createpolicy_existingrole > 0) {
	    sql[i_createpolicy_existingrole] = createExistingRolePolicy.getSQL()[0];
	}
	return sql;
    }

    @Override
    public void setDatabaseName(String name) {
	databaseName = name;
	createPolicyQuery.setDatabaseName(name);
	createExistingRolePolicy.setDatabaseName(name);
    }

    @Override
    public String getDatabaseName() {
	return databaseName;
    }

    @Override
    public void setID(int index, int id) {
	switch (index) {
	case 0: {
	    break;
	}
	case 1: {
	    break;
	}
	case 2: {
	    createPolicyQuery.setID(0, id);
	}
	}

    }
}
