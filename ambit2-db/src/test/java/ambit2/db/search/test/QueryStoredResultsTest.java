/* QueryStoredResultsTest.java
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
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryStoredResults;

public class QueryStoredResultsTest  extends QueryTest<QueryStoredResults> {

	@Override
	protected QueryStoredResults createQuery() throws Exception {
		QueryStoredResults q = new QueryStoredResults();
		q.setFieldname(new StoredQuery(1));
		return q;
	}

	@Override
	protected void verify(QueryStoredResults query, ResultSet rs)
			throws Exception {
		int records = 0;
		while (rs.next()) {
			records++;
			IStructureRecord r = query.getObject(rs);
			Assert.assertTrue(
					((r.getIdstructure()==100211)&&(r.getIdchemical()==7))
					||
					((r.getIdstructure()==129345)&&(r.getIdchemical()==29141))
					);
			Assert.assertEquals(query.getFieldname().getId().intValue(),rs.getInt(1));

		}
		Assert.assertEquals(2,records);
		
	}
/*
  <query_results idquery="1" idchemical="7" idstructure="100211" selected="1" metric="0"/>
  <query_results idquery="1" idchemical="29141" idstructure="129345" selected="1" metric="0"/>
  
  <query_results idquery="2" idchemical="10" idstructure="100214" selected="1" metric="0"/>
  <query_results idquery="2" idchemical="29141" idstructure="129345" selected="1" metric="0"/>  
 */
}
