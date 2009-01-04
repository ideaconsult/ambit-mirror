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

import java.sql.Connection;
import java.sql.ResultSet;

import org.junit.Test;

import ambit2.db.DatasourceFactory;
import ambit2.db.readers.RetrieveFieldNames;
import ambit2.db.search.QueryExecutor;
import ambit2.db.test.RepositoryTest;

/**
 * TODO dbunit test
 * @author nina
 *
 */
public class RetrieveFieldNamesTest extends RepositoryTest {
	protected RetrieveFieldNames query;
	@Override
	protected void setUp() throws Exception {
		datasource = DatasourceFactory.getDataSource(
				DatasourceFactory.getConnectionURI(
						"jdbc:mysql", 
						"localhost", "33060", "ambit2", "guest","guest" ));
		query = new RetrieveFieldNames();
	}
	@Test
	public void testGetParameters() throws Exception {
		assertNull(query.getParameters());
	}

	@Test
	public void testGetSQL() throws Exception {
		assertEquals("select idfieldname,name from field_names", query.getSQL());
	}

	@Test
	public void testGetObject() throws Exception {
		QueryExecutor<RetrieveFieldNames> qe = new QueryExecutor<RetrieveFieldNames>();
		Connection c = datasource.getConnection();
		qe.setConnection(c);
		ResultSet rs = qe.process(query);
		
		while (rs.next()) {
			System.out.println(query.getObject(rs));
		}
		rs.close();
		qe.close();
		if (!c.isClosed())
			c.close();
	}	

	@Test
	public void testGetFieldID() {
		assertEquals("idfieldname", query.getFieldID());
	}

	@Test
	public void testGetValueID() {
		assertEquals("name", query.getValueID());
	}

	@Test
	public void testGetFieldType() throws Exception {
		assertEquals(String.class,query.getFieldType());
	}

	@Test
	public void testGetValueType() {
		assertEquals(String.class,query.getValueType());
	}

}
