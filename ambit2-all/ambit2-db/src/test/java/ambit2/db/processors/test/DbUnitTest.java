/* DbUnitTest.java
 * Author: nina
 * Date: Jan 7, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.db.processors.test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.BeforeClass;

import ambit2.base.config.AMBITConfigProperties;
import ambit2.base.config.Preferences;
import ambit2.base.data.StringBean;
import ambit2.db.DBVersion;
import ambit2.db.processors.DbCreateDatabase;
import junit.framework.Assert;

public abstract class DbUnitTest {
	boolean noAccessToProcedureBodies = true;

	public boolean isNoAccessToProcedureBodies() {
		return noAccessToProcedureBodies;
	}

	public void setNoAccessToProcedureBodies(boolean noAccessToProcedureBodies) {
		this.noAccessToProcedureBodies = noAccessToProcedureBodies;
	}

	protected static Logger logger = Logger.getLogger(DbUnitTest.class.getName());
	protected static AMBITConfigProperties properties;
	
	@BeforeClass
	public static void initProperties() {
		properties = new AMBITConfigProperties();	
	}
	
	public static void setLogLevel(Level level) throws Exception {
		Logger tempLogger = logger;
		while (tempLogger != null) {
			tempLogger.setLevel(level);
			for (Handler handler : tempLogger.getHandlers())
				handler.setLevel(level);
			tempLogger = tempLogger.getParent();
		}
	}

	protected String getHost() {
		return properties.getPropertyWithDefault(Preferences.HOST, AMBITConfigProperties.ambitProperties, "localhost");
	}

	protected String getDatabase() {
		return properties.getPropertyWithDefault("database.test", AMBITConfigProperties.ambitProperties, "ambit-test");
	}

	protected String getPort() {
		return properties.getPropertyWithDefault("database.test.port", AMBITConfigProperties.ambitProperties, "3306");

	}

	protected String getUser() {
		return properties.getPropertyWithDefault("database.user.test", AMBITConfigProperties.ambitProperties, "guest");
	}

	protected String getPWD() {
		return properties.getPropertyWithDefault("database.user.test.password", AMBITConfigProperties.ambitProperties,
				"guest");
	}

	@Before
	public void setUp() throws Exception {
		boolean dbExists = false;
		IDatabaseConnection c = null;
		try {
			c = getConnection(getHost(), getDatabase(), getPort(), getUser(), getPWD());
			c.getConnection().setAutoCommit(false);
			dbExists = DbCreateDatabase.dbExists(c.getConnection(), getDatabase());
			if (dbExists) {
				String version = null;
				try {
					version = DbCreateDatabase.getDbVersion(c.getConnection(), getDatabase());
				} catch (Exception x) {
					version = null;
				}
				String expected = String.format("%s.%s", DBVersion.AMBITDB_VERSION_MAJOR,
						DBVersion.AMBITDB_VERSION_MINOR);
				if (!expected.equals(version)) {
					logger.log(Level.INFO,
							String.format("ambit DB version:\tExpected %s\tFound %s", expected, version));
					List<String> tables = DbCreateDatabase.tablesExists(c.getConnection(), getDatabase());
					DbCreateDatabase.dropTables(c.getConnection(), getDatabase(), tables);
					c.getConnection().commit();
					DbCreateDatabase db = new DbCreateDatabase(getUser(), getPWD());
					db.setUseExistingDatabase(true);
					db.setConnection(c.getConnection());
					db.create(new StringBean(getDatabase()));
					c.getConnection().commit();
				}
			}
		} catch (Exception x) {
			throw x;
		} finally {
			if (c != null)
				c.close();
		}

		if (!dbExists) {
			throw new Exception(String.format("Database %s does not exist. Please create before running these tests.",
					getDatabase()));
		}

	}

	protected IDatabaseConnection getConnection(String host, String db, String port, String user, String pass)
			throws Exception {
		return getConnection(host, db, port, user, pass, false);
	}

	protected IDatabaseConnection getConnection(String host, String db, String port, String user, String pass,
			boolean debug) throws Exception {
		String debugConnection = "&useUsageAdvisor=true&dontTrackOpenResources=false";
		String noAccessToProcedureBodiesQ = noAccessToProcedureBodies ? "&noAccessToProcedureBodies=true" : "";
		Class.forName("com.mysql.jdbc.Driver");
		Connection jdbcConnection = DriverManager.getConnection(String.format(
				"jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF8&characterSetResults=UTF-8&profileSQL=%s%s%s&useSSL=false&allowPublicKeyRetrieval=true",
				host, port, db, Boolean.toString(isProfileSQL()), (debug ? debugConnection : ""),
				noAccessToProcedureBodiesQ), user, pass);
		// SET NAMES utf8
		IDatabaseConnection c = new DatabaseConnection(jdbcConnection);
		DatabaseConfig dbConfig = c.getConfig();

		dbConfig.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, Boolean.TRUE);
		dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
		/*
		 * dbConn.getConfig().setProperty(DatabaseConfig.
		 * PROPERTY_METADATA_HANDLER , new MySqlMetadataHandler());
		 */
		return c;

	}

	protected boolean isProfileSQL() {
		return false;
	}

	protected IDatabaseConnection getConnection(boolean debug) throws Exception {
		return getConnection(getHost(), getDatabase(), getPort(), getUser(), getPWD(), debug);
	}

	protected IDatabaseConnection getConnection() throws Exception {
		return getConnection(getHost(), getDatabase(), getPort(), getUser(), getPWD());
	}

	public void setUpDatabaseFromResource(String resource) throws Exception {
		InputStream xmlfile = getClass().getClassLoader().getResourceAsStream(resource);
		Assert.assertNotNull(resource, xmlfile);
		setUpDatabase(xmlfile);
	}

	public void setUpDatabase(InputStream xmlfile) throws Exception {
		// This ensures all tables as defined in the schema are cleaned up, and
		// is a single place to modify if a schema changes
		try (InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/test/tables.xml")) {
			initDB(in, DatabaseOperation.DELETE_ALL, true);
		}
		// This will import only records, defined in the xmlfile
		initDB(xmlfile, DatabaseOperation.INSERT, false);
	}

	private void initDB(InputStream xmlin, DatabaseOperation op, boolean admin) throws Exception {
		IDatabaseConnection connection = admin ? getConnection(getHost(), getDatabase(), getPort(), getUser(), getPWD())
				: getConnection();
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setCaseSensitiveTableNames(false);
		IDataSet dataSet = builder.build(xmlin);
		try {
			// DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
			op.execute(connection, dataSet);
		} catch (Exception x) {
			logger.log(Level.SEVERE, x.getMessage(), x);
			throw x;
		} finally {
			connection.close();
			if (xmlin != null)
				xmlin.close();
		}
	}

	public static List<String> tablesExists(Connection connection, String dbname) throws Exception {
		int tables = 0;
		ResultSet rs = null;
		Statement st = null;
		List<String> table_names = new ArrayList<String>();
		try {
			st = connection.createStatement();
			rs = st.executeQuery(String.format("Use `%s`", dbname)); // just in
																		// case
		} catch (Exception x) {
			throw x;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception x) {
			}
			try {
				if (st != null)
					st.close();
			} catch (Exception x) {
			}
		}
		try {
			st = connection.createStatement();
			rs = st.executeQuery("show tables");
			while (rs.next()) {
				tables++;
				table_names.add(rs.getString(1));
			}

		} catch (Exception x) {
			throw x;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception x) {
			}
			try {
				if (st != null)
					st.close();
			} catch (Exception x) {
			}
		}
		return table_names;
	}
}
