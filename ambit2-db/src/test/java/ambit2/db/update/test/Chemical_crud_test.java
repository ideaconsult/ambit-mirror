/* Chemical_crud_test.java
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

import junit.framework.Assert;
import net.idea.modbcum.i.query.IQueryUpdate;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IChemical;
import ambit2.db.update.chemical.CreateChemical;
import ambit2.db.update.chemical.DeleteChemical;
import ambit2.db.update.chemical.UpdateChemical;

public class Chemical_crud_test extends CRUDTest<Object,IChemical>{

	@Override
	protected IQueryUpdate<Object, IChemical> createQuery() throws Exception {
		IChemical c = new StructureRecord();
		c.setIdchemical(7);
		c.setSmiles("CCC");
		c.setInchi("inchi");
		c.setFormula("formula");
		c.setInchiKey("inchi-key");
		return new CreateChemical(c);
	}

	@Override
	protected void createVerify(IQueryUpdate<Object, IChemical> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where smiles='CCC' and inchi='inchi' and formula='formula' and inchikey='key'");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where idchemical=7");
		Assert.assertEquals(1,table.getRowCount());		
		c.close();
	}
	@Test
	public void testCreate() throws Exception {
		IQueryUpdate query = createQuery();
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		executor.setConnection(c.getConnection());
		executor.open();
		Integer r = executor.process(query);
		//Assert.assertTrue(r==2);
		createVerify(query);
		c.close();
	}

	protected IQueryUpdate<Object, IChemical> createQueryNew() throws Exception {
		IChemical c = new StructureRecord();
		c.setSmiles("CCC");
		c.setInchi("inchi");
		c.setFormula("formula");
		c.setInchiKey("key");
		return new CreateChemical(c);
	}

	protected void createVerifyNew(IQueryUpdate<Object, IChemical> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where smiles='CCC' and inchi='inchi' and formula='formula' and inchikey='key'");
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
	@Override
	protected IQueryUpdate<Object, IChemical> deleteQuery() throws Exception {
		IChemical c = new StructureRecord();
		c.setIdchemical(10);
		DeleteChemical q = new DeleteChemical(c);
		q.setForceStructureDelete(true);
		return q;
	}

	@Override
	protected void deleteVerify(IQueryUpdate<Object, IChemical> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where inchi='InChI=1/2FH.Na/h2*1H;/q;;+1/p-1'");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where idchemical=10");
		Assert.assertEquals(0,table.getRowCount());
		
		c.close();		
		
	}

	/**
	 * Only update inchi/inchi keys, not other fields 
	 * @throws Exception
	 */
	@Test
	public void testUpdateInChI() throws Exception {
		
		IChemical chem = new StructureRecord();
		chem.setInchi("InChI=UPDATEDINCHI");
		chem.setInchiKey("UPDATEDKEY");
		chem.setIdchemical(10);
		
		IQueryUpdate<Object,IChemical> query = new UpdateChemical(chem);
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		executor.setConnection(c.getConnection());
		executor.open();
		Assert.assertTrue(executor.process(query)>=1);
		updateVerifyInChI(query);
		c.close();
	}
	

	protected void updateVerifyInChI(IQueryUpdate<Object, IChemical> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where inchi='InChI=1/2FH.Na/h2*1H;/q;;+1/p-1'");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where idchemical=10");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals  where smiles is null and formula is null and inchi='InChI=UPDATEDINCHI' and inchikey='UPDATEDKEY' and idchemical=10");
		Assert.assertEquals(1,table.getRowCount());

		c.close();		
		
	}	
	
	@Override
	protected IQueryUpdate<Object, IChemical> updateQuery() throws Exception {
		IChemical c = new StructureRecord();
		c.setSmiles("CCC");
		c.setInchi("inchi");
		c.setFormula("formula");
		c.setInchiKey("key");
		c.setIdchemical(10);
		return new UpdateChemical(c);
	}

	@Override
	protected void updateVerify(IQueryUpdate<Object, IChemical> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where inchi='InChI=1/2FH.Na/h2*1H;/q;;+1/p-1'");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where idchemical=10");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where smiles='CCC' and inchi='inchi' and formula='formula' and inchikey='key' and idchemical=10");
		Assert.assertEquals(1,table.getRowCount());
	
		c.close();		
		
	}

}
