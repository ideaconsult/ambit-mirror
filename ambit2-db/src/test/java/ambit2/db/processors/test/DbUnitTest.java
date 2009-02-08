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
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;

import ambit2.core.data.StringBean;
import ambit2.db.processors.DbCreateDatabase;


public abstract class DbUnitTest {
	//TODO make use of properties
	protected String getDatabase() {
		return "ambit-test";
	}
	protected String getPort() {
		return "33060";
	}
	protected String getUser() {
		return"guest";
	}
	protected String getPWD() {
		return "guest";
	}
	protected String getAdminUser() {
		return "root";
	}
	protected String getAdminPWD() {
		return "";
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
                "jdbc:mysql://localhost:"+port +"/"+db, user,pass);
	        
	   return new DatabaseConnection(jdbcConnection);
	}	
	protected IDatabaseConnection getConnection() throws Exception {
	   return getConnection(getDatabase(),getPort(),getUser(),getPWD());
	}
    public void setUpDatabase(String xmlfile) throws Exception {

        IDatabaseConnection connection = getConnection();
        IDataSet dataSet = new FlatXmlDataSet(new File(xmlfile));
        try {
            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        } finally {
            connection.close();
        }
    }
}
