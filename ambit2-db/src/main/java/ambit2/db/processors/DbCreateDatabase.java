/* DbCreateDatabase.java
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

package ambit2.db.processors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import ambit2.base.config.Preferences;
import ambit2.base.data.StringBean;


public class DbCreateDatabase extends AbstractRepositoryWriter<StringBean,String> {
	protected String adminPass = null;
	public String getAdminPass() {
		return adminPass;
	}
	public void setAdminPass(String adminPass) {
		this.adminPass = adminPass;
	}
	protected String userPass = null;
	
   	public String getUserPass() {
		return userPass;
	}
	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}
	public static String[] func = {
   	    	"DROP FUNCTION IF EXISTS `sortstring`",
   	    	
   	    	"CREATE FUNCTION `sortstring`(inString TEXT) RETURNS TEXT deterministic\n"+
   	    	"BEGIN\n"+
   	    	"  DECLARE delim CHAR(1) DEFAULT ','; -- delimiter\n"+ 
   	    	"  DECLARE strings INT DEFAULT 0;     -- number of substrings\n"+
   	    	"  DECLARE forward INT DEFAULT 1;     -- index for traverse forward thru substrings\n"+
   	    	"  DECLARE backward INT;   -- index for traverse backward thru substrings, position in calc. substrings\n"+
   	    	"  DECLARE remain TEXT;               -- work area for calc. no of substrings\n"+
   	    	"  DECLARE swap1 TEXT;                 -- left substring to swap\n"+
   	    	"  DECLARE swap2 TEXT;                 -- right substring to swap\n"+
   	    	"  SET remain = inString;\n"+
   	    	"  SET backward = LOCATE(delim, remain);\n"+
   	    	"  WHILE backward != 0 DO\n"+
   	    	"    SET strings = strings + 1;\n"+
   	    	"    SET backward = LOCATE(delim, remain);\n"+
   	    	"    SET remain = SUBSTRING(remain, backward+1);\n"+
   	    	"  END WHILE;\n"+
   	    	"  IF strings < 2 THEN RETURN inString; END IF;\n"+
   	    	"  REPEAT\n"+
   	    	"    SET backward = strings;\n"+
   	    	"    REPEAT\n"+
   	    	"      SET swap1 = SUBSTRING_INDEX(SUBSTRING_INDEX(inString,delim,backward-1),delim,-1);\n"+
   	    	"      SET swap2 = SUBSTRING_INDEX(SUBSTRING_INDEX(inString,delim,backward),delim,-1);\n"+
   	    	"      IF  swap1 > swap2 THEN\n"+
   	    	"        SET inString = TRIM(BOTH delim FROM CONCAT_WS(delim\n"+
   	    	"        ,SUBSTRING_INDEX(inString,delim,backward-2)\n"+
   	    	"        ,swap2,swap1\n"+
   	    	"        ,SUBSTRING_INDEX(inString,delim,(backward-strings))));\n"+
   	    	"      END IF;\n"+
   	    	"      SET backward = backward - 1;\n"+
   	    	"    UNTIL backward < 2 END REPEAT;\n"+
   	    	"    SET forward = forward +1;\n"+
   	    	"  UNTIL forward + 1 > strings\n"+
   	    	"  END REPEAT;\n"+
   	    	"RETURN inString;\n"+
   	    	"END\n"};
    /**
	 */
	private static final long serialVersionUID = -335737998721944578L;
	protected String SQLFile = "ambit2/db/sql/create_tables.sql";
	
	public DbCreateDatabase() {
		this("guest","guest");
	}
	public DbCreateDatabase(String guestpass, String adminpass) {
		this("guest",guestpass,"admin",adminpass);
	}
	public DbCreateDatabase(String guest,String guestpass, String admin, String adminpass) {
		setOperation(OP.CREATE);

		this.adminPass = adminpass;
        this.userPass = guestpass;
	
	}
    @Override
    protected void prepareStatement(Connection connection) throws SQLException {
        // TODO Auto-generated method stub

    }
    
    @Override
    public String create(StringBean database) throws SQLException {
        createDatabase(database.toString());
        createTables(database.toString());
        
        String[] local = new String[] {"localhost","127.0.0.1","::1"};  
        /**
         * to work with this MySQL, one needs to have privileges for localhost & 127.0.0.1 
# Do not resolve host names when checking client connections.
# Use only IP addresses. If you use this option, all Host column values
# in the grant tables must be IP addresses or localhost.
skip-name-resolve
         */
        //TODO move this to the SQL script
        //TODO refactor to use existing empty db with rights assigned; not create it on the fly
        	String[] users = {
        	String.format("insert into ausers (user_name,email,lastname,keywords,homepage) values (\"guest\",\"guest\",\"Default guest user\",\"guest\",\"http://ambit.sourceforge.net\");"),
        	String.format("insert into ausers (user_name,email,lastname,keywords,homepage) values (\"admin\",\"admin\",\"Default admin user\",\"admin\",\"http://ambit.sourceforge.net\");"),
        	String.format("insert into ausers (user_name,email,lastname,keywords,homepage) values (\"quality\",\"quality\",\"Automatic quality verifier\",\"quality\",\"http://ambit.sourceforge.net\");"),
        	
        	String.format("GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON `%s`.* TO 'guest'@'%s' IDENTIFIED BY '%s';",database,"localhost",userPass),
        	String.format("GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON `%s`.* TO 'guest'@'%s' ;",database,"127.0.0.1"),
        	String.format("GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON `%s`.* TO 'guest'@'%s' ;",database,"::1"),
        	
        	};
	        Statement st = connection.createStatement();
	        
	        for (String user : users) try {
	        	st.executeUpdate(user);
	        } catch (Exception x) {
	        	logger.log(java.util.logging.Level.WARNING,user,x);
	        }
	        	
	        
	        st.close();
	        try {
        	Preferences.setProperty(Preferences.DATABASE, database.toString());
        	Preferences.saveProperties(getClass().getName());
	        } catch (Exception x) {}


        try {
        	createFunctions();
	        st = connection.createStatement();
	        for (String localAddr : local) {
		        st.executeQuery(String.format("GRANT EXECUTE ON FUNCTION sortstring TO 'guest'@'%s';",localAddr));
		        st.executeQuery(String.format("GRANT EXECUTE ON FUNCTION sql_xtab TO 'guest'@'%s';",localAddr));
		        st.executeQuery(String.format("GRANT EXECUTE ON PROCEDURE p_xtab TO 'guest'@'%s';",localAddr));
		        //chem space vis
		        st.executeQuery(String.format("GRANT EXECUTE ON PROCEDURE tanimotoBinnedSpace TO 'guest'@'%s';",localAddr));
		        st.executeQuery(String.format("GRANT EXECUTE ON FUNCTION tanimotoCell TO 'guest'@'%s';",localAddr));
		        st.executeQuery(String.format("GRANT EXECUTE ON FUNCTION tanimotoChemicals TO 'guest'@'%s';",localAddr));
		        st.executeQuery(String.format("GRANT EXECUTE ON FUNCTION getBinnedRange TO 'guest'@'%s';",localAddr));
		        st.executeQuery(String.format("GRANT EXECUTE ON FUNCTION getPropertyMax TO 'guest'@'%s';",localAddr));
		        st.executeQuery(String.format("GRANT EXECUTE ON FUNCTION getPropertyMin TO 'guest'@'%s';",localAddr));
		        
		        st.executeQuery(String.format("GRANT EXECUTE ON PROCEDURE setAtomEnvironment TO 'guest'@'%s';",localAddr));
		        st.executeQuery(String.format("GRANT SELECT ON `mysql`.`proc` TO 'guest'@'%s';",localAddr));
		        st.executeQuery(String.format("GRANT EXECUTE ON PROCEDURE findByProperty TO 'guest'@'%s';",localAddr));
		        st.executeQuery(String.format("GRANT EXECUTE ON PROCEDURE deleteDataset TO 'guest'@'%s';",localAddr));
		        
		        st.executeQuery(String.format("GRANT EXECUTE ON PROCEDURE createBundleCopy TO 'guest'@'%s';",localAddr));
		        st.executeQuery(String.format("GRANT EXECUTE ON PROCEDURE createBundleVersion TO 'guest'@'%s';",localAddr));
	        }
	         
        } catch (Exception x) {
        	logger.log(java.util.logging.Level.WARNING,x.getMessage(),x);
        }
        return database.toString();
    }
    public void createDatabase(String newDb) throws SQLException {
            Statement t = connection.createStatement();
            t.addBatch("DROP DATABASE IF EXISTS `"+newDb+"`");
            t.addBatch("CREATE SCHEMA IF NOT EXISTS `" +newDb + "` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ");
            t.addBatch("USE `"+newDb+"`");
            t.executeBatch();
            t.close();
    }
    /*
    public void createTables(String newDB) throws SQLException {
    	 InputStream in = this.getClass().getClassLoader().getResourceAsStream(
                 getSQLFile());
    	try {
	    	ScriptRunner script = new ScriptRunner(connection,false,false);
	    	
	    	script.runScript(new  BufferedReader(new InputStreamReader(in)));
    	} catch (IOException x) {
    		x.printStackTrace();
    		throw new SQLException(x.getMessage());
    	}
    	finally {
    		try {in.close(); } catch (Exception x) {x.printStackTrace();}
    	}
    	
    }
    */
    
    public void createTables(String newDB) throws SQLException {
        try {
        //if (this.getUser().getTitle().equals(AmbitUser.USERTYPE_ADMIN)) {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(
                    getSQLFile());
                  
            if (in == null) throw new SQLException("Can't find " + getSQLFile());
            
                Statement t = connection.createStatement();
                t.execute("USE `"+newDB + "`;");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = null;                         
                StringBuffer table = new StringBuffer();
                String delimiter = ";";
                while (true) {

                        line = reader.readLine();
                        if (line == null) break;
                        if (line.toUpperCase().startsWith("DELIMITER")) {
                            delimiter = line.substring(line.indexOf("DELIMITER")+10).trim();
                            //t.execute(table.toString());
                            table = new StringBuffer();                             
                            continue;
                        }
                        
                        if (line.trim().toUpperCase().startsWith("END "+delimiter)) {
                            table.append("END");
                            int ok = t.executeUpdate(table.toString());
                            table = new StringBuffer();                            
                            continue;
                        }                        
                        if (line == null) break;
                        if (line.trim().equals("")) continue;
                        if (line.indexOf("--") == 0) continue;
                        table.append(line);
                        table.append("\n");
                        if (line.indexOf(delimiter) >= 0) {
                            //t.addBatch(table.toString());
                            t.executeUpdate(table.toString());
                            table = new StringBuffer();
                        }

                }
                //t.executeBatch();
                in.close();
                reader.close();
        } catch (IOException x) {
            x.printStackTrace();
            throw new SQLException(x.getMessage());
        }
    }
    public void createFunctions() throws SQLException {
 
    	
    	
    	for (String f: func) {
    		Statement t = connection.createStatement();
    		t.executeUpdate(f);
    		t.close();
    	}
    	
    }
    public synchronized String getSQLFile() {
        return SQLFile;
    }
    public synchronized void setSQLFile(String file) {
        SQLFile = file;
    }
}
