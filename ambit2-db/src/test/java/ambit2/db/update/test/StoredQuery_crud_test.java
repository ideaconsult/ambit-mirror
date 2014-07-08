/* StoredQuery_crud_test.java
 * Author: nina
 * Date: Apr 10, 2009
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

import ambit2.db.SessionID;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.db.update.storedquery.CreateStoredQuery;
import ambit2.db.update.storedquery.DeleteStoredQuery;
import ambit2.db.update.storedquery.UpdateStoredQuery;

public class StoredQuery_crud_test extends CRUDTest<SessionID,IStoredQuery> {

	@Override
	protected IQueryUpdate<SessionID, IStoredQuery> createQuery()
			throws Exception {
		StoredQuery q = new StoredQuery();
		StringBuilder b = new StringBuilder();
		for (int i=0; i < 250; i++) b.append("a");
		q.setName(b.toString());
		q.setQuery(new QueryStructureByID(100215));
		CreateStoredQuery c =  new CreateStoredQuery();
		//c.setGroup(new SessionID(1));
		c.setObject(q);
		return c;
	}

	@Override
	public void testCreate() throws Exception {
	
	}
	
	@Override
	protected IQueryUpdate<SessionID, IStoredQuery> createQueryNew()
			throws Exception {
		StoredQuery q = new StoredQuery();
		q.setName("new name");
		q.setQuery(new QueryStructureByID(100215));
		CreateStoredQuery c =  new CreateStoredQuery();
		c.setGroup(new SessionID(1));
		c.setObject(q);
		return c;
	}

	@Override
	protected void createVerify(IQueryUpdate<SessionID, IStoredQuery> query)
			throws Exception {
        IDatabaseConnection c = getConnection();
		StringBuilder b = new StringBuilder();
		for (int i=0; i < 200; i++) b.append("a");  
		ITable table = 	c.createQueryTable("EXPECTED","SELECT name FROM query where name regexp '^a'");// where name='"+b.toString()+"'");
		Assert.assertEquals(200,b.toString().length());
		Assert.assertEquals(200,table.getValue(0,"name").toString().length());
		Assert.assertEquals(b.toString(),table.getValue(0,"name").toString());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM query join query_results using(idquery) \nwhere idchemical=11 and idstructure=100215 and name='"+b.toString()+"'");
		Assert.assertEquals(1,table.getRowCount());		
		c.close();
		
	}

	@Override
	protected void createVerifyNew(IQueryUpdate<SessionID, IStoredQuery> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM query where name='new name'");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM query join query_results using(idquery) where idchemical=11 and idstructure=100215 and name='new name'");
		Assert.assertEquals(1,table.getRowCount());		
		c.close();
		
	}

	@Test
	public void testDeleteQueriesBySession() throws Exception {
		IQueryUpdate<SessionID, IStoredQuery> query = deleteQueriesBySession();
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		executor.setConnection(c.getConnection());
		executor.open();
		Assert.assertTrue(executor.process(query)>=1);
		deleteQueriesBySessionVerify(query);
		c.close();
	}	
	
	protected void deleteQueriesBySessionVerify(IQueryUpdate<SessionID, IStoredQuery> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM query where idsessions=1");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM query_results join query using(idquery) join sessions using(idsessions) where idsessions=1");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
	}	
	protected IQueryUpdate<SessionID, IStoredQuery> deleteQueriesBySession()
			throws Exception {
		DeleteStoredQuery c =  new DeleteStoredQuery();
		c.setGroup(new SessionID(1));
		return c;
	}
	/*
	
	@Test
	public void testToggleSelectedResult() throws Exception {
		IQueryUpdate<SessionID, IStoredQuery> query = deleteQueriesBySession();
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		executor.setConnection(c.getConnection());
		executor.open();
		Assert.assertTrue(executor.process(query)>=1);
		deleteQueriesBySessionVerify(query);
		c.close();
	}	
	
	protected void deleteQueriesBySessionVerify(IQueryUpdate<SessionID, IStoredQuery> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM query where idsessions=1");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM query_results join query using(idquery) join sessions using(idsessions) where idsessions=1");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
	}	
	
	protected IQueryUpdate<SessionID, IStoredQuery> deleteQueriesBySession()
			throws Exception {
		DeleteStoredQuery c =  new DeleteStoredQuery();
		c.setGroup(new SessionID(1));
		return c;
	}
*/
	@Override
	protected IQueryUpdate<SessionID, IStoredQuery> deleteQuery()
			throws Exception {
		StoredQuery q = new StoredQuery();
		q.setId(1);
		DeleteStoredQuery c =  new DeleteStoredQuery();
		c.setGroup(new SessionID(1));
		c.setObject(q);
		return c;
	}

	@Override
	protected void deleteVerify(IQueryUpdate<SessionID, IStoredQuery> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM query where idquery=1");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM query");
		Assert.assertEquals(1,table.getRowCount());		
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM query_results where idquery=1");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
	}

	@Override
	protected IQueryUpdate<SessionID, IStoredQuery> updateQuery()
			throws Exception {
		StoredQuery q = new StoredQuery();
		q.setName("new name 2");
		q.setId(2);
		UpdateStoredQuery c =  new UpdateStoredQuery();
		c.setObject(q);
		return c;
	}

	@Override
	protected void updateVerify(IQueryUpdate<SessionID, IStoredQuery> query)
			throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM query where idquery=2");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM query where idquery=2 and name='new name 2'");
		Assert.assertEquals(1,table.getRowCount());		
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM query where idquery=2 and name='test query 2'");
		Assert.assertEquals(0,table.getRowCount());				
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM query_results where idquery=2");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
	}

}
