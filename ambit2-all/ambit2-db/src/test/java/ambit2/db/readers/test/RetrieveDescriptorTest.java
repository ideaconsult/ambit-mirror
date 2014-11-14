/* RetrieveDescriptorTest.java
 * Author: nina
 * Date: Jan 9, 2009
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
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;

import ambit2.base.data.StructureRecord;
import ambit2.db.readers.RetrieveDescriptor;
import ambit2.db.results.AmbitRows;
import ambit2.db.search.QueryExecutor;

public class RetrieveDescriptorTest extends RetrieveTest<DescriptorValue>{

	@Override
	protected String getTestDatabase() {
		return "src/test/resources/ambit2/db/processors/test/dataset-properties.xml";
	}
	@Test
	public void testGetObject() throws Exception {
		setUpDatabase(getTestDatabase());

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM values_number");		
		Assert.assertEquals(4,names.getRowCount());

		QueryExecutor<RetrieveDescriptor> qe = new QueryExecutor<RetrieveDescriptor>();		
		qe.setConnection(c.getConnection());
		ResultSet rs = qe.process((RetrieveDescriptor)query);
		int count = 0;
		while (rs.next()) {
			DescriptorValue value = query.getObject(rs);
			double d = ((DoubleResult)value.getValue()).doubleValue();
			String[] descrnames = value.getNames();
			for (String name: descrnames) {

				names = c.createQueryTable("EXPECTED_DATASETS","SELECT value,name FROM properties join values_number using(idproperty) where name='"+name +"' and value="+d );		
				Assert.assertEquals(1,names.getRowCount());
			}
			count++;
		}
		Assert.assertEquals(2,count);
		rs.close();
		qe.close();
		c.close();
	}

	
	@Override
	protected IQueryRetrieval<DescriptorValue> createQuery() {
		RetrieveDescriptor q = new RetrieveDescriptor();
		q.setValue(new StructureRecord(0,100215,"",""));
		return q;
	}
	@Override
	protected AmbitRows<DescriptorValue> createRows() throws Exception {
		return new AmbitRows<DescriptorValue>();
	}
	@Override
	protected void verifyRows(AmbitRows<DescriptorValue> rows) throws Exception {
		IDatabaseConnection c = getConnection();
		Assert.assertNotNull(rows);
		Assert.assertEquals(2,rows.size());
		while (rows.next()) {
			DescriptorValue value = rows.getObject();
			Assert.assertEquals(1,value.getNames().length);
			ITable table = 	c.createQueryTable("EXPECTED",
					"SELECT idproperty,title,url,name,value_num as value,user_name FROM property_values join properties using(idproperty) join catalog_references using(idreference) where idstructure=100215 and name='"+value.getNames()[0]+"' and value_num is not null");			
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
