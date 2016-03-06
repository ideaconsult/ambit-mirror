/* RetrieveDictionaryTest.java
 * Author: nina
 * Date: Apr 5, 2009
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

package ambit2.db.readers.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import net.idea.modbcum.i.IQueryRetrieval;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.Dictionary;
import ambit2.db.search.DictionaryObjectQuery;

public class RetrieveDictionaryTest extends RetrieveTest<Dictionary> {

	@Override
	protected IQueryRetrieval<Dictionary> createQuery() {
		DictionaryObjectQuery q = new DictionaryObjectQuery();
		q.setValue("Endpoints");
		return q;
	}

	@Override
	protected String getTestDatabase() {
		return "ambit2/db/processors/test/descriptors-datasets.xml";
	}
	
	@Override
	protected void verifyRows(IQueryRetrieval<Dictionary> query, ResultSet rows) throws Exception {
		IDatabaseConnection c = getConnection();
		Assert.assertNotNull(rows);
		int r = 0;
		while (rows.next()) {
			r++;
			Dictionary dict = query.getObject(rows);
			ITable table = 	c.createQueryTable("EXPECTED",
					"select tSubject.name,relationship from dictionary d "+
					"join template as tSubject on d.idsubject=tSubject.idtemplate "+
					"join template as tObject on d.idobject=tObject.idtemplate "+
					"where tObject.name = '"+dict.getParentTemplate()+"' order by tObject.idtemplate")	;				
			Assert.assertEquals(1,table.getRowCount());			
			for (int i=1; i <= rows.getMetaData().getColumnCount();i++) {
				Assert.assertEquals(table.getValue(0,"name"),dict.getTemplate());
				Assert.assertEquals(table.getValue(0,"relationship"),dict.getRelationship());				
			}
		}
		Assert.assertEquals(1,r);
	}
}
