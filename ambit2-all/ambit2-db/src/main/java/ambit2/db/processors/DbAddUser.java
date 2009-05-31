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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.OperationNotSupportedException;

import ambit2.base.data.AmbitUser;
import ambit2.base.data.AmbitUser.USER_TYPE;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.LoginInfo;
import ambit2.db.search.QueryUser;
import ambit2.db.update.user.CreateUser;

public class DbAddUser extends AbstractRepositoryWriter<AmbitUser,AmbitUser> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1394498894723529507L;
	protected CreateUser createUser = new CreateUser();
    protected QueryUser queryUser = new QueryUser();
	
    @Override
    protected void prepareStatement(Connection connection) throws SQLException {
    	
    }
    @Override
    public AmbitUser read(AmbitUser user) throws SQLException,
    		OperationNotSupportedException,AmbitException {
    	
    	queryUser.setFieldname("user_name");
    	queryUser.setValue(user.getName());
    	ResultSet rs = null;
    	try {
    		queryexec.setConnection(connection);
    		rs = queryexec.process(queryUser);
    		while (rs.next()) {
    			user = queryUser.getObject(rs);
    		}
    	} catch (SQLException x) {
    		throw x;
    	} catch (AmbitException x) {
    		throw x;
    	} catch (Exception x) {
    		throw new AmbitException(x);
    	} finally  {
    		queryexec.closeResults(rs);
    	}
    	return user;
    }
    

    @Override
    public AmbitUser create(AmbitUser user) throws SQLException, AmbitException {
    	createUser.setObject(user);
    	exec.process(createUser);
        
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
            st.executeBatch();
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

      
    
}
