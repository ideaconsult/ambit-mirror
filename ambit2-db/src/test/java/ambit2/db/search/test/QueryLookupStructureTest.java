/* QueryLookupStructureTest.java
 * Author: nina
 * Date: Apr 27, 2009
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
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.db.search.structure.QueryLookupStructure;

public class QueryLookupStructureTest  extends QueryTest<QueryLookupStructure> {

	@Override
	protected QueryLookupStructure createQuery() throws Exception {
		IStructureRecord record = new StructureRecord();
		record.setFormat(MOL_TYPE.SDF.toString());
		record.setContent(
		"\n  SciTegic07150813472D\n"+
		"\n"+
		"  3  0  0  0  0  0  0  0  0  0999 V2000\n"+
		"    1.4458    0.0000    0.0000 F   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		"    0.0000   -8.5542    0.0000 F   0  5  0  0  0  0  0  0  0  0  0  0\n"+
		"   10.0000   -8.5542    0.0000 Na  0  3  0  0  0  0  0  0  0  0  0  0\n"+
		"M  CHG  2   2  -1   3   1\n"+
		"M  END\n"+
		"> <CAS>\n"+
		"1333-83-1\n"+
		"\n"+
		"$$$$"
		);

		QueryLookupStructure qf = new QueryLookupStructure();
		qf.setValue(record);
		qf.setId(1);		
		return qf;
	}

	@Override
	protected void verify(QueryLookupStructure query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			IStructureRecord record = query.getObject(rs);
			Assert.assertEquals(10,record.getIdchemical());
			Assert.assertEquals(100214,record.getIdstructure());
			count++;
		}
		Assert.assertEquals(1,count);
		
	}

}
