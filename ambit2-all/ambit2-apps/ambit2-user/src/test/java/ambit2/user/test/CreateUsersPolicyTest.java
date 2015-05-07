package ambit2.user.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.aalocal.CreateUsersDatabaseProcessor;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.test.DbUnitTest;
import net.idea.restnet.i.aa.RESTPolicy;
import net.idea.restnet.i.task.TaskResult;
import net.idea.restnet.user.DBUser;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Method;

import ambit2.db.UpdateExecutor;
import ambit2.user.policy.CallablePolicyUsersCreator;
import ambit2.user.policy.CreateUsersPolicy;
import ambit2.user.policy.RESTPolicyUsers;

public class CreateUsersPolicyTest extends DbUnitTest {
    protected UpdateExecutor<IQueryUpdate> executor;
    protected UUID bundle = UUID.randomUUID();
    protected UUID bundletest = UUID.nameUUIDFromBytes("TEST".getBytes());
    
    @Override
    @Before
    public void setUp() throws Exception {
	super.setUp();
	executor = new UpdateExecutor<IQueryUpdate>();
    }

    protected String getRole(String bundle,String mode) {
	return "B." + bundle.replace("-", "").toUpperCase() + "." + mode;
    }
    protected String getResource(String bundle) {
	return "/bundle/"+bundle.toUpperCase();
    }
    

    @Test
    public void testCreate() throws Exception {
	setUpDatabase(dbFile);
	IQueryUpdate query = createQuery();
	IDatabaseConnection c = getConnection();
	executor.setConnection(c.getConnection());
	executor.open();
	Assert.assertTrue(executor.process(query) >= 1);
	createVerify(query);
	c.close();
    }
    
    protected IQueryUpdate<RESTPolicy, RESTPolicyUsers> createQuery() throws Exception {
	DBRole role = new DBRole("user", "Bundle role");
	List<DBUser> users = new ArrayList<>();
	DBUser user = new DBUser();
	user.setUserName("test");
	users.add(user);
	user = new DBUser();
	user.setUserName("admin");
	users.add(user);
	RESTPolicyUsers policy = new RESTPolicyUsers(users);
	policy.setAllowGET(true);
	policy.setRole(getRole(bundle.toString(),"R"));
	policy.setUri("http://localhost/ambit2"+getResource(bundle.toString()));
	
	RESTPolicy all = new RESTPolicy();
	all.setAllowGET(true);
	all.setRole("user");
	all.setUri(policy.getUri());
	return new CreateUsersPolicy(all, policy);
    }


    protected void createVerify(IQueryUpdate<RESTPolicy, RESTPolicyUsers> query) throws Exception {
	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED_USER", "SELECT role_name,prefix,resource,level,mget,mput,mpost,mdelete FROM policy where role_name regexp '^B.*.R$'");
	Assert.assertEquals(1, table.getRowCount());
	Assert.assertEquals("/ambit2", table.getValue(0, "prefix"));
	Assert.assertEquals(getResource(bundle.toString()), table.getValue(0, "resource").toString());
	Assert.assertEquals(2,table.getValue(0, "level"));
	Assert.assertEquals(true,table.getValue(0, "mget"));
	Assert.assertEquals(false,table.getValue(0, "mput"));
	Assert.assertEquals(false,table.getValue(0, "mpost"));
	Assert.assertEquals(false,table.getValue(0, "mdelete"));
	
	table = c.createQueryTable("EXPECTED_USER", "SELECT role_name,prefix,resource,level,mget,mput,mpost,mdelete FROM policy where role_name ='user' and prefix='/ambit2' order by resource");	
	Assert.assertEquals(2, table.getRowCount());
	Assert.assertEquals(getResource(bundle.toString()), table.getValue(1, "resource").toString());
	Assert.assertEquals(2,table.getValue(1, "level"));
	Assert.assertEquals(true,table.getValue(1, "mget"));
	Assert.assertEquals(false,table.getValue(1, "mput"));
	Assert.assertEquals(false,table.getValue(1, "mpost"));
	Assert.assertEquals(false,table.getValue(1, "mdelete"));
	
	table = c.createQueryTable("EXPECTED_USER", "SELECT role_name FROM roles where role_name regexp '^B.*.R$'");
	Assert.assertEquals(1, table.getRowCount());
	table = c.createQueryTable("EXPECTED_USER", "SELECT user_name,role_name FROM user_roles where user_name in ('test','admin') and role_name regexp '^B.*.R$'");
	Assert.assertEquals(2, table.getRowCount());
	c.close();

    }

    @Test
    public void testUpdate() throws Exception {
	setUpDatabase(dbFile);
	IQueryUpdate query = updateQuery();
	IDatabaseConnection c = getConnection();
	executor.setConnection(c.getConnection());
	executor.open();
	Assert.assertTrue(executor.process(query) >= 1);
	updateVerify(query,2);
	c.close();
    }
    
