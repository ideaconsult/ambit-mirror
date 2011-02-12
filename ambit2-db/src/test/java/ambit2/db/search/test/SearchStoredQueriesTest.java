/* SearchStoredQueriesTest.java
 * Author: nina
 * Date: Apr 12, 2009
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
import ambit2.db.update.storedquery.SearchStoredQueries;

public class SearchStoredQueriesTest extends QueryTest<SearchStoredQueries> {

	@Override
	protected SearchStoredQueries createQuery() throws Exception {
		SearchStoredQueries q = new SearchStoredQueries();
		q.setValue("test");
		Assert.assertEquals("select idquery,count(idstructure) as rows,name,content from query_results join query using(idquery) join sessions using (idsessions) where user_name=SUBSTRING_INDEX(user(),'@',1)  and name regexp ? group by idquery order by idquery desc",q.getSQL());
		Assert.assertEquals("^test",q.getValue());
		return q;
	}

	@Override
	protected void verify(SearchStoredQueries query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			IStoredQuery q = query.getObject(rs);
			
			Assert.assertTrue(
					((q.getId()==1) && (q.getName().equals("test query 1")))
					||
					((q.getId()==2) && (q.getName().equals("test query 2")))
					);
			count++;
		}
		Assert.assertEquals(2,count);
		
	}

}