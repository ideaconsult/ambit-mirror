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

package ambit2.ui.test;

import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;

import org.junit.Test;

import ambit2.ui.QueryBrowser;
import ambit2.ui.table.BrowsableTableModel;


public class QueryBrowserTest {
	
	@Test
	public void test() throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		AbstractTableModel dataModel = new AbstractTableModel() {
			public int getColumnCount() {
				return 10;
			}
			public int getRowCount() {
				return 33;
			}
			public Object getValueAt(int row, int col) {
				return new Integer((row+1)*(col));
			}
		};

		//QueryBrowser<TestTableModel> browser = new QueryBrowser<TestTableModel>(dataModel);
		QueryBrowser<BrowsableTableModel> browser = new QueryBrowser<BrowsableTableModel>(new BrowsableTableModel(dataModel));
		browser.setPreferredSize(new Dimension(800,600));
		JOptionPane.showMessageDialog(null,browser,"",JOptionPane.PLAIN_MESSAGE,null);
	}
}

