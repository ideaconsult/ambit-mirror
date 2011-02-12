/* QueryStoredTest.java
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

package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StoredQuery;
import ambit2.db.update.storedquery.ReadStoredQuery;

public class QueryStoredTest extends QueryTest<ReadStoredQuery> {

	@Override
	protected ReadStoredQuery createQuery() throws Exception {
		ReadStoredQuery q = new ReadStoredQuery();
		Assert.assertEquals(
				"select idquery,name,title,content from query join sessions using(idsessions)\n where  title = ? order by name",
				q.getSQL());
		StoredQuery sq = new StoredQuery(1);
		sq.setName(null);
		q.setValue(sq);
		Assert.assertEquals(
				"select idquery,name,title,content from query join sessions using(idsessions)\n where  idquery = ? and  title = ? order by name",
				q.getSQL());		
		return q;
	}

	@Override
	protected void verify(ReadStoredQuery query, ResultSet rs) throws Exception {
		int c = 0;
		while (rs.next()) {
			IStoredQuery q = query.getObject(rs);
			
			Assert.assertTrue(
					((q.getId()==1) && (q.getName().equals("test query 1")))
					||
					((q.getId()==2) && (q.getName().equals("test query 2")))
					);
			c++;
		}
		Assert.assertEquals(1,c);
		
	}

}
