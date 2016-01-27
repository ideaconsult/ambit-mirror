package ambit2.user.policy;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.restnet.db.aalocal.policy.CreatePolicy;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.i.aa.RESTPolicy;

/**
 * Bundle specific policies
 * 
 * @author nina
 * 
 */
public class CreateUsersBundlePolicy extends CreateUsersPolicy implements
		IDBConfig {
	private static final String bundle_policy_sql = "insert into %spolicy_bundle (idpolicy,role_name,prefix,resource,level,mget,mput,mpost,mdelete) values (null,?,?,unhex(?),?,?,?,?,?) on duplicate key update level=values(level),mget=values(mget),mput=values(mput),mpost=values(mpost),mdelete=values(mdelete)";

	public CreateUsersBundlePolicy(RESTPolicy existingrole,
			RESTPolicyUsers policy) {
		super(existingrole, policy);
	}

	@Override
	protected CreatePolicy getCreatePolicyQuery() {
		return new CreatePolicy() {
			@Override
			public String[] getSQL() throws AmbitException {
				return new String[] { String.format(
						bundle_policy_sql,
						databaseName == null ? "" : String.format("`%s`.",
								databaseName)) };
			}

			@Override
			public List<QueryParam> getParameters(int index)
					throws AmbitException {
				if ((getObject() == null) || (getObject().getRole() == null)
						|| (getObject().getUri() == null))
					throw new AmbitException("Undefined policy");
				try {
					String[] prefix_resource = getObject().splitURI(
							getObject().getUri());
					String uuid = prefix_resource[1].replace("/bundle/","").replace("-", "");
					List<QueryParam> params2 = new ArrayList<QueryParam>();
					params2.add(new QueryParam<String>(String.class,
							getObject().getRole()));
					params2.add(new QueryParam<String>(String.class,
							prefix_resource[0]));
					params2.add(new QueryParam<String>(String.class,
							uuid));
					params2.add(new QueryParam<Integer>(Integer.class,
							getObject().getLevel(prefix_resource[1])));
					params2.add(new QueryParam<Boolean>(Boolean.class,
							getObject().isAllowGET()));
					params2.add(new QueryParam<Boolean>(Boolean.class,
							getObject().isAllowPUT()));
					params2.add(new QueryParam<Boolean>(Boolean.class,
							getObject().isAllowPOST()));
					params2.add(new QueryParam<Boolean>(Boolean.class,
							getObject().isAllowDELETE()));
					return params2;
				} catch (Exception x) {
					throw new AmbitException(x);
				}

			}
		};
	}
}
