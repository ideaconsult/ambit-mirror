/* DictionaryObjectTest.java
 * Author: nina
 * Date: Feb 6, 2009
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
import java.util.List;

import junit.framework.Assert;
import net.idea.modbcum.i.query.QueryParam;

import org.junit.Before;
import org.junit.Test;

import ambit2.base.data.Dictionary;
import ambit2.db.search.DictionaryObjectQuery;
import ambit2.db.search.DictionaryQuery;
import ambit2.db.search.DictionarySubjectQuery;
import ambit2.db.search.StringCondition;

public class DictionaryObjectTest extends QueryTest<DictionaryQuery<Dictionary>> {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		setDbFile("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
	}
	@Override
	protected DictionaryQuery<Dictionary> createQuery() throws Exception {
		DictionaryObjectQuery q = new DictionaryObjectQuery();
		q.setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
		q.setValue("Endpoints");
		return q;
	}

	@Override
	protected void verify(DictionaryQuery<Dictionary> query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			records ++;
			Dictionary d = query.getObject(rs);
			Assert.assertEquals("Endpoints",d.getParentTemplate());
			Assert.assertEquals("Physicochemical effects",d.getTemplate());			
		}
		Assert.assertEquals(1,records);
		
	}
	@Test
	public void test() throws Exception {
		DictionarySubjectQuery qf = new DictionarySubjectQuery();
		qf.setValue("Endpoints");
		qf.setCondition(StringCondition.getInstance("="));
		qf.setId(1);
		Assert.assertEquals(String.format(DictionaryQuery.SQL,qf.getFieldname(),qf.getCondition()), 
				qf.getSQL());
		List<QueryParam> params = qf.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(1,params.size());
		Assert.assertEquals(String.class,params.get(0).getType());
		Assert.assertEquals("Endpoints",params.get(0).getValue());
	
	}
	

}
