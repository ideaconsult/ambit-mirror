/* MySQLShellTest.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-9 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit2.test.processors;

import java.sql.SQLException;
import java.util.Hashtable;

import junit.framework.TestCase;
import ambit2.database.DbAdmin;
import ambit2.database.MySQLShell;
import ambit2.data.AmbitUser;
import ambit2.exceptions.AmbitException;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class MySQLShellTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MySQLShellTest.class);
    }


    public void xtestStart() {
        MySQLShell ms = new MySQLShell();
        try {
            ms.startMySQL();
            DbAdmin dbc = new DbAdmin("localhost","33060","mysql","root","");
            if (dbc.open(false)) {
                System.out.println("successfully connected");
                assertTrue(dbc.databaseExist("mysql"));
                assertFalse(dbc.databaseExist("ambit"));
                
            }
            dbc.close();
        } catch (AmbitException x) {
            x.printStackTrace();
            fail();
        } catch (SQLException x) {
            x.printStackTrace();
            fail();
        }
        try {
            ms.stopMySQL();
            System.out.println("stopped");
        } catch (AmbitException x) {
            x.printStackTrace();
            fail();
        }
    }

    public void testStop() {
    }

    public void testCreateDatabase() {
        String db = "ambittest";
        MySQLShell ms = new MySQLShell();
        try {
            ms.startMySQL();
            DbAdmin dbc = new DbAdmin("localhost","33060","mysql","root","");
            dbc.setCreateSQLFile("ambit/database/ambitdemo_create.sql");
            if (dbc.open(false)) {
                System.out.println("successfully connected");
                assertFalse(dbc.databaseExist(db));
                
                try {
	                dbc.createDatabase(db);
	                assertTrue(dbc.databaseExist(db));
	                
	                System.out.println(db + "\t created");
	                dbc.createTables(db);
					AmbitUser user = new AmbitUser("guest");
					dbc.createUsers(db, user);
                } catch (AmbitException x) {
                    x.printStackTrace();
                    System.out.println(db + "\t rollback");
                    dbc.dropDatabase(db);
                    fail();
                }
                Hashtable tables = new Hashtable();


                String[] ts = new String[] {"substance","structure","alias","ambituser","author","cas",
                        "datasets","ddictionary","descrgroups","dgroup","dict_user"
                        ,"dsname","dvalues","fp1024","fpae","fpaeid","gamut","journal"
                        ,"literature","localdvalues","modeltype","name","qsar_user","qsardata"
                        ,"qsardescriptors","qsars","query","ref_authors","species","src_dataset"
                        ,"struc_dataset","struc_user","structure","study","stype","substance"
                        ,"testedstrucs","testresults","timings" };
                for (int i=0; i < ts.length;i++)
                    tables.put(ts[i],new Boolean(false));
                try {
                    int n = dbc.tablesExist(tables,true);
                    System.out.println(n + "\ttables OK");
                } catch (AmbitException x) {
                    System.out.println(x.getMessage());
                }
                
                dbc.dropDatabase(db);
                assertFalse(dbc.databaseExist(db));
            }
            dbc.close();
        } catch (AmbitException x) {
            x.printStackTrace();
            fail();
        } catch (SQLException x) {
            x.printStackTrace();
            fail();
        }
        try {
            ms.stopMySQL();
            System.out.println("stopped");
        } catch (AmbitException x) {
            x.printStackTrace();
            fail();
        }
    }

}
