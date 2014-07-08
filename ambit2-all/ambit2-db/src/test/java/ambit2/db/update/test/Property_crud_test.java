/* Property_crud_test.java
 * Author: nina
 * Date: Mar 30, 2009
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

import ambit2.base.data.Property;
import ambit2.db.update.property.CreateProperty;
import ambit2.db.update.property.DeleteProperty;
import ambit2.db.update.property.UpdateProperty;

public class Property_crud_test  extends CRUDTest<Object,Property> {

	@Override
	protected IQueryUpdate<Object,Property> createQuery() throws Exception {
		Property p = Property.getInstance("new property","newtitle","newurl");
		p.setLabel("new label");
		p.setId(-1);
		p.setClazz(Number.class);
		return new CreateProperty(p);
	}

	@Override
	protected void createVerify(IQueryUpdate<Object,Property> query) throws Exception {
		Assert.assertTrue(query.getObject().getId()>0);
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM catalog_references where title='newtitle' and url='newurl'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where title='newtitle' and name='new property' and ptype='NUMERIC'");
		Assert.assertEquals(1,table.getRowCount());
		
		c.close();	
	}

	@Override
	protected IQueryUpdate<Object,Property> deleteQuery() throws Exception {
		Property property = new Property("");	
		property.setId(1);
		return new DeleteProperty(property);
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object,Property> query) throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties where idproperty=1");
		Assert.assertEquals(0,table.getRowCount());

		c.close();
		
	}

	@Override
	protected IQueryUpdate<Object,Property> updateQuery() throws Exception {

		Property property = Property.getInstance("My new property","Dummy reference");
		property.setId(1);		
		property.setLabel("My new label");
		property.setClazz(Number.class);
		return new UpdateProperty(property);
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object,Property> query) throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties where idproperty=1 and name='My new property' and comments='My new label' and ptype='NUMERIC'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();	}

	@Override
	protected IQueryUpdate<Object, Property> createQueryNew() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, Property> query)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void testCreateNew() throws Exception {
	}
}
