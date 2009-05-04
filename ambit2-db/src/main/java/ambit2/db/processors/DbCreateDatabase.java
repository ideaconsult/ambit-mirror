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
        	"insert into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values (\"guest\",\"084e0343a0486ff05530df6c705c8bb4\",\"guest\",\"Default guest user\",now(),\"confirmed\",\"guest\",\"http://ambit.acad.bg\");",
        	"insert into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values (\"admin\",\"21232f297a57a5a743894a0e4a801fc3\",\"admin\",\"Default admin user\",now(),\"confirmed\",\"admin\",\"http://ambit.acad.bg\");",
        	"insert into user_roles (user_name,role_name) values (\"guest\",\"ambit_guest\");",
        	"insert into user_roles (user_name,role_name) values (\"admin\",\"ambit_admin\");",
        	"REVOKE ALL PRIVILEGES ON `"+database+"`.* FROM 'admin'@'localhost';",
        	"REVOKE ALL PRIVILEGES ON `"+database+"`.* FROM 'guest'@'localhost';",

        	"GRANT USAGE ON `"+database+"`.* TO 'admin'@'localhost' IDENTIFIED BY PASSWORD '*4ACFE3202A5FF5CF467898FC58AAB1D615029441';",
        	"GRANT ALL PRIVILEGES ON `"+database+"`.* TO 'admin'@'localhost' WITH GRANT OPTION;",
        	"GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON `"+database+"`.* TO 'guest'@'localhost' IDENTIFIED BY PASSWORD '*11DB58B0DD02E290377535868405F11E4CBEFF58';"

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
                        if (line.trim().toUpperCase().startsWith("END")) {
                            table.append("END");
                            t.execute(table.toString());
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
				
                            
                            t.execute(table.toString());
                            
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
    public synchronized String getSQLFile() {
        return SQLFile;
    }
    public synchronized void setSQLFile(String file) {
        SQLFile = file;
    }
}
