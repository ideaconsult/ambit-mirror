package ambit2.rest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.restlet.Context;

import ambit2.base.config.Preferences;
import ambit2.db.LoginInfo;
import ambit2.rest.config.AMBITAppConfigProperties;
import net.idea.modbcum.c.DatasourceFactory;
import net.idea.modbcum.i.exceptions.AmbitException;

public class DBConnection {
	protected static AMBITAppConfigProperties properties = new AMBITAppConfigProperties();
	protected Context context;
	protected String namedConfig = null;

	public String getNamedConfig() {
		return namedConfig;
	}

	public void setNamedConfig(String namedConfig) {
		this.namedConfig = namedConfig;
	}

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


	public boolean allowDBCreate() {
		return properties.allowDatabaseCreate();
	}

	public String rdfWriter() {
		return properties.getRDFwriter();
	}

	/**
	 * 
	 * @return
	 */
	public boolean dataset_prefixed_compound_uri() {
		return properties.isDatasetMembersPrefix();
	}

	public LoginInfo getLoginInfo() {

		LoginInfo li = new LoginInfo();

		String p = properties.getPropertyWithDefault(Preferences.DATABASE,AMBITAppConfigProperties.ambitProperties,"ambit2");
		li.setDatabase(p == null || ("${ambit.db}".equals(p)) ? "ambit2" : p);
		p = properties.getPropertyWithDefault(Preferences.PORT,AMBITAppConfigProperties.ambitProperties,"3306");
		li.setPort(p == null ? "3306" : p);
		p = properties.getPropertyWithDefault(Preferences.USER,AMBITAppConfigProperties.ambitProperties,"guest");
		li.setUser(p == null ? "guest" : p);
		p = properties.getPropertyWithDefault(Preferences.PASSWORD,AMBITAppConfigProperties.ambitProperties,"guest");
		li.setPassword(p == null ? "guest" : p);
		p = properties.getPropertyWithDefault(Preferences.HOST,AMBITAppConfigProperties.ambitProperties,"localhost");
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
		return getConnection(5, false, 2);
	}

	public synchronized Connection getConnection(int maxRetry, boolean testIt, int testTimeout)
			throws AmbitException, SQLException {
		return getConnectionNamedConfig(getNamedConfig(), getConnectionURI(), maxRetry, testIt, testTimeout);
	}

	public synchronized Connection getConnection(String namedConfig) throws AmbitException, SQLException {
		// if (connectionURI == null)
		// connectionURI = getConnectionURI();
		return getConnectionNamedConfig(namedConfig, getConnectionURI(), 5, false, 2);
	}

	public synchronized Connection getConnectionNamedConfig(String namedConfig, String connectionURI, int maxRetry,
			boolean testIt, int testTimeout) throws AmbitException, SQLException {
		SQLException error = null;
		Connection c = null;

		ResultSet rs = null;
		Statement t = null;
		for (int retry = 0; retry < maxRetry; retry++)
			try {
				error = null;
				DataSource ds = DatasourceFactory.getDataSource(namedConfig, connectionURI, "com.mysql.jdbc.Driver");

				c = ds.getConnection();
				if (testIt) {
					if (c.isValid(testTimeout))
						return c;
					else
						throw new SQLException(String.format("Invalid connection on attempt %d", retry));
				} else
					return c;
			} catch (SQLException x) {
				error = x;
				Context.getCurrentLogger().severe(x.getMessage());
				// remove the connection from the pool
				try {
					if (c != null) {
						c.close();
						c = null;
					}
				} catch (Exception e) {
				}
			} catch (Throwable x) {
				Context.getCurrentLogger().severe(x.getMessage());
				try {
					if (c != null) {
						c.close();
						c = null;
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
		return properties.getPropertyWithDefault(key,AMBITAppConfigProperties.ambitProperties,null);
	}
}
