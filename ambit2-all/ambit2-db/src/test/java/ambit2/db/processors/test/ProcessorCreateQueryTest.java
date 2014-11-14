/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
 */

package ambit2.db.processors.test;

import junit.framework.Assert;
import net.idea.modbcum.i.IQueryObject;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.db.SessionID;
import ambit2.db.processors.ProcessorCreateQuery;
import ambit2.db.processors.ProcessorCreateSession;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.structure.QueryStructureByID;

public class ProcessorCreateQueryTest extends DbUnitTest {

	@Test
	public void test() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
		IQueryObject q = new QueryStructureByID(100211, 100215);
		q.setPageSize(1000);
		IDatabaseConnection c = getConnection();
		ProcessorCreateSession ps = new ProcessorCreateSession();
		ps.setConnection(c.getConnection());
		SessionID s = new SessionID();
		s = ps.process(s);

		c = getConnection();
		ProcessorCreateQuery pq = new ProcessorCreateQuery();
		pq.setSession(s);
		pq.setConnection(c.getConnection());
		IStoredQuery storedQuery = pq.process(q);
		c = getConnection();
		ITable table = c.createQueryTable("EXPECTED", String.format(
				"SELECT idsessions,title from sessions where title='%s'",
				s.getName()));
		Assert.assertEquals(1, table.getRowCount());
		Assert.assertEquals(s.getName(), table.getValue(0, "title").toString());

		Assert.assertEquals(3, storedQuery.getRows());
		c.close();
	}
}
