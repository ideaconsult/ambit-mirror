/* DatasetStructure_crud_test.java
 * Author: nina
 * Date: Apr 1, 2009
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

package ambit2.db.update.test;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.update.dataset.DatasetAddStructure;
import ambit2.db.update.dataset.DatasetDeleteStructure;

public class DatasetStructure_crud_test extends CRUDTest<SourceDataset, IStructureRecord> {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		dbFile = "src/test/resources/ambit2/db/processors/test/dataset-properties.xml";			
	}
	@Override
	protected IQueryUpdate<SourceDataset, IStructureRecord> createQuery()
			throws Exception {
		SourceDataset adataset = new SourceDataset();
		adataset.setName("Dataset 1");
		DatasetAddStructure d = new DatasetAddStructure();
		d.setGroup(adataset);
		
		IStructureRecord record = new StructureRecord();
		record.setIdchemical(10);
		record.setIdstructure(100211);
		d.setObject(record);
		return d;
	}

	@Override
	protected void createVerify(
			IQueryUpdate<SourceDataset, IStructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT count(idstructure) as c FROM struc_dataset join src_dataset using(id_srcdataset) where name='Dataset 1'");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("3",table.getValue(0,"c").toString());
		table = c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset join src_dataset using(id_srcdataset) where name='Dataset 1' and idstructure=100211");
		Assert.assertEquals(1,table.getRowCount());		
		c.close();
		
	}

	@Test
	public void testCreateNew() throws Exception {
		IQueryUpdate query = createQueryNew();
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		executor.setConnection(c.getConnection());
		executor.open();
		Assert.assertTrue(executor.process(query)>=1);
		createVerifyNew(query);
		c.close();
	}
	
	protected IQueryUpdate<SourceDataset, IStructureRecord> createQueryNew()
			throws Exception {
		SourceDataset adataset = new SourceDataset();
		adataset.setName("ambit");
		adataset.setTitle("reference");
		DatasetAddStructure d = new DatasetAddStructure();
		d.setGroup(adataset);
		
		IStructureRecord record = new StructureRecord();
		record.setIdchemical(10);
		record.setIdstructure(100211);
		d.setObject(record);
		return d;
	}
	
	protected void createVerifyNew(
			IQueryUpdate<SourceDataset, IStructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT count(idstructure) as c FROM struc_dataset join src_dataset using(id_srcdataset) where name='ambit'");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("1",table.getValue(0,"c").toString());
		table = c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset join src_dataset using(id_srcdataset) where name='ambit' and idstructure=100211");
		Assert.assertEquals(1,table.getRowCount());		
		c.close();
		
	}	
	@Override
	protected IQueryUpdate<SourceDataset, IStructureRecord> deleteQuery()
			throws Exception {
		SourceDataset adataset = new SourceDataset();
		adataset.setId(1);
		DatasetDeleteStructure d = new DatasetDeleteStructure();
		d.setGroup(adataset);
		
		IStructureRecord record = new StructureRecord();
		record.setIdchemical(10);
		record.setIdstructure(100215);
		d.setObject(record);
		return d;
	}

	@Override
	protected void deleteVerify(
			IQueryUpdate<SourceDataset, IStructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT count(idstructure) as c FROM struc_dataset join src_dataset using(id_srcdataset) where name='Dataset 1'");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("1",table.getValue(0,"c").toString());
		table = c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset join src_dataset using(id_srcdataset) where name='Dataset 1' and idstructure=100215");
		Assert.assertEquals(0,table.getRowCount());		
		c.close();
		
	}

	@Override
	public void testUpdate() throws Exception {
	}
	@Override
	protected IQueryUpdate<SourceDataset, IStructureRecord> updateQuery()
			throws Exception {
		return null;
	}

	@Override
	protected void updateVerify(
			IQueryUpdate<SourceDataset, IStructureRecord> query)
			throws Exception {
		
	}

}
