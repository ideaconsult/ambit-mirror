/* SourceDatasetRowsTest.java
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

package ambit2.db.readers.test;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;

import ambit2.db.SourceDataset;
import ambit2.db.SourceDatasetRows;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.readers.RetrieveDatasets;

public class SourceDatasetRowsTest extends DbUnitTest {
	@Test
    public void testSourceDatasetRows() throws Exception {
        SourceDatasetRows rows = new SourceDatasetRows();
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");

		IDatabaseConnection c = getConnection();        
        RetrieveDatasets query = new RetrieveDatasets();
        query.setValue(null);
        rows.setConnection(c.getConnection());        
        rows.setQuery(query);
 
        while (rows.next()) {
            SourceDataset d = rows.getObject();
            Assert.assertTrue(d.getId() > 0);
            Assert.assertTrue(d.getReference().getId() > 0);
        }
        rows.close();
        c.close();
    }	

}
