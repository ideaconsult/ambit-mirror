package ambit2.rest;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.Context;

import ambit2.base.config.Preferences;
import ambit2.db.LoginInfo;
import ambit2.db.pool.DatasourceFactory;

public class DBConnection {
    protected static Properties properties = null;
    protected Context context;

    public Context getContext() {
	return context;
    }

    public void setContext(Context context) {
	this.context = context;
    }

    public DBConnection(Context context) {
	super();
	this.context = context;
    }

    protected synchronized void loadProperties() {
	try {
	    if (properties == null) {
		properties = new Properties();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/rest/config/ambit2.pref");
		properties.load(in);
		in.close();
	    }
	} catch (Exception x) {
	    properties = null;
	}
    }

    public boolean allowDBCreate() {
	loadProperties();
	String ok = properties.getProperty("database.create");
	return (ok != null) && ok.toLowerCase().equals("true");
    }

    public String rdfWriter() {
	loadProperties();
	String rdfwriter = properties.getProperty("rdf.writer");
	return (rdfwriter == null) ? "jena" : rdfwriter;// jena or stax
    }

    /**
     * 
     * @return
     */
    public boolean dataset_prefixed_compound_uri() {
	loadProperties();
	String prefix = properties.getProperty("dataset.members.prefix");
	try {
	    return Boolean.parseBoolean(prefix);
	} catch (Exception x) {
	    return false;
	}
    }

    public LoginInfo getLoginInfo() {
	loadProperties();
	LoginInfo li = new LoginInfo();

	String p = properties.getProperty("Database");
	li.setDatabase(p == null || ("${ambit.db}".equals(p)) ? "ambit2" : p);
	p = properties.getProperty("Port");
	li.setPort(p == null ? "3306" : p);
	p = properties.getProperty("User");
	li.setUser(p == null ? "guest" : p);
	p = properties.getProperty("Password");
	li.setPassword(p == null ? "guest" : p);
	p = properties.getProperty(Preferences.HOST);
	li.setHostname(p == null || ("${ambit.db.host}".equals(p)) ? "localhost" : p);

	if (getContext().getParameters().getFirstValue(Preferences.DATABASE) != null)
	    li.setDatabase(getContext().getParameters().getFirstValue(Preferences.DATABASE));
	if (getContext().getParameters().getFirstValue(Preferences.USER) != null)
	    li.setUser(getContext().getParameters().getFirstValue(Preferences.USER));
	if (getContext().getParameters().getFirstValue(Preferences.PASSWORD) != null)
	    li.setPassword(getContext().getParameters().getFirstValue(Preferences.PASSWORD));
	if (getContext().getParameters().getFirstValue(Preferences.HOST) != null)
	    li.setHostname(getContext().getParameters().getFirstValue(Preferences.HOST));
	if (getContext().getParameters().getFirstValue(Preferences.PORT) != null)
	    li.setPort(getContext().getParameters().getFirstValue(Preferences.PORT));

	return li;
    }

    protected String getConnectionURI() throws AmbitException {
	return getConnectionURI(null, null);
    }

    protected String getConnectionURI(String user, String password) throws AmbitException {

	try {
	    LoginInfo li = getLoginInfo();
	    return DatasourceFactory.getConnectionURI(li.getScheme(), li.getHostname(), li.getPort(), li.getDatabase(),
		    user == null ? li.getUser() : user, password == null ? li.getPassword() : password);
	} catch (Exception x) {
	    throw new AmbitException(x);
	}

    }

    public synchronized Connection getConnection(String user, String password) throws AmbitException, SQLException {
	return getConnection(getConnectionURI(user, password));
    }

    public synchronized Connection getConnection() throws AmbitException, SQLException {
	// if (connectionURI == null)
	// connectionURI = getConnectionURI();
	return getConnection(getConnectionURI());
    }

    public synchronized Connection getConnection(String connectionURI) throws AmbitException, SQLException {
	SQLException error = null;
	Connection c = null;

	ResultSet rs = null;
	Statement t = null;
	for (int retry = 0; retry < 5; retry++)
	    try {
		DataSource ds = DatasourceFactory.getDataSource(connectionURI);
		/*
		 * if ( ds instanceof PooledDataSource) { PooledDataSource pds =
		 * (PooledDataSource) ds; System.err.println("num_connections: "
		 * + pds.getNumConnectionsDefaultUser());
		 * System.err.println("num_busy_connections: " +
		 * pds.getNumBusyConnectionsDefaultUser());
		 * System.err.println("num_idle_connections: " +
		 * pds.getNumIdleConnectionsDefaultUser());
		 * System.err.println("num_thread_awaiting: "
		 * +pds.getNumThreadsAwaitingCheckoutDefaultUser());
		 * System.err.println
		 * ("StatementCacheNumCheckedOutStatementsAllUsers: "
		 * +pds.getStatementCacheNumCheckedOutStatementsAllUsers());
		 * System.err.println("num_unclosed_orphaned_connections: "
		 * +pds.getNumUnclosedOrphanedConnectionsAllUsers());
		 * 
		 * System.err.println(); } else
		 * System.err.println("Not a c3p0 PooledDataSource!");
		 */
		c = ds.getConnection();
		t = c.createStatement();
		rs = t.executeQuery("/* ping */ SELECT 1 FROM DUAL");
		while (rs.next()) {
		    rs.getInt(1);
		}
		rs.close();
		rs = null;
		t.close();
		t = null;
		error = null;
		return c;
	    } catch (SQLException x) {
		error = x;
		Context.getCurrentLogger().severe(x.getMessage());
		// remove the connection from the pool
		try {
		    if (c != null) {
			c.close();
			c= null;
		    }	
		} catch (Exception e) {
		}
	    } catch (Throwable x) {
		Context.getCurrentLogger().severe(x.getMessage());
		try {
		    if (c != null) {
			c.close();
			c= null;
		    }	
		} catch (Exception e) {
		}
	    } finally {
		try {
		    if (rs != null)
			rs.close();
		} catch (Exception x) {
		}
		try {
		    if (t != null)
			t.close();
		} catch (Exception x) {
		}
	    }
	if (error != null)
	    throw error;
	else
	    throw new SQLException("Can't establish connection " + connectionURI);
    }

    /*
     * 
     * public synchronized Connection getConnection(String connectionURI) throws
     * AmbitException , SQLException{
     * 
     * return DatasourceFactory.getDataSource(connectionURI).getConnection();
     * 
     * 
     * }
     */

    public String getProperty(String key) {
	loadProperties();
	return properties.getProperty(key);
    }
}
