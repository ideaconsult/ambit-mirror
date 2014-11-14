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
import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.IQueryRetrieval;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.Property;
import ambit2.db.results.AmbitRows;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.property.RetrieveFieldNames;

/**
 * A dbunit test for {@link RetrieveFieldNames}
 * @author nina
 *
 */
public class RetrieveFieldNamesTest extends RetrieveTest<Property> {

	@Override
	protected IQueryRetrieval<Property> createQuery() {
		RetrieveFieldNames q = new RetrieveFieldNames();
		return q;
	}
	@Test
	public void testGetParameters() throws Exception {
		Assert.assertNull(((IQueryObject)query).getParameters());
	}

	@Test
	public void testGetSQL() throws Exception {
		Assert.assertEquals(RetrieveFieldNames.base_sql, ((IQueryObject)query).getSQL());
	}

	@Override
	protected String getTestDatabase() {
		return "src/test/resources/ambit2/db/processors/test/dataset-properties.xml";
	}
	@Test
	public void testGetObject() throws Exception {
		setUpDatabase(getTestDatabase());

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");		
		Assert.assertEquals(4,names.getRowCount());

		QueryExecutor<RetrieveFieldNames> qe = new QueryExecutor<RetrieveFieldNames>();		
		qe.setConnection(c.getConnection());

		ResultSet rs = qe.process((RetrieveFieldNames)query);
		
		int count = 0; 
		while (rs.next()) {
			names = 	c.createQueryTable("EXPECTED_NAME","SELECT * FROM properties where name='"+query.getObject(rs)+"'");		
			Assert.assertEquals(1,names.getRowCount());
			count++;
		}
		Assert.assertEquals(4,count);
		rs.close();
		qe.close();
		c.close();
	}	
	
	@Test
	public void testGetObjectByName() throws Exception {
		setUpDatabase(getTestDatabase());

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");		
		Assert.assertEquals(4,names.getRowCount());

		QueryExecutor<RetrieveFieldNames> qe = new QueryExecutor<RetrieveFieldNames>();		
		qe.setConnection(c.getConnection());

		Property p = Property.getInstance("Property 1","CAS Registry Number");
		RetrieveFieldNames q = new RetrieveFieldNames();
		q.setFieldname("name");
		q.setValue(p);
		ResultSet rs = qe.process(q);
		
		int count = 0; 
		while (rs.next()) {
			
			names = 	c.createQueryTable("EXPECTED_NAME","SELECT * FROM properties where idproperty="+query.getObject(rs).getId());		
			Assert.assertEquals(1,names.getRowCount());
			count++;
		}
		Assert.assertEquals(1,count);
		rs.close();
		qe.close();
		c.close();
	}		

	@Test
	public void testGetFieldID() {
		Assert.assertEquals("idproperty", ((RetrieveFieldNames)query).getFieldID());
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
	@Override
	protected AmbitRows<Property> createRows() throws Exception {
		return new AmbitRows<Property>();
	}
	@Override
	protected void verifyRows(AmbitRows<Property> rows) throws Exception {
		IDatabaseConnection c = getConnection();
		Assert.assertNotNull(rows);
		Assert.assertEquals(4,rows.size());
		while (rows.next()) {
			Property p = rows.getObject();
			ITable table = 	c.createQueryTable("EXPECTED",
					"select idproperty,name,units,title,url,idreference,comments,ptype,islocal,type,rdf_type,predicate,object from properties join catalog_references using(idreference) left join property_annotation using(idproperty) where name='"+p.getName()+"' and title='"+p.getReference().getTitle()+"'");		
			Assert.assertEquals(1,table.getRowCount());			
			for (int i=1; i <= rows.getMetaData().getColumnCount();i++) {
				Object expected = table.getValue(0,rows.getMetaData().getColumnName(i));
				Object actual = rows.getObject(i);
				if ((expected == null) && (actual == null)) continue;
				else
					Assert.assertEquals(expected.toString(),actual.toString());

				
			}
			
		}
	}
}
