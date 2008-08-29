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

package ambit2.db.test;

import java.sql.Connection;

import ambit2.db.SessionID;
import ambit2.db.processors.ProcessorCreateQuery;
import ambit2.db.processors.ProcessorCreateSession;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryStructureByID;


public class ProcessorCreateQueryTest extends RepositoryTest {
	public void test() throws Exception {
		/*
		IStoredQuery q = new StoredQuery();
		q.setQuery();
		q.setName("test");
		*/
		IQueryObject q = new QueryStructureByID(100,200); 
		
		Connection c = datasource.getConnection();
		ProcessorCreateSession ps = new ProcessorCreateSession();
		ps.setConnection(c);
		SessionID s = new SessionID();
		s = ps.process(s);
		ProcessorCreateQuery pq = new ProcessorCreateQuery();
		pq.setSession(s);
		pq.setConnection(datasource.getConnection());
		IStoredQuery storedQuery = pq.process(q);
		assertEquals(101,storedQuery.getRows());
	}
}


