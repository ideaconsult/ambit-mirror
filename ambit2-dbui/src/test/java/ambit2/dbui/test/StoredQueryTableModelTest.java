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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.db.results.StoredQueryTableModel;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.ui.QueryBrowser;
import ambit2.ui.table.BrowsableTableModel;

public class StoredQueryTableModelTest extends RepositoryTest {
	@Test
	public void test() throws Exception {
		final IStoredQuery query = new StoredQuery();
		query.setId(93);//83);//142
		final StoredQueryTableModel queryModel = new StoredQueryTableModel();
		Profile profile = new Profile();
		Property prop = Property.getInstance("CAS","CAS");
		prop.setLabel("CAS RN");
		prop.setClazz(String.class);
		prop.setEnabled(true);
		profile.add(prop);
		prop =Property.getInstance("PUBCHEM_COMPOUND_CID", "PUBCHEM_COMPOUND_CID");
		prop.setEnabled(true);
		
		profile.add(prop);
		queryModel.setProfile("test",profile);

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		QueryBrowser<BrowsableTableModel> browser = new QueryBrowser<BrowsableTableModel>(
				new BrowsableTableModel(queryModel));
		/*		
        QueryBrowser<StoredQueryTableModel> browser = new QueryBrowser<StoredQueryTableModel>(
                queryModel);
                */		
		browser.setPreferredSize(new Dimension(600,600));
		
		JButton button = new JButton(new AbstractAction("Connect") {
		    public void actionPerformed(ActionEvent e) {
		        try {
                    queryModel.setConnection(datasource.getConnection());
                    queryModel.setQuery((AbstractStructureQuery)query.getQuery());
                    assertTrue(queryModel.getRowCount()>0);		    
		        } catch (Exception x) {
		            Assert.fail(x.getMessage());
		        }
		}});
		
		JPanel p = new JPanel(new BorderLayout());
		p.add(browser,BorderLayout.CENTER);
		p.add(button,BorderLayout.NORTH);
		JOptionPane.showMessageDialog(null,p);
	}
	
	
}
