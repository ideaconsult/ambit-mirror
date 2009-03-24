/* TemplateWriterTest.java
 * Author: nina
 * Date: Feb 7, 2009
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

package ambit2.db.processors.test;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.base.data.Dictionary;
import ambit2.db.processors.TemplateWriter;

public class TemplateWriterTest extends DbUnitTest  {
	protected TemplateWriter writer;
	@Before
	public void setUp() throws Exception {
		writer = new TemplateWriter(); 
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() throws Exception  {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");			
        IDatabaseConnection c = getConnection();
		ITable templates = 	c.createQueryTable("EXPECTED_TEMPLATES","SELECT * FROM template");	
		Assert.assertEquals(5,templates.getRowCount());

		ITable dictionary = c.createQueryTable("EXPECTED_ONTOLOGY","SELECT * FROM dictionary d JOIN template t1 on d.idsubject=t1.idtemplate JOIN template t2 on d.idobject=t2.idtemplate WHERE t1.name= \"Skin sensitisation\" and t2.name=\"Endpoints\" ");	
		Assert.assertEquals(0,dictionary.getRowCount());		
		
        writer.setConnection(c.getConnection());
        writer.open();
        
        Dictionary d = new Dictionary("Skin sensitisation","Endpoints");
        writer.write(d);
        
		dictionary = 	c.createQueryTable("EXPECTED_ONTOLOGY","SELECT * FROM dictionary d JOIN template t1 on d.idsubject=t1.idtemplate JOIN template t2 on d.idobject=t2.idtemplate WHERE t1.name= \"Skin sensitisation\" and t2.name=\"Endpoints\" ");	
		Assert.assertEquals(1,dictionary.getRowCount());
		
        d = new Dictionary("LLNA","Skin sensitisation");
        writer.write(d);		
		dictionary = 	c.createQueryTable("EXPECTED_ONTOLOGY","SELECT * FROM dictionary d JOIN template t1 on d.idsubject=t1.idtemplate JOIN template t2 on d.idobject=t2.idtemplate WHERE t2.name= \"Skin sensitisation\" and t1.name=\"LLNA\" ");	
		Assert.assertEquals(1,dictionary.getRowCount());
		
        writer.close();
        c.close();
	}

}
