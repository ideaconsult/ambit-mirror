/* DatasourceFactoryTest.java
 * Author: Nina Jeliazkova
 * Date: Apr 13, 2008 
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

package ambit2.test.database;


import java.sql.Connection;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

import ambit2.database.DatasourceFactory;

public class DatasourceFactoryTest extends TestCase{

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    public void testGetConnectionURI() throws Exception {
        assertEquals(
                "jdbc:mysql://test/db",
                DatasourceFactory.getConnectionURI("test",null,"db")
                );
    }
    public void testGetConnectionURIPort() throws Exception {
        assertEquals(
                "jdbc:mysql://test:33060/db",
                DatasourceFactory.getConnectionURI("test","33060","db")
                );
    }
    
    public void testGetConnectionURIAll() throws Exception {
        String s = DatasourceFactory.getConnectionURI("jdbc:mysql","test","33060","db","myuser","mypass");
        assertEquals(
                "jdbc:mysql://test:33060/db?user=myuser&password=mypass",
                s
                );
    }    
    public void testGetDatasource() throws Exception {
        DataSource ds = DatasourceFactory.getDataSource(DatasourceFactory.getConnectionURI("localhost",null,"mysql"));
        assertNotNull(ds);
        Connection c = ds.getConnection();
        assertNotNull(c);
    }
}
