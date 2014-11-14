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
import net.idea.modbcum.i.IQueryRetrieval;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.db.readers.RetrieveField;
import ambit2.db.results.AmbitRows;
import ambit2.db.search.QueryExecutor;

public class RetrieveFieldTest extends RetrieveTest<Object> {

	@Override
	protected IQueryRetrieval<Object> createQuery() {
		RetrieveField q = new RetrieveField();
		q.setValue(new StructureRecord(-1,100215,null,null));
		q.setFieldname(Property.getInstance("Property 1",""));
		return q;
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

		QueryExecutor<RetrieveField> qe = new QueryExecutor<RetrieveField>();		
		qe.setConnection(c.getConnection());

		ResultSet rs = qe.process((RetrieveField)query);
		
		int count = 0; 
		while (rs.next()) {
					
			
			count++;
		}
		Assert.assertTrue(count>0);
		rs.close();
		qe.close();
		c.close();
	}	
	@Override
	protected AmbitRows<Object> createRows() throws Exception {
		return new AmbitRows<Object>();
	}
	@Override
	protected void verifyRows(AmbitRows<Object> rows) throws Exception {
		IDatabaseConnection c = getConnection();
		Assert.assertNotNull(rows);
		Assert.assertEquals(1,rows.size());
		while (rows.next()) {
			ITable table = 	c.createQueryTable("EXPECTED",
					"select name,idreference,idproperty,idstructure,ifnull(text,value) as value_string,value_num,title,url,-1,id,units,comments from property_values \n"+
					"left join property_string using(idvalue_string) \n"+
					"join properties using(idproperty) join catalog_references using(idreference) \n"+
					"where idstructure=100215 and name='Property 1' "
					/*
					"select name,idreference,idproperty,idstructure,value_string,value_num,idtype from properties join\n"+
					"(\n"+
					"select idstructure,idproperty,null as value_string,value as value_num,1 as idtype from values_number where idstructure=100215\n"+
					"union\n"+
					"select idstructure,idproperty,value as value_string,null,0 as idtype from values_string where idstructure=100215\n"+
					") as L using (idproperty)\nwhere name='Property 1'"
					*/
					);			
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
