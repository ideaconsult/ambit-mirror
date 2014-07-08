/* TemplateProperty_crud_test.java
 * Author: nina
 * Date: Apr 1, 2009
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

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.db.update.dictionary.TemplateAddProperty;
import ambit2.db.update.dictionary.TemplateDeleteProperty;

public class TemplateProperty_crud_test extends CRUDTest<Dictionary,Property>{

	@Override
	protected IQueryUpdate<Dictionary, Property> createQuery() throws Exception {
		Dictionary d = new Dictionary("template","parenttemplate");
		Property p = Property.getInstance("Property 1","Dummy reference","NA");
		return new TemplateAddProperty(d,p);
	}

	@Override
	protected void createVerify(IQueryUpdate<Dictionary, Property> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM template where name='template'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM template where name='parenttemplate'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM dictionary as d join template as t1 on d.idsubject=t1.idtemplate join template as t2 on d.idobject=t2.idtemplate where t2.name='parenttemplate' and t1.name='template'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM catalog_references where title='Dummy reference' and url='NA'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where name='Property 1' and title='Dummy reference'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM template join template_def using(idtemplate) join properties using(idproperty) where properties.name='Property 1' and template.name='template'");
		Assert.assertEquals(1,table.getRowCount());				
		c.close();
		
	}	
	@Override
	protected IQueryUpdate<Dictionary, Property> createQueryNew()
			throws Exception {
		Dictionary d = new Dictionary("template","parenttemplate");
		Property p = Property.getInstance("property","newtitle","newurl");
		return new TemplateAddProperty(d,p);
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Dictionary, Property> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM template where name='template'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM template where name='parenttemplate'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM dictionary as d join template as t1 on d.idsubject=t1.idtemplate join template as t2 on d.idobject=t2.idtemplate where t2.name='parenttemplate' and t1.name='template'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM catalog_references where title='newtitle' and url='newurl'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where name='property' and title='newtitle'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM template join template_def using(idtemplate) join properties using(idproperty) where properties.name='property' and template.name='template'");
		Assert.assertEquals(1,table.getRowCount());				
		c.close();
	}


	@Override
	protected IQueryUpdate<Dictionary, Property> deleteQuery() throws Exception {
		Dictionary d = new Dictionary("BCF","parenttemplate");
		Property p = Property.getInstance("Property 1","Dummy reference","NA");
		return new TemplateDeleteProperty(d,p);
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Dictionary, Property> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM template where name='BCF'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM catalog_references where title='Dummy reference' and url='NA'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where name='Property 1' and title='Dummy reference'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM template join template_def using(idtemplate) join properties using(idproperty) where properties.name='Property 1' and template.name='BCF'");
		Assert.assertEquals(0,table.getRowCount());				
		c.close();
		
	}

	@Override
	protected IQueryUpdate<Dictionary, Property> updateQuery() throws Exception {

		return null;
	}

	@Override
	protected void updateVerify(IQueryUpdate<Dictionary, Property> query)
			throws Exception {

		
	}
	@Override
	public void testUpdate() throws Exception {
		//not supported
	}

}
