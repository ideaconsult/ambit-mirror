/* RetrieveFieldNamesTest.java
 * Author: nina
 * Date: Dec 29, 2008
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Ideaconsult Ltd.
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

import ambit2.db.readers.IRetrieval;
import ambit2.db.readers.RetrieveFieldNames;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.QueryExecutor;

/**
 * A dbunit test for {@link RetrieveFieldNames}
 * @author nina
 *
 */
public class RetrieveFieldNamesTest extends RetrieveTest<String> {

	@Override
	protected IRetrieval<String> createQuery() {
		return new RetrieveFieldNames();
	}
	@Test
	public void testGetParameters() throws Exception {
		Assert.assertNull(((IQueryObject)query).getParameters());
	}

	@Test
	public void testGetSQL() throws Exception {
		Assert.assertEquals("select idfieldname,name from field_names", ((IQueryObject)query).getSQL());
	}

	@Test
	public void testGetObject() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/dataset-properties.xml");

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM FIELD_NAMES");		
		Assert.assertEquals(36,names.getRowCount());

		QueryExecutor<RetrieveFieldNames> qe = new QueryExecutor<RetrieveFieldNames>();		
		qe.setConnection(c.getConnection());
		ResultSet rs = qe.process((RetrieveFieldNames)query);
		
		while (rs.next()) {
			names = 	c.createQueryTable("EXPECTED_NAME","SELECT * FROM FIELD_NAMES where name='"+query.getObject(rs)+"'");		
			Assert.assertEquals(1,names.getRowCount());
		}
		rs.close();
		qe.close();
		c.close();
	}	

	@Test
	public void testGetFieldID() {
		Assert.assertEquals("idfieldname", ((RetrieveFieldNames)query).getFieldID());
	}

	@Test
	public void testGetValueID() {
		Assert.assertEquals("name", ((RetrieveFieldNames)query).getValueID());
	}

	@Test
	public void testGetFieldType() throws Exception {
		Assert.assertEquals(String.class,((RetrieveFieldNames)query).getFieldType());
	}

	@Test
	public void testGetValueType() {
		Assert.assertEquals(String.class,((RetrieveFieldNames)query).getValueType());
	}

}
