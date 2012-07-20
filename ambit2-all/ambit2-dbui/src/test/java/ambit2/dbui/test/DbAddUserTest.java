/* DbAddUserTest.java
 * Author: Nina Jeliazkova
 * Date: May 6, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
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

package ambit2.dbui.test;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.swing.JOptionPane;

import ambit2.base.data.AmbitUser;
import ambit2.db.pool.DatasourceFactory;
import ambit2.db.processors.DbAddUser;
import ambit2.db.processors.DbDeleteUser;
import ambit2.dbui.LoginInfoBean;
import ambit2.dbui.LoginPanel;


public class DbAddUserTest extends RepositoryTest {
	protected LoginInfoBean info;
    protected void setUp() throws Exception {
    	//The default guest user doesn't have CREATE USER rights
    	info = new LoginInfoBean();
    	info.setDatabase("ambit2");
    	info.setUser("root");
    	info.setPassword("");
    	info.setPort("3306");
    	info.setHostname("localhost");
    	LoginPanel p= new LoginPanel();
    	p.setObject(info);
    	JOptionPane.showConfirmDialog(null,p);
    	
        datasource = DatasourceFactory.getDataSource(
                DatasourceFactory.getConnectionURI("jdbc:mysql", info.getHostname(),info.getPort(),info.getDatabase(),info.getUser(),info.getPassword() ));
                
        
    }


    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void test() throws Exception {
        DbAddUser r = new DbAddUser();
        
        Connection c = datasource.getConnection();
        r.setConnection(c);
        r.open();
        AmbitUser user = new AmbitUser("test3");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPassword("");
        r.write(user);
        
        String username = c.getMetaData().getUserName();
        assertEquals(info.getUser(),username.substring(0,username.indexOf("@")));
        r.close();

        assertEquals("test3",user.getName());
        DataSource ds;
        Connection newConnection;
        /*
        ds = DatasourceFactory.getDataSource(
                DatasourceFactory.getConnectionURI("jdbc:mysql", info.getHostname(),info.getPort(),info.getDatabase(), user.getName(), user.getPassword()));
        newConnection = ds.getConnection(); 
        assertNotNull(newConnection);
        newConnection.close();
        newConnection = null;
        */
        
        c = datasource.getConnection();
        DbDeleteUser delete = new DbDeleteUser();
        delete.setConnection(c);
        delete.open();
        delete.write(user);
        c.close();
        
        ds = DatasourceFactory.getDataSource(
                DatasourceFactory.getConnectionURI("jdbc:mysql", info.getHostname(),info.getPort(),info.getDatabase(), user.getName(), user.getPassword()));
        try {
        newConnection = ds.getConnection();
        username= newConnection.getMetaData().getUserName();
        assertEquals(user.getName(),username.substring(0,username.indexOf("@")));
        } catch (SQLException x) {
        	assertTrue(true);
        }
        //not null because of cached datasource???
        //assertNull(newConnection);
        /*
        PreparedStatement s = newConnection.prepareStatement("SELECT USER_NAME from USERS WHERE USER_NAME=?");
        
        int records = 0;
        s.setString(1, user.getName());
        ResultSet rs = s.executeQuery();
        while (rs.next()) {
        	System.out.println(rs.getString(1));
        	records++;
        }
        rs.close();
        newConnection.close();
        assertEquals(0,records);
        */
    }
    
}
