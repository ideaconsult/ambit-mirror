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

package ambit2.dbui.test;

import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.junit.Test;

import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StoredQuery;
import ambit2.dbui.StoredQueryTableModel;
import ambit2.ui.QueryBrowser;
import ambit2.ui.table.BrowsableTableModel;

public class StoredQueryTableModelTest extends RepositoryTest {
	@Test
	public void test() throws Exception {
		;
		IStoredQuery query = new StoredQuery();
		query.setId(2);
		StoredQueryTableModel model = new StoredQueryTableModel();
		model.setConnection(datasource.getConnection());
		model.setQuery(query);
		assertTrue(model.getRowCount()>0);

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		QueryBrowser<BrowsableTableModel> browser = new QueryBrowser<BrowsableTableModel>(
				new BrowsableTableModel(model));
		browser.setPreferredSize(new Dimension(600,600));
		JOptionPane.showMessageDialog(null,browser);
	}
}
