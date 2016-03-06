/* RetrieveProfileTest.java
 * Author: nina
 * Date: Apr 26, 2009
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

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.db.search.property.RetrieveProfile;

public class RetrieveProfileTest extends RetrieveTest<Property> {

	@Override
	protected IQueryRetrieval<Property> createQuery() {
		Profile<Property> profile = new Profile<Property>();
		Property p = Property.getInstance("PUBCHEM_CID",
				LiteratureEntry.getInstance("pubchem"));
		p.setEnabled(true);
		profile.add(p);
		p = Property.getInstance("Property 1",
				LiteratureEntry.getInstance("pubchem"));
		p.setEnabled(true);
		profile.add(p);
		RetrieveProfile q = new RetrieveProfile();
		q.setValue(profile);
		return q;
	}

	@Override
	protected String getTestDatabase() {
		return "ambit2/db/processors/test/dataset-properties.xml";
	}

	@Override
	protected void verifyRows(IQueryRetrieval<Property> query, ResultSet rows)
			throws Exception {
		IDatabaseConnection c = getConnection();
		Assert.assertNotNull(rows);
		int r = 0;
		while (rows.next()) {
			r++;
			Property p = query.getObject(rows);
			ITable table = c.createQueryTable("EXPECTED",
					"select name from properties where name='" + p.getName()
							+ "'");
			Assert.assertEquals(1, table.getRowCount());
		}
		Assert.assertEquals(2, r);
	}

}
