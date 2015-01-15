/* UpdateTest.java
 * Author: nina
 * Date: Mar 28, 2009
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.db.UpdateExecutor;
import ambit2.db.processors.test.DbUnitTest;

public abstract class CRUDTest<G, T> extends DbUnitTest {
    protected UpdateExecutor<IQueryUpdate<G, T>> executor;
    protected String dbFile = "src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml";

    @Override
    @Before
    public void setUp() throws Exception {
	super.setUp();

	executor = new UpdateExecutor<IQueryUpdate<G, T>>();
    }

    @After
    public void tearDown() throws Exception {
	if (executor != null)
	    executor.close();
    }

    @Test
    public void testUpdate() throws Exception {
	IQueryUpdate<G, T> query = updateQuery();
	setUpDatabase(dbFile);
	IDatabaseConnection c = getConnection();
	executor.setConnection(c.getConnection());
	executor.open();
	Assert.assertTrue(executor.process(query) >= 1);
	updateVerify(query);
	c.close();
    }

    @Test
    public void testCreate() throws Exception {
	setUpDatabase(dbFile);
	IQueryUpdate<G, T> query = createQuery();
	IDatabaseConnection c = getConnection();
	executor.setConnection(c.getConnection());
	executor.open();
	Assert.assertTrue(executor.process(query) >= 1);
	createVerify(query);
	c.close();
    }

    @Test
    public void testCreateNew() throws Exception {
	IQueryUpdate<G, T> query = createQueryNew();
	setUpDatabase(dbFile);
	IDatabaseConnection c = getConnection();
	executor.setConnection(c.getConnection());
	executor.open();
	Assert.assertTrue(executor.process(query) >= 1);
	createVerifyNew(query);
	c.close();
    }

    @Test
    public void testDelete() throws Exception {
	IQueryUpdate<G, T> query = deleteQuery();
	setUpDatabase(dbFile);
	IDatabaseConnection c = getConnection();
	executor.setConnection(c.getConnection());
	executor.open();
	Assert.assertTrue(executor.process(query) >= 1);
	deleteVerify(query);
	c.close();
    }

    protected abstract IQueryUpdate<G, T> createQuery() throws Exception;

    protected abstract IQueryUpdate<G, T> createQueryNew() throws Exception;

    protected abstract IQueryUpdate<G, T> updateQuery() throws Exception;

    protected abstract IQueryUpdate<G, T> deleteQuery() throws Exception;

    protected abstract void createVerify(IQueryUpdate<G, T> query) throws Exception;

    protected abstract void createVerifyNew(IQueryUpdate<G, T> query) throws Exception;

    protected abstract void updateVerify(IQueryUpdate<G, T> query) throws Exception;

    protected abstract void deleteVerify(IQueryUpdate<G, T> query) throws Exception;
}