    protected IQueryUpdate<RESTPolicy, RESTPolicyUsers> updateQuery() throws Exception {
   	DBRole role = null;
   	List<DBUser> users = new ArrayList<>();
   	DBUser user = new DBUser();
   	user.setUserName("test");
   	users.add(user);
   	user = new DBUser();
   	user.setUserName("admin");
   	users.add(user);
   	RESTPolicyUsers policy = new RESTPolicyUsers(users);
   	policy.setAllowGET(false);
	policy.setAllowPUT(true);
	policy.setAllowPOST(true);
	policy.setAllowDELETE(false);
   	policy.setRole(getRole(bundletest.toString(),"W"));
   	policy.setUri("http://localhost/ambit2"+getResource(bundletest.toString()));
   	
   	RESTPolicy all = new RESTPolicy();
	all.setAllowGET(false);
	all.setAllowPUT(false);
	all.setAllowPOST(false);
	all.setAllowDELETE(false);	
	all.setRole("user");
	all.setUri(policy.getUri());
   	return new CreateUsersPolicy(all, policy);
       }


       protected void updateVerify(IQueryUpdate<RESTPolicy, RESTPolicyUsers> query,int users) throws Exception {
   	IDatabaseConnection c = getConnection();
   	
   	ITable table = c.createQueryTable("EXPECTED_USER", "SELECT role_name,prefix,resource,level,mget,mput,mpost,mdelete FROM policy where role_name regexp '^B.*.W$'");
   	Assert.assertEquals(1, table.getRowCount());
   	Assert.assertEquals("/ambit2", table.getValue(0, "prefix"));
   	Assert.assertEquals(getResource(bundletest.toString()), table.getValue(0, "resource").toString());
   	Assert.assertEquals(2,table.getValue(0, "level"));
   	Assert.assertEquals(false,table.getValue(0, "mget"));
   	Assert.assertEquals(true,table.getValue(0, "mput"));
   	Assert.assertEquals(true,table.getValue(0, "mpost"));
   	Assert.assertEquals(false,table.getValue(0, "mdelete"));
   	
	table = c.createQueryTable("EXPECTED_USER", "SELECT role_name,prefix,resource,level,mget,mput,mpost,mdelete FROM policy where role_name ='user' and prefix='/ambit2'");	
	Assert.assertEquals(1, table.getRowCount());
   	
   	table = c.createQueryTable("EXPECTED_USER", "SELECT role_name FROM roles where role_name regexp '^B.*.W$'");
   	Assert.assertEquals(1, table.getRowCount());
   	table = c.createQueryTable("EXPECTED_USER", "SELECT user_name,role_name FROM user_roles where user_name in ('test','admin') and role_name regexp '^B.*.W$'");
   	Assert.assertEquals(users, table.getRowCount());
   	c.close();

       }
    protected String dbFile = "src/test/resources/ambit2/db/processors/test/users/aalocal.xml";

    @Override
    protected CreateDatabaseProcessor getDBCreateProcessor() {
	return new CreateUsersDatabaseProcessor() {
	    @Override
	    public synchronized String getSQLFile() {
		return "ambit2/db/sql/users/ambit_users.sql";
	    }
	};
    }

    @Override
    protected String getConfig() {
	return "net/idea/restnet/db/aalocal/aalocal.pref";
    }

    @Override
    public String getDBTables() {
	return "src/test/resources/ambit2/db/processors/test/users/tables.xml";
    }
    
    @Test
    public void testCallableReadAccess() throws Exception {
	setUpDatabase(dbFile);
	IDatabaseConnection c = getConnection();
	
	Form form  = new Form();
	form.add("bundle_number",bundle.toString());
	form.add("canRead","g_user");
	form.add("canRead","admin");
	form.add("canRead","test");
	
	try {
		
		CallablePolicyUsersCreator callable = new CallablePolicyUsersCreator(
				Method.POST,form,"http://localhost:8081/ambit2/",c.getConnection(),null,getDatabase()
				);
		TaskResult task = callable.call();
		createVerify(null);
		
	} catch (Exception x) {
		throw x;
	} finally {
		try {c.close();} catch (Exception x) {}
	}	
    }

    @Test
    public void testCallableWriteAccess() throws Exception {
	setUpDatabase(dbFile);
	IDatabaseConnection c = getConnection();
	
	Form form  = new Form();
	form.add("bundle_number",bundletest.toString());
	form.add("canWrite","admin");
	form.add("canWrite","test");
	
	try {
		
		CallablePolicyUsersCreator callable = new CallablePolicyUsersCreator(
				Method.POST,form,"http://localhost:8081/ambit2/",c.getConnection(),null,getDatabase()
				);
		callable.setDefaultRole("user");
		TaskResult task = callable.call();
		updateVerify(null,2);
		
	} catch (Exception x) {
		throw x;
	} finally {
		try {c.close();} catch (Exception x) {}
	}	
    }
    
    @Test
    public void testCallableGroupWriteAccess() throws Exception {
	setUpDatabase(dbFile);
	IDatabaseConnection c = getConnection();
	
	Form form  = new Form();
	form.add("bundle_number",bundletest.toString());
	form.add("canWrite","g_user");
	
	try {
		
		CallablePolicyUsersCreator callable = new CallablePolicyUsersCreator(
				Method.POST,form,"http://localhost:8081/ambit2/",c.getConnection(),null,getDatabase()
				);
		callable.setDefaultRole("user");
		TaskResult task = callable.call();
		updateVerify(null,0);
		
	} catch (Exception x) {
		throw x;
	} finally {
		try {c.close();} catch (Exception x) {}
	}	
    }    
}
