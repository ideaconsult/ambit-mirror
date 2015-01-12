/* RetrieveDatasetsTest.java
 * Author: nina
 * Date: Jan 7, 2009
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
import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.IQueryRetrieval;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.SourceDatasetRows;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.db.results.AmbitRows;
import ambit2.db.search.QueryExecutor;

public class RetrieveDatasetsTest extends RetrieveTest<SourceDataset> {


	@Test
	public void testGetParameters() throws Exception {
		Assert.assertEquals(0,((IQueryObject)query).getParameters().size());
	}

	@Override
	protected String getTestDatabase() {
		return "src/test/resources/ambit2/db/processors/test/src-datasets.xml";
	}
	@Test
	public void testGetObject() throws Exception {
		setUpDatabase(getTestDatabase());

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM src_dataset");		
		Assert.assertEquals(3,names.getRowCount());

		QueryExecutor<RetrieveDatasets> qe = new QueryExecutor<RetrieveDatasets>();		
		qe.setConnection(c.getConnection());
		ResultSet rs = qe.process((RetrieveDatasets)query);
		
		while (rs.next()) {
			ISourceDataset dataset = query.getObject(rs);
			names = c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM src_dataset where id_srcdataset="+dataset.getID() + " and name='"+ dataset.getName()+"'");		
			Assert.assertEquals(1,names.getRowCount());
		}
		rs.close();
		qe.close();
		c.close();
	}
	
	@Test
	public void testGetObjectByName() throws Exception {
		setUpDatabase(getTestDatabase());

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM src_dataset where name='Dataset 1'");		
		Assert.assertEquals(1,names.getRowCount());

		QueryExecutor<RetrieveDatasets> qe = new QueryExecutor<RetrieveDatasets>();		
		qe.setConnection(c.getConnection());
		((RetrieveDatasets)query).setValue(new SourceDataset("Dataset 1"));
		ResultSet rs = qe.process((RetrieveDatasets)query);
		
		while (rs.next()) {
			ISourceDataset dataset = query.getObject(rs);
			names = c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM src_dataset where id_srcdataset="+dataset.getID() + " and name='"+ dataset.getName()+"'");		
			Assert.assertEquals(1,names.getRowCount());
		}
		rs.close();
		qe.close();
		c.close();
	}	
	@Test
	public void testGetDatasetbyChemical() throws Exception {
		setUpDatabase(getTestDatabase());

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM src_dataset where name='Dataset 1'");		
		Assert.assertEquals(1,names.getRowCount());

		QueryExecutor<RetrieveDatasets> qe = new QueryExecutor<RetrieveDatasets>();		
		qe.setConnection(c.getConnection());
		IStructureRecord record = new StructureRecord();
		record.setIdchemical(29141);
		((RetrieveDatasets)query).setFieldname(record);
		ResultSet rs = qe.process((RetrieveDatasets)query);
		int count = 0;
		while (rs.next()) {
			ISourceDataset dataset = query.getObject(rs);
			names = c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM src_dataset where id_srcdataset="+dataset.getID() + " and name='"+ dataset.getName()+"'");		
			Assert.assertEquals(1,names.getRowCount());
			count++; 
		}
		Assert.assertEquals(2,count);
		rs.close();
		qe.close();
		c.close();
	}		
	@Test
	public void testGetDatasetbyStructure() throws Exception {
		setUpDatabase(getTestDatabase());

		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM src_dataset where name='Dataset 1'");		
		Assert.assertEquals(1,names.getRowCount());

		QueryExecutor<RetrieveDatasets> qe = new QueryExecutor<RetrieveDatasets>();		
		qe.setConnection(c.getConnection());
		IStructureRecord record = new StructureRecord();
		record.setIdstructure(129345);
		((RetrieveDatasets)query).setFieldname(record);
		ResultSet rs = qe.process((RetrieveDatasets)query);
		int count = 0;
		while (rs.next()) {
			ISourceDataset dataset = query.getObject(rs);
			names = c.createQueryTable("EXPECTED_DATASETS","SELECT * FROM src_dataset where id_srcdataset="+dataset.getID() + " and name='"+ dataset.getName()+"'");		
			Assert.assertEquals(1,names.getRowCount());
			count++;
		}
		Assert.assertEquals(2,count);
		rs.close();
		qe.close();
		c.close();
	}		
	@Override
	protected void verifyRows(AmbitRows<SourceDataset> rows) throws Exception {
		IDatabaseConnection c = getConnection();
		Assert.assertNotNull(rows);
		Assert.assertEquals(3,rows.size());
		while (rows.next()) {
			ISourceDataset dataset = rows.getObject();
			ITable table = 	c.createQueryTable("EXPECTED","SELECT id_srcdataset,name,user_name,idreference,title,url,licenseURI,rightsHolder,stars,maintainer FROM src_dataset join catalog_references using(idreference) where name='"+ dataset.getName() +"'");		
			Assert.assertEquals(1,table.getRowCount());			
			for (int i=1; i <= rows.getMetaData().getColumnCount();i++) {
				Assert.assertEquals(table.getValue(0,rows.getMetaData().getColumnName(i)).toString(),rows.getString(i));
			}
		}
		
	}
	@Override
	protected AmbitRows<SourceDataset> createRows() throws Exception {
		return new SourceDatasetRows();
	}
	@Override
	protected IQueryRetrieval<SourceDataset> createQuery() {
		return new RetrieveDatasets();
	}	
}
