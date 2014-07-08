/* Dictionary_crud_test.java
 * Author: nina
 * Date: Mar 31, 2009
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
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.Dictionary;
import ambit2.db.update.dictionary.CreateDictionary;
import ambit2.db.update.dictionary.DeleteDictionary;
import ambit2.db.update.dictionary.UpdateDictionary;

public class Dictionary_crud_test extends  CRUDTest<Object,Dictionary> {

	@Override
	protected IQueryUpdate<Object,Dictionary> createQuery() throws Exception {
		Dictionary d = new Dictionary();
		d.setTemplate("template");
		d.setParentTemplate("parentTemplate");
		CreateDictionary user = new CreateDictionary();
		user.setObject(d);
		return user;
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,Dictionary> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM template where name='template'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM template where name='parentTemplate'");
		Assert.assertEquals(1,table.getRowCount());
		
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM dictionary as d join template as t1 on d.idsubject=t1.idtemplate join template as t2 on d.idobject=t2.idtemplate where t2.name='parentTemplate' and t1.name='template'");
		Assert.assertEquals(1,table.getRowCount());
		
		c.close();	
		
	}

	@Override
	protected IQueryUpdate<Object,Dictionary> deleteQuery() throws Exception {
		Dictionary d = new Dictionary();
		d.setTemplate("Octanol-water partition coefficient (Kow)");
		d.setParentTemplate("Physicochemical effects");
		DeleteDictionary user = new DeleteDictionary();
		user.setObject(d);
		return user;
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object,Dictionary> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM template where name='Octanol-water partition coefficient (Kow)'");
		Assert.assertEquals(1,table.getRowCount());
		
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM template where name='Physicochemical effects'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM dictionary as d join template as t1 on d.idsubject=t1.idtemplate join template as t2 on d.idobject=t2.idtemplate where t2.name='Physicochemical effects' and t1.name='Octanol-water partition coefficient (Kow)'");
		c.close();	
		
	}
	@Test
	public void testUpdate() throws Exception {
		
	}
	@Override
	protected IQueryUpdate<Object,Dictionary> updateQuery() throws Exception {
		return new UpdateDictionary();
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,Dictionary> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IQueryUpdate<Object, Dictionary> createQueryNew()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, Dictionary> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void testCreateNew() throws Exception {
	}
}
