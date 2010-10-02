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
import java.util.Properties;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;

import ambit2.base.data.StringBean;
import ambit2.db.processors.DbCreateDatabase;


public abstract class DbUnitTest {
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

	protected String getDatabase() {
		loadProperties();
		String p = properties.getProperty("database.test");
		return p==null?"ambit-test":p;
	}
	protected String getPort() {
		loadProperties();
		String p = properties.getProperty("database.test.port");
		return p==null?"3306":p;		
	}
	protected String getUser() {
		loadProperties();
		String p = properties.getProperty("database.user.test");
		return p==null?"guest":p;			
	}
	protected String getPWD() {
		loadProperties();
		String p = properties.getProperty("database.user.test.password");
		return p==null?"guest":p;	
	}
	protected String getAdminUser() {
		return "root";
	}
	protected String getAdminPWD() {
		loadProperties();
		String p = properties.getProperty("database.user.root.password");
		return p==null?"":p;	
	}	
	@Before
	public void setUp() throws Exception {
		IDatabaseConnection c = getConnection("mysql",getPort(),getAdminUser(),getAdminPWD());
		try {
			DbCreateDatabase db = new DbCreateDatabase();
			db.setConnection(c.getConnection());
			db.process(new StringBean(getDatabase()));
		} finally {
			c.close();
		}
	}
	protected IDatabaseConnection getConnection(String db,String port,String user, String pass) throws Exception {
		  
        Class.forName("com.mysql.jdbc.Driver");
        Connection jdbcConnection = DriverManager.getConnection(
                String.format("jdbc:mysql://localhost:%s/%s?useUnicode=true&characterEncoding=UTF8&characterSetResults=UTF-8",port,db)
                , user,pass);
//SET NAMES utf8	        
	   return new DatabaseConnection(jdbcConnection);
	}	
	protected IDatabaseConnection getConnection() throws Exception {
	   return getConnection(getDatabase(),getPort(),getUser(),getPWD());
	}
    public void setUpDatabase(String xmlfile) throws Exception {
        IDatabaseConnection connection = getConnection();
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setCaseSensitiveTableNames(false);
        IDataSet dataSet = builder.build(new File(xmlfile));
        try {
            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        } finally {
            connection.close();

        }
    }
}
