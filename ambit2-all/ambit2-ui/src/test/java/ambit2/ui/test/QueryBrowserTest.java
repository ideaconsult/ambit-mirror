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
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.ui.QueryBrowser;
import ambit2.ui.Utils;
import ambit2.ui.table.BrowsableTableModel;
import ambit2.ui.table.IFindNavigator;

import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.lowagie.text.Image;


public class QueryBrowserTest {
	
	@Test
	public void test() throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		//UIManager.setLookAndFeel(Options.getSystemLookAndFeelClassName());
		Options.setPopupDropShadowEnabled(true);
		Options.setUseSystemFonts(true);
		TestTableModel dataModel = new TestTableModel();
		/*
		AbstractTableModel dataModel = new AbstractTableModel()  {
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
		*/

		//QueryBrowser<TestTableModel> browser = new QueryBrowser<TestTableModel>(dataModel);
		QueryBrowser<BrowsableTableModel> browser = new QueryBrowser<BrowsableTableModel>(new BrowsableTableModel(dataModel));
		browser.setPreferredSize(new Dimension(800,600));
		JOptionPane.showMessageDialog(null,browser,"",JOptionPane.PLAIN_MESSAGE,null);
	}
}

class TestTableModel extends AbstractTableModel implements IFindNavigator {
	String findValue = "Find";
	ArrayList<Integer> hits=new ArrayList<Integer>();
	int pointer;
	Hashtable<Integer, IAtomContainer> molecules;
	
	public TestTableModel() {
		molecules = new Hashtable<Integer, IAtomContainer>();
	}
	public int getColumnCount() {
		return 10;
	}
	public int getRowCount() {
		return 33;
	}
	public Object getValueAt(int row, int col) {
		try {
		if (col==1) {
			IAtomContainer a = molecules.get(row);
			if (a==null) {
				a = MoleculeFactory.makeAlkane(row+1);
				molecules.put(row,a);
			}	
			return a;
		}
		} catch (Exception x) {}
		return new Integer((row+1)*(col));
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex==1) return IAtomContainer.class;
		else return super.getColumnClass(columnIndex);
	}
	public int findNext() throws UnsupportedOperationException {
		if (!isCompleted()) {
			find();
		} if (pointer < (hits.size()-1)) {
			return hits.get(++pointer);
		} 
		return hits.get(pointer);			

	}
	public int findPrevious() throws UnsupportedOperationException {
		if (!isCompleted()) find();
		if (pointer >0) {
			return hits.get(--pointer);
		} 
		return hits.get(pointer);
	}
	public Object getValue() {
		return findValue;
	}
	public boolean isCompleted() {
		return hits.size()>0;
	}
	public void setValue(Object value) {
		findValue = value.toString();
		hits.clear();
		setCompleted(false);
	}
	public int find() throws UnsupportedOperationException {
		while (hits.size()<10) {
			int x = (int)Math.ceil(Math.random()*getRowCount());
			if (x < getRowCount())
				if (hits.indexOf(x)==-1)
					hits.add(new Integer(x));
		
		}	
		
		Collections.sort(hits);
		System.out.println(hits);
		pointer = 0;
		return hits.size();
	}
	public void setCompleted(boolean value) {
		
		
	}
	public void addPropertyChangeListener(PropertyChangeListener x) {
		// TODO Auto-generated method stub
		
	}
	public void removePropertyChangeListener(PropertyChangeListener x) {
		// TODO Auto-generated method stub
		
	}
	public boolean isFound(int record) {
		return hits.indexOf(record)>-1;
	}
}

