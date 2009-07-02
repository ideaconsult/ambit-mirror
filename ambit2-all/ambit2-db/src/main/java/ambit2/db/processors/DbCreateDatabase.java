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

/*
 cant' execute it via jdbc ...
DROP FUNCTION IF EXISTS `sortString`;

DELIMITER $
CREATE FUNCTION `sortString`(inString TEXT) RETURNS TEXT
BEGIN
  DECLARE delim CHAR(1) DEFAULT ','; -- delimiter 
  DECLARE strings INT DEFAULT 0;     -- number of substrings
  DECLARE forward INT DEFAULT 1;     -- index for traverse forward thru substrings
  DECLARE backward INT;   -- index for traverse backward thru substrings, position in calc. substrings
  DECLARE remain TEXT;               -- work area for calc. no of substrings
  DECLARE swap1 TEXT;                 -- left substring to swap
  DECLARE swap2 TEXT;                 -- right substring to swap
  SET remain = inString;
  SET backward = LOCATE(delim, remain);
  WHILE backward != 0 DO
    SET strings = strings + 1;
    SET backward = LOCATE(delim, remain);
    SET remain = SUBSTRING(remain, backward+1);
  END WHILE;
  IF strings < 2 THEN RETURN inString; END IF;
  REPEAT
    SET backward = strings;
    REPEAT
      SET swap1 = SUBSTRING_INDEX(SUBSTRING_INDEX(inString,delim,backward-1),delim,-1);
      SET swap2 = SUBSTRING_INDEX(SUBSTRING_INDEX(inString,delim,backward),delim,-1);
      IF  swap1 > swap2 THEN
        SET inString = TRIM(BOTH delim FROM CONCAT_WS(delim
        ,SUBSTRING_INDEX(inString,delim,backward-2)
        ,swap2,swap1
        ,SUBSTRING_INDEX(inString,delim,(backward-strings))));
      END IF;
      SET backward = backward - 1;
    UNTIL backward < 2 END REPEAT;
    SET forward = forward +1;
  UNTIL forward + 1 > strings
  END REPEAT;
RETURN inString;
END $
DELIMITER ;
 */

public class DbCreateDatabase extends AbstractRepositoryWriter<StringBean,String> {
	
    /**
	 */
	private static final long serialVersionUID = -335737998721944578L;
	protected String SQLFile = "ambit2/db/sql/create_tables.sql";

	public DbCreateDatabase() {
		setOperation(OP.CREATE);
	}
    @Override
    protected void prepareStatement(Connection connection) throws SQLException {
        // TODO Auto-generated method stub

    }
    @Override
    public String create(StringBean database) throws SQLException {
        createDatabase(database.toString());
        createTables(database.toString());
        
        try {
        	String[] users = {
        	"insert into roles (role_name) values (\"ambit_guest\");",
        	"insert into roles (role_name) values (\"ambit_admin\");",
        	"insert into roles (role_name) values (\"ambit_quality\");",
        	"insert into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values (\"guest\",\"084e0343a0486ff05530df6c705c8bb4\",\"guest\",\"Default guest user\",now(),\"confirmed\",\"guest\",\"http://ambit.sourceforge.net\");",
        	"insert into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values (\"admin\",\"21232f297a57a5a743894a0e4a801fc3\",\"admin\",\"Default admin user\",now(),\"confirmed\",\"admin\",\"http://ambit.sourceforge.net\");",
        	"insert into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values (\"quality\",\"d66636b253cb346dbb6240e30def3618\",\"quality\",\"Automatic quality verifier\",now(),\"confirmed\",\"quality\",\"http://ambit.sourceforge.net\");",
        	"insert into user_roles (user_name,role_name) values (\"guest\",\"ambit_guest\");",
        	"insert into user_roles (user_name,role_name) values (\"admin\",\"ambit_admin\");",
        	"insert into user_roles (user_name,role_name) values (\"quality\",\"ambit_quality\");",
        	
        	"REVOKE ALL PRIVILEGES ON `"+database+"`.* FROM 'admin'@'localhost';",
        	"REVOKE ALL PRIVILEGES ON `"+database+"`.* FROM 'guest'@'localhost';",

        	"GRANT USAGE ON `"+database+"`.* TO 'admin'@'localhost' IDENTIFIED BY PASSWORD '*4ACFE3202A5FF5CF467898FC58AAB1D615029441';",
        	"GRANT ALL PRIVILEGES ON `"+database+"`.* TO 'admin'@'localhost' WITH GRANT OPTION;",
        	"GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON `"+database+"`.* TO 'guest'@'localhost' IDENTIFIED BY PASSWORD '*11DB58B0DD02E290377535868405F11E4CBEFF58';",
        	"GRANT EXECUTE FUNCTION sortString ON `"+database+"`.* TO 'guest'@'localhost';"

        	};
	        Statement st = connection.createStatement();
	        for (String user : users)
	        	st.addBatch(user);
	        st.executeBatch();
	        try {
        	Preferences.setProperty(Preferences.DATABASE, database.toString());
        	Preferences.saveProperties(getClass().getName());
	        } catch (Exception x) {}
        } catch (SQLException x) {
        	logger.warn(x);
        }
        try {
        	createFunctions();
        } catch (Exception x) {
        	logger.warn(x);
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
                            logger.debug(table.toString());
                            //t.execute(table.toString());
                            table = new StringBuffer();                             
                            continue;
                        }
                        
                        if (line.trim().toUpperCase().startsWith("END "+delimiter)) {
                            table.append("END");
                            int ok = t.executeUpdate(table.toString());
                            logger.debug(table.toString());
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
                            logger.debug(table.toString());
                            t.executeUpdate(table.toString());
                            
                            logger.debug(table.toString());
                            table = new StringBuffer();
                        }

                }
                //t.executeBatch();
                in.close();
                reader.close();
        } catch (IOException x) {
            throw new SQLException(x.getMessage());
        }
    }
    public void createFunctions() throws SQLException {
    	String[] func = {
    	"DROP FUNCTION IF EXISTS `sortString`",
    	
    	"CREATE FUNCTION `sortString`(inString TEXT) RETURNS TEXT\n"+
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
