/* DbCreateDatabaseTest.java
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
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

import ambit2.core.data.StringBean;
import ambit2.db.DBVersion;
import ambit2.db.DatasourceFactory;
import ambit2.db.LoginInfo;
import ambit2.db.processors.DbCreateDatabase;
import ambit2.dbui.LoginPanel;

public class DbCreateDatabaseTest extends RepositoryTest {
	protected LoginInfo info;
    protected void setUp() throws Exception {
    	info = new LoginInfo();
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
        DbCreateDatabase r = new DbCreateDatabase();
        
        Connection c = datasource.getConnection();
        r.setConnection(c);
        
        StringBean newdb = new StringBean("ambit-Test");
        r.write(newdb);
        c.close();
        datasource = DatasourceFactory.getDataSource(
                DatasourceFactory.getConnectionURI("jdbc:mysql", info.getHostname(),info.getPort(),newdb.toString(), "guest","guest"));        
        c = datasource.getConnection();
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("select idmajor,idminor from version");
        boolean ok = false;
        while (rs.next()) {
        	if ((rs.getInt(1) == DBVersion.AMBITDB_VERSION_MAJOR) && (rs.getInt(2) == DBVersion.AMBITDB_VERSION_MINOR)) {
        		ok = true;
        		break;
        	}
        }
        rs.close();
        c.close();
        assertTrue(ok);
    }
}
