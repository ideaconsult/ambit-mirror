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

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;

import ambit2.base.data.StringBean;
import ambit2.db.processors.DbCreateDatabase;


public abstract class DbUnitTest {
	protected static Logger logger = Logger.getLogger(DbUnitTest.class.getName());
	protected Properties properties;
	
	protected void loadProperties()  {
		try {
		if (properties == null) {
			properties = new Properties();
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/conf/ambit2.pref");
			properties.load(in);
			in.close();		
		}
		} catch (Exception x) {
			properties = null;
		}
	}
	protected String getHost() {
		loadProperties();
		String p = properties.getProperty("Host");
		return p==null?"localhost":
			("${ambit.db.host}".equals(p))?"localhost":p;
	}
	protected String getDatabase() {
		loadProperties();
		String p = properties.getProperty("database.test");
		return (p==null)||("${ambit.db}".equals(p))?"ambit-test":p;
	}
	protected String getPort() {
		loadProperties();
		String p = properties.getProperty("database.test.port");
		return p==null?"3306":p;		
	}
	protected String getUser() {
		loadProperties();
		String p = properties.getProperty("database.user.test");
		return (p==null) || ("${ambit.db.user.test}".equals(p))?"guest":p;			
	}
	protected String getPWD() {
		loadProperties();
		String p = properties.getProperty("database.user.test.password");
		return (p==null) || ("${ambit.db.user.test.password}".equals(p))?"guest":p;	
	}
	protected String getAdminUser() {
		return "root";
	}
	protected String getAdminPWD() {
		loadProperties();
		String p = properties.getProperty("database.user.root.password");
		return (p==null) || ("${ambit.db.user.root.password}".equals(p))?"":p;	
	}	
	@Before
	public void setUp() throws Exception {
		IDatabaseConnection c = getConnection(getHost(),"mysql",getPort(),getAdminUser(),getAdminPWD());
		boolean dbExists = false;
		Statement st = null;
		ResultSet rs = null;
		try {
			String sql = String.format("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '%s'",getDatabase());
			st = c.getConnection().createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				dbExists = rs.getString(1).equals(getDatabase());
			}
		} catch (Exception x) {
			dbExists = false;
		} finally {
			rs.close();
			st.close();
		}

		if (!dbExists)
			try {
				
				DbCreateDatabase db = new DbCreateDatabase(getPWD(),getAdminPWD());
				db.setConnection(c.getConnection());
				db.process(new StringBean(getDatabase()));
			} finally {
				c.close();
			}
		

	}
	protected IDatabaseConnection getConnection(String host,String db,String port,String user, String pass) throws Exception {
		return getConnection( host, db, port, user,  pass,false);
	}
	protected IDatabaseConnection getConnection(String host,String db,String port,String user, String pass,boolean debug) throws Exception {
		String debugConnection = "&useUsageAdvisor=true&dontTrackOpenResources=false";		  
        Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = DriverManager.getConnection(
                String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF8&characterSetResults=UTF-8&profileSQL=%s%s",
                		host,port,db,Boolean.toString(isProfileSQL()),(debug?debugConnection:"")
                		)
                , user,pass);
//SET NAMES utf8	        
        IDatabaseConnection c = new DatabaseConnection(jdbcConnection);
		DatabaseConfig dbConfig = c.getConfig();
		dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
		/*
		dbConn.getConfig().setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER, new MySqlMetadataHandler());
		 */
		return c;

	}	
	protected boolean isProfileSQL() {
		return false;
	}
	protected IDatabaseConnection getConnection(boolean debug) throws Exception {
		   return getConnection(getHost(),getDatabase(),getPort(),getUser(),getPWD(),debug);
	}
	protected IDatabaseConnection getConnection() throws Exception {
	   return getConnection(getHost(),getDatabase(),getPort(),getUser(),getPWD());
	}
    public void setUpDatabase(String xmlfile) throws Exception {
    	//This ensures all tables as defined in the schema are cleaned up, and is a single place to modify if a schema changes
    	initDB("src/test/resources/ambit2/db/processors/test/tables.xml",DatabaseOperation.DELETE_ALL,true);
    	//This will import only records, defined in the xmlfile
    	initDB(xmlfile,DatabaseOperation.INSERT,false);
    }
    
    private void initDB(String xmlfile,DatabaseOperation op,boolean admin ) throws Exception {
        IDatabaseConnection connection = admin?getConnection(getHost(),getDatabase(),getPort(),getAdminUser(),getAdminPWD()):getConnection();
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setCaseSensitiveTableNames(false);
        IDataSet dataSet = builder.build(new File(xmlfile));
        try {
            //DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
            op.execute(connection, dataSet);
        } catch (Exception x) {
        	x.printStackTrace();
        	throw x;
        } finally {
            connection.close();

        }
    }
}
