/*
Copyright (C) 2005-2006  

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

package ambit2.test.database.readers;

import junit.framework.TestCase;
import ambit2.database.DbConnection;
import ambit2.database.processors.ExperimentSearchProcessor;
import ambit2.database.query.ExperimentQuery;
import ambit2.database.query.TemplateFieldQuery;
import ambit2.database.search.DbSearchExperiments;
import ambit2.test.ITestDB;


public class DbSearchExperimentsTest extends TestCase {
	public void test() {
	    try {
	        DbConnection conn = new DbConnection(ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"");
			conn.open(true);
	        ExperimentSearchProcessor esp = new ExperimentSearchProcessor(conn.getConn());
			ExperimentQuery experiments = new ExperimentQuery();
			TemplateFieldQuery q = new TemplateFieldQuery("Endpoint");
			q.setResult(false);
			q.setNumeric(false);
			q.setCondition("ALL");
			q.setEnabled(true);
			esp.readFieldname(q);
			
			System.out.println(q);
			
			ExperimentQuery l = new ExperimentQuery();
			l.setCombineWithAND(true);
			l.addItem(q);

			DbSearchExperiments reader = new DbSearchExperiments( conn.getConn(),l,null,0,10);
			while (reader.hasNext()) {
			    //Object object = processors.process(reader.next());
			    //System.out.println(((IChemObject) object).getProperties());
				Object o = reader.next();
				System.out.println(o);
			}
			reader.close();
			
			conn.close();
	    } catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}

}


