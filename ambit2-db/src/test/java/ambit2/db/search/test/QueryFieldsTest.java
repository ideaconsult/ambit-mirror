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

package ambit2.db.search.test;

import java.sql.ResultSet;
import java.util.List;

import junit.framework.Assert;
import net.idea.modbcum.i.query.QueryParam;

import org.junit.Test;

import ambit2.base.data.Property;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.QueryField;


public class QueryFieldsTest extends  QueryTest<QueryField>  {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setDbFile("src/test/resources/ambit2/db/processors/test/query-datasets-string.xml");
	}
	@Test
	public void test() throws Exception {
		QueryField qf = new QueryField();
		qf.setFieldname(Property.getInstance("name","ref"));
		qf.setValue("value");
		qf.setCondition(StringCondition.getInstance("regexp"));
		qf.setId(1);

		List<QueryParam> params = qf.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(3,params.size());
		Assert.assertEquals(Integer.class,params.get(0).getType());
		Assert.assertEquals(String.class,params.get(1).getType());
		Assert.assertEquals(String.class,params.get(2).getType());
		Assert.assertEquals(1,params.get(0).getValue());
		Assert.assertEquals("name",params.get(1).getValue());
		Assert.assertEquals("value",params.get(2).getValue());		
	}
	@Test
	public void testComments() throws Exception {
		QueryField qf = new QueryField();
		qf.setSearchByAlias(true);
		qf.setFieldname(Property.getInstance("name","ref"));
		qf.setValue("value");
		qf.setCondition(StringCondition.getInstance("regexp"));
		qf.setId(1);
		logger.fine(qf.getSQL());
		List<QueryParam> params = qf.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(3,params.size());
		Assert.assertEquals(Integer.class,params.get(0).getType());
		Assert.assertEquals(String.class,params.get(1).getType());
		Assert.assertEquals(String.class,params.get(2).getType());
		Assert.assertEquals(1,params.get(0).getValue());
		Assert.assertEquals("name",params.get(1).getValue());
		Assert.assertEquals("value",params.get(2).getValue());		
	}

	@Override
	protected QueryField createQuery() throws Exception {
		QueryField qf = new QueryField();
		qf.setFieldname(null);
		qf.setValue("abietic acid");
		qf.setCondition(StringCondition.getInstance("="));
		qf.setId(1);
		return qf;
	}

	@Override
	protected void verify(QueryField query, ResultSet rs) throws Exception {
		int count = 0;
		while (rs.next()) {
			Assert.assertEquals(query.getId().intValue(),rs.getInt(1));
			Assert.assertEquals(1,rs.getInt(2));
			Assert.assertEquals(1,rs.getInt(3));
			count++;
			//assertEquals(query.getValue(),rs.getInt(2));
		}
		Assert.assertEquals(1,count);
	}
}


