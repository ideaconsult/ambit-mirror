/* QueryProfile.java
 * Author: nina
 * Date: May 1, 2009
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
import net.idea.modbcum.i.IQueryRetrieval;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.ProcessorCreateProfileQuery;

public class QueryProfile   extends QueryTest<IQueryRetrieval<IStructureRecord>>{
	@Override
	public void setUp() throws Exception {
		super.setUp();
			
	}	
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery() throws Exception {
		setDbFile("src/test/resources/ambit2/db/processors/test/dataset-properties.xml");	
		setUpDatabase(getDbFile());
		ProcessorCreateProfileQuery p = new ProcessorCreateProfileQuery();
		p.setTag(null);
		try {
			p.setConnection(getConnection().getConnection());
  			Template t = new Template();
  			t.setName("BCF");			
			return p.process(t);
		} finally {
			p.close();
			
		}
	}

	@Override
	protected void verify(IQueryRetrieval<IStructureRecord> query, ResultSet rs)
			throws Exception {
		int records = 0;
		while (rs.next()) {
			records ++;
			Assert.assertEquals(11,rs.getInt(2));
			Assert.assertEquals(100215,rs.getInt(3));

		}
		Assert.assertEquals(1,records);
		
	}
}
