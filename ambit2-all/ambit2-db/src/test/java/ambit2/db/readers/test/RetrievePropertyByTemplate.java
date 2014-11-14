/* RetrievePropertyByTemplate.java
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

import junit.framework.Assert;
import net.idea.modbcum.i.IQueryRetrieval;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;

import ambit2.base.data.Property;
import ambit2.db.results.AmbitRows;
import ambit2.db.search.property.TemplateQuery;

public class RetrievePropertyByTemplate  extends RetrieveTest<Property> {

	@Override
	protected IQueryRetrieval<Property> createQuery() {
		TemplateQuery q = new TemplateQuery();
		q.setValue("BCF");
		return q;
	}

	@Override
	protected String getTestDatabase() {
		return "src/test/resources/ambit2/db/processors/test/dataset-properties.xml";
	}
	@Override
	protected AmbitRows<Property> createRows() throws Exception {
		return new AmbitRows<Property>();
	}
	@Override
	protected void verifyRows(AmbitRows<Property> rows) throws Exception {
		IDatabaseConnection c = getConnection();
		Assert.assertNotNull(rows);
		Assert.assertEquals(3,rows.size());
		while (rows.next()) {
			Property p = rows.getObject();
			ITable table = 	c.createQueryTable("EXPECTED",
					"SELECT idproperty,idreference,properties.name,properties.units,properties.comments,title,url,ptype,islocal,type,`order` from template join template_def using(idtemplate) "+
					"join properties using(idproperty) join catalog_references using(idreference) " +
					"where template.name='BCF' and properties.name='"+p.getName()+"'");		
			Assert.assertEquals(1,table.getRowCount());			
			for (int i=1; i <= rows.getMetaData().getColumnCount();i++) {
				Object expected = table.getValue(0,rows.getMetaData().getColumnName(i));
				Object actual = rows.getObject(i);
				if ((expected == null) && (actual == null)) continue;
				else
					Assert.assertEquals(expected.toString(),actual.toString());

				
			}
			
		}
	}
}
