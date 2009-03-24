/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.db.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import ambit2.base.data.AmbitUser;

public class DbDeleteUser extends AbstractRepositoryWriter<AmbitUser,AmbitUser> {
	
	protected PreparedStatement deleteUser ;
	protected PreparedStatement deleteMySQLUser ;
	/**
	 * 
	 */
	private static final long serialVersionUID = 8403776373884865133L;

	@Override
	protected void prepareStatement(Connection connection) throws SQLException {
		deleteUser = connection.prepareStatement("delete from users where user_name=?");
		deleteMySQLUser = connection.prepareStatement("DELETE FROM mysql.user WHERE User = ?");
	}

	@Override
	public AmbitUser write(AmbitUser user) throws SQLException {
		deleteUser.setString(1, user.getName());
		deleteMySQLUser.setString(1, user.getName());
		deleteUser.executeUpdate();		
		deleteMySQLUser.executeUpdate();
        Statement st = getConnection().createStatement();
        st.executeUpdate("FLUSH PRIVILEGES");
		return user;
	}
}
