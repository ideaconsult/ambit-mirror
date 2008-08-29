/* DbAddUser.java
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import ambit2.core.data.AmbitUser;
import ambit2.core.data.AmbitUser.USER_TYPE;
import ambit2.db.LoginInfo;

public class DbAddUser extends AbstractRepositoryWriter<AmbitUser,AmbitUser> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1394498894723529507L;
	protected PreparedStatement insertRoles;
    protected PreparedStatement insertUser;
    protected PreparedStatement insertUserRoles;

    
    @Override
    protected void prepareStatement(Connection connection) throws SQLException {
        insertRoles = connection.prepareStatement("insert ignore into roles (role_name) values (?)");
        insertUser = connection.prepareStatement("insert into users (user_name,password,email,firstname,lastname,registration_date,registration_status,webpage) values (?,MD5(?),?,?,?,now(),\"confirmed\",?) ON DUPLICATE KEY UPDATE password=MD5(?)");
        insertUserRoles = connection.prepareStatement("insert ignore into user_roles (user_name,role_name) values (?,?)");

    }
    @Override
    public AmbitUser write(AmbitUser user) throws SQLException {
        String role = "ambit_"+user.getType();
        insertRoles.setString(1, role);
        insertUser.setString(1, user.getName());
        insertUser.setString(2, user.getPassword());
        insertUser.setString(3, user.getEmail());
        insertUser.setString(4, user.getFirstName());
        insertUser.setString(5, user.getLastName());
        insertUser.setString(6, user.getWww());
        insertUser.setString(7, user.getPassword());
        insertUserRoles.setString(1, user.getName());
        insertUserRoles.setString(2, role);

        insertRoles.executeUpdate();
        insertUser.executeUpdate();
        insertUserRoles.executeUpdate();
        
        try {
            LoginInfo li = new LoginInfo();
            li.setURI(getConnection());

            try {
            Statement st = getConnection().createStatement();
            st.addBatch("REVOKE ALL PRIVILEGES ON "+ connection.getCatalog() + ".* FROM '"+user.getName()+"'@'%'");
            } catch (Exception xx) {
                logger.warn(xx);
            }
            Statement st = getConnection().createStatement();
            if (user.getType().equals(USER_TYPE.Admin)) {
                st.addBatch("GRANT USAGE ON "+ connection.getCatalog() + ".* TO  '"+user.getName()+"'@'%' IDENTIFIED BY '"+user.getPassword()+"'");
                st.addBatch("GRANT ALL PRIVILEGES ON "+ connection.getCatalog() + ".* TO  '"+user.getName()+"'@'%' WITH GRANT OPTION");
            } else
                st.addBatch("GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON "+ connection.getCatalog() + ".* TO  '"+user.getName()+"'@'%' IDENTIFIED BY '"+user.getPassword()+"'");
            st.addBatch("flush privileges");
            //System.out.println(st.getWarnings());
            st.executeBatch();
            //System.out.println(st.getWarnings());
        } catch (Exception x) {
            throw new SQLException(x.getMessage());
        }
        /*
        REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'admin'@'localhost';
        REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'guest'@'localhost';
        GRANT USAGE ON ambit2.* TO 'admin'@'localhost' IDENTIFIED BY PASSWORD '*4ACFE3202A5FF5CF467898FC58AAB1D615029441';
        GRANT ALL PRIVILEGES ON `ambit2`.* TO 'admin'@'localhost' WITH GRANT OPTION;
        GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON `ambit2`.* TO 'guest'@'localhost' IDENTIFIED BY PASSWORD '*11DB58B0DD02E290377535868405F11E4CBEFF58';
        */
        
        return user;
    }
        @Override
        public void close() throws SQLException {
           if (insertRoles != null) insertRoles.close();
           if (insertUser != null) insertUser.close();
           if (insertUserRoles != null) insertUserRoles.close();
            super.close();
        }
    
}
