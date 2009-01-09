/* SourceDatasetWriterTest.java
 * Author: Nina Jeliazkova
 * Date: May 5, 2008 
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

package ambit2.db.processors.test;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.core.data.LiteratureEntry;
import ambit2.db.SourceDataset;
import ambit2.db.processors.DbSrcDatasetWriter;

/**
 * Test for {@link DbSrcDatasetWriter}
 * @author nina
 *
 */
public class DbSrcDatasetWriterTest extends DbUnitTest {
	@Test
    public void test() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
        IDatabaseConnection c = getConnection();
        
		ITable names = 	c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM src_dataset");
		Assert.assertEquals(0,names.getRowCount());
        DbSrcDatasetWriter writer = new DbSrcDatasetWriter();

        writer.setConnection(c.getConnection());
        writer.open();
        LiteratureEntry le = new LiteratureEntry("title","url");
        le.setId(-1);
        SourceDataset ds = new SourceDataset("My new dataset",le);
        ds.setId(-1);
        writer.write(ds);
        c.close();
        
        c = getConnection();
		names = c.createQueryTable("EXPECTED_DATASETS","SELECT name,user_name,title,url FROM src_dataset join catalog_references using(idreference) where name='My new dataset'");
		Assert.assertEquals(1,names.getRowCount());
		Assert.assertEquals("My new dataset",names.getValue(0,"name"));
		Assert.assertEquals("title",names.getValue(0,"title"));
		Assert.assertEquals("url",names.getValue(0,"url"));		
		Assert.assertEquals("guest",names.getValue(0,"user_name"));		
		c.close();
		
    }    

}
