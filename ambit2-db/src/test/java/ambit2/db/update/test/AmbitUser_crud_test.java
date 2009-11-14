/* AmbitUserCRUDTest.java
 * Author: nina
 * Date: Mar 28, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
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

package ambit2.db.update.test;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.AmbitUser;
import ambit2.base.data.AmbitUser.USER_TYPE;
import ambit2.db.update.IQueryUpdate;
import ambit2.db.update.user.CreateUser;
import ambit2.db.update.user.DeleteUser;
import ambit2.db.update.user.UpdateUser;

public class AmbitUser_crud_test extends CRUDTest<Object,AmbitUser> {

	@Override
	protected IQueryUpdate<Object,AmbitUser> createQuery() throws Exception {
		AmbitUser auser = new AmbitUser();
		auser.setName("ambit");
		auser.setAddress("address");
		auser.setAffiliation("affiliation");
		auser.setCountry("country");
		auser.setEmail("email");
		auser.setPassword("password");
		auser.setFirstName("firstName");
		auser.setLastName("lastName");
		auser.setType(USER_TYPE.Admin);
		CreateUser user = new CreateUser();
		user.setObject(auser);
		return user;
	}

	@Override
	public void testCreate() throws Exception {
			//	super.testCreate();
	}
	@Override
	protected void createVerify(IQueryUpdate<Object,AmbitUser> query) throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED_USER","SELECT * FROM users where user_name='ambit'");
		Assert.assertEquals(1,table.getRowCount());
		table = c.createQueryTable("EXPECTED_USERROLE","SELECT * FROM user_roles where user_name='ambit' and role_name='Admin'");
		Assert.assertEquals(1,table.getRowCount());		
		c.close();
	}

	@Override
	protected IQueryUpdate<Object,AmbitUser> deleteQuery() throws Exception {
		AmbitUser auser = new AmbitUser();
		auser.setName("admin");		
		return new DeleteUser(auser);
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object,AmbitUser> query) throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED_USER","SELECT * FROM users where user_name='admin'");
		Assert.assertEquals(0,table.getRowCount());
		table = c.createQueryTable("EXPECTED_USERROLE","SELECT * FROM user_roles where user_name='admin'");
		Assert.assertEquals(0,table.getRowCount());		
		c.close();
	}

	@Override
	protected IQueryUpdate<Object,AmbitUser> updateQuery() throws Exception {
		AmbitUser auser = new AmbitUser();
		auser.setName("admin");
		auser.setFirstName("Nina");
		auser.setLastName("Nikolova");
		return new UpdateUser(auser);
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,AmbitUser> query) throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED_USER","SELECT * FROM users where user_name='admin' and firstname='Nina' and lastname='Nikolova'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}

	@Override
	protected IQueryUpdate<Object, AmbitUser> createQueryNew() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, AmbitUser> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void testCreateNew() throws Exception {
	}
}
