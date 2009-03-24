/* RetrieveFieldTest.java
 * Author: nina
 * Date: Feb 8, 2009
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

package ambit2.db.readers.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.StructureRecord;
import ambit2.db.readers.IRetrieval;
import ambit2.db.readers.RetrieveField;
import ambit2.db.search.QueryExecutor;

public class RetrieveFieldTest extends RetrieveTest<String> {

	@Override
	protected IRetrieval<String> createQuery() {
		RetrieveField<String,String> q = new RetrieveField<String, String>();
		q.setValue(new StructureRecord(-1,100215,null,null));
		q.setFieldname("Property 1");
		return q;
	}
	@Test
	public void testGetObject() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/dataset-properties.xml");

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");		
		Assert.assertEquals(3,names.getRowCount());

		QueryExecutor<RetrieveField> qe = new QueryExecutor<RetrieveField>();		
		qe.setConnection(c.getConnection());

		ResultSet rs = qe.process((RetrieveField)query);
		
		int count = 0; 
		while (rs.next()) {
					
			System.out.println(query.getObject(rs));
			count++;
		}
		Assert.assertTrue(count>0);
		rs.close();
		qe.close();
		c.close();
	}	
}
