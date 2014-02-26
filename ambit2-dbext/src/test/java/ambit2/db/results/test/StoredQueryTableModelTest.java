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

package ambit2.db.results.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.ISelectableRecords.SELECTION_MODE;
import ambit2.db.results.StoredQueryTableModel;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.db.search.test.QueryTest;

public class StoredQueryTableModelTest extends QueryTest<QueryStoredResults> {
	/*
	public void test() throws Exception {
		final IStoredQuery query = new StoredQuery();
		query.setId(3);
		final StoredQueryTableModel queryModel = new StoredQueryTableModel();
		Profile profile = new Profile();
		Property prop = Property.getInstance("CAS","CAS");
		prop.setLabel("CAS RN");
		prop.setClazz(String.class);
		prop.setEnabled(true);
		profile.add(prop);
		queryModel.setProfile(profile);
	
	}
*/
	@Override
	protected QueryStoredResults createQuery() throws Exception {
		QueryStoredResults q = new QueryStoredResults();
		q.setFieldname(new StoredQuery(1));
		return q;
	}

	@Test
	public void testSelect() throws Exception {
		setUpDatabase(getDbFile());
		IDatabaseConnection c = getConnection();
		testTable(c,false);
		c.close();
	}	
	
	
	public void testSelectChemicals() throws Exception {
		setUpDatabase(getDbFile());
		IDatabaseConnection c = getConnection();
		testTable(c,true);
		c.close();
	}	
		
	public void testTable(IDatabaseConnection connection,boolean chemicalsOnly) throws Exception {

		StoredQueryTableModel queryModel = new StoredQueryTableModel();
		queryModel.setChemicalsOnly(chemicalsOnly);
		/*
		Profile profile = new Profile();
		Property prop = Property.getInstance("CAS","CAS");
		prop.setLabel("CAS RN");
		prop.setClazz(String.class);
		prop.setEnabled(true);
		profile.add(prop);
		queryModel.setProfile(profile);
		*/
		queryModel.setConnection(connection.getConnection());
		queryModel.setQuery(query);
		queryModel.setValueAt(false,0,0);
		Thread.yield();
		Assert.assertFalse((Boolean)queryModel.getValueAt(0,0));
		/*
		for (int r=0;r< queryModel.getRowCount();r++)
			for (int c=0;c< queryModel.getColumnCount();c++) {
				System.out.print(r);
				System.out.print(',');
				System.out.print(c);
				System.out.print(':');
					System.out.println(queryModel.getValueAt(r,c));
			}
			*/
		queryModel.setValueAt(true,1,0);
		Assert.assertTrue((Boolean)queryModel.getValueAt(1,0));		
		/*
		UpdateExecutor e = new UpdateExecutor();
		e.setConnection(connection.getConnection());
		SelectStoredQuery toggle  = new SelectStoredQuery();
		toggle.setGroup(query.getFieldname());
		toggle.setObject(new StructureRecord(29141,129345,null,null));
		e.process(toggle);
		*/
		ITable table = 	connection.createQueryTable("EXPECTED","SELECT selected FROM query_results where selected =false");// where name='"+b.toString()+"'");
		Assert.assertEquals(1,table.getRowCount());
		
		queryModel.setSelection(SELECTION_MODE.UNSELECT_ALL);
		table = 	connection.createQueryTable("EXPECTED","SELECT selected FROM query_results where selected =false and idquery=1");
		Assert.assertEquals(2,table.getRowCount());
		
		Assert.assertFalse((Boolean)queryModel.getValueAt(1,0));
	}	
	
	@Override
	protected void verify(QueryStoredResults query, ResultSet rs)
			throws Exception {

		
	}
	
	
}
