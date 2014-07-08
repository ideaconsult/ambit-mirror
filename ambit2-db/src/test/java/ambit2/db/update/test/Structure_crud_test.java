/* Structure_crud_test.java
 * Author: nina
 * Date: Mar 31, 2009
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

import java.sql.ResultSet;

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.core.processors.structure.key.ExactStructureSearchMode;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.structure.QueryStructure;
import ambit2.db.update.structure.CreateStructure;
import ambit2.db.update.structure.DeleteStructure;
import ambit2.db.update.structure.UpdateStructure;


public class Structure_crud_test extends CRUDTest<Object,IStructureRecord>{

	@Override
	protected IQueryUpdate<Object, IStructureRecord> createQuery()
			throws Exception {
		IStructureRecord c = new StructureRecord();
		c.setContent("content");
		c.setFormat("SDF");
		c.setIdchemical(10);
		return new CreateStructure(c);
	}

	@Override
	protected void createVerify(IQueryUpdate<Object, IStructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
        QueryStructure q = new QueryStructure();
        q.setFieldname(ExactStructureSearchMode.idchemical);
        q.setValue("10");
        
        RetrieveStructure r = new RetrieveStructure();
        r.setFieldname(false); //structure
        QueryExecutor execR = new QueryExecutor();
        execR.setConnection(c.getConnection());
		QueryExecutor exec = new QueryExecutor();
		exec.setConnection(c.getConnection());
		ResultSet rs = exec.process(q);
		int count = 0;
		while (rs.next()) {
			IStructureRecord record = q.getObject(rs);
			r.setValue(record);
			ResultSet rs1 = execR.process(r);
			while (rs1.next()) {
				record = r.getObject(rs1);
				if ("content".equals(record.getContent())) count++;
			}
			execR.closeResults(rs1);
			
		}
		exec.closeResults(rs);
		
		Assert.assertEquals(1,count);
		c.close();
	}

	@Override
	protected IQueryUpdate<Object, IStructureRecord> deleteQuery()
			throws Exception {
		
		IStructureRecord c = new StructureRecord();
		c.setIdchemical(10);
		c.setIdstructure(100214);
		return new DeleteStructure(c);

	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object, IStructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure where idstructure=100214 and idchemical=10");
		Assert.assertEquals(0,table.getRowCount());
		c.close();	
	}

	@Override
	protected IQueryUpdate<Object, IStructureRecord> updateQuery()
			throws Exception {
		IStructureRecord c = new StructureRecord();
		c.setContent("content");
		c.setFormat("SDF");
		c.setIdchemical(10);
		c.setIdstructure(100214);
		return new UpdateStructure(c);

	}

	@Override
	protected void updateVerify(IQueryUpdate<Object, IStructureRecord> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure where idstructure=100214 and idchemical=10");
		Assert.assertEquals(1,table.getRowCount());
		c.close();	
		
	}

	@Override
	protected IQueryUpdate<Object, IStructureRecord> createQueryNew()
			throws Exception {
		IStructureRecord c = new StructureRecord();
		c.setContent("content");
		c.setFormat("SDF");
		return new CreateStructure(c);
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<Object, IStructureRecord> query)
			throws Exception {
	        IDatabaseConnection c = getConnection();	

			ITable table = 	c.createQueryTable("EXPECTED","SELECT idstructure,idchemical,type_structure FROM structure");
			Assert.assertTrue(table.getRowCount()>0);
	        
	        RetrieveStructure r = new RetrieveStructure();
	        r.setPreferedStructure(false);
	        r.setFieldname(false);
	        QueryExecutor execR = new QueryExecutor();
	        execR.setConnection(c.getConnection());
			int count = 0;
			for (int i=0; i < table.getRowCount();i++) {
				IStructureRecord record = new StructureRecord();
				record.setIdstructure(Integer.parseInt(table.getValue(i,"idstructure").toString()));
	            try {
	            	Object t = table.getValue(i,"type_structure");
	            	for (STRUC_TYPE type : STRUC_TYPE.values()) if (type.toString().equals(t)) {
	            		record.setType(type);
	            		break;
	            	}
	            } catch (Exception x) {
	            	record.setType(STRUC_TYPE.NA);
	            }				
				r.setValue(record);
				ResultSet rs1 = execR.process(r);
				while (rs1.next()) {
					record = r.getObject(rs1);
					if ("content".equals(record.getContent())) count++;
				}
				execR.closeResults(rs1);
				
			}
			
			Assert.assertEquals(1,count);
			c.close();
		
	}

}
