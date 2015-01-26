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
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.vecmath.Vector2d;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.ui.QueryBrowser;
import ambit2.ui.table.BrowsableTableModel;
import ambit2.ui.table.IFindNavigator;

import com.jgoodies.looks.Options;


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

		//QueryBrowser<TestTableModel> browser = new QueryBrowser<TestTableModel>();
		//QueryBrowser<BrowsableTableModel> browser = new QueryBrowser<BrowsableTableModel>(new BrowsableTableModel(dataModel));
		QueryBrowser<BrowsableTableModel> browser = new QueryBrowser<BrowsableTableModel>() {
	     	protected int setRecord(int row, int col) {
        		int record = super.setRecord(row, col);
        		System.out.println("Record " +record);
        		return record;
        	}			
		};
		browser.setObject(new BrowsableTableModel(dataModel));
		browser.setPreferredSize(new Dimension(800,600));
		JOptionPane.showMessageDialog(null,browser,"",JOptionPane.PLAIN_MESSAGE,null);
	}
	
	public static void main(String[] args) throws Exception {
		/*
		String file = JasperCompileManager.compileReportToFile("src/test/resources/query.jrxml");
		JasperPrint jasperPrint = JasperFillManager.fillReport(file, 
				new HashMap(),
				new JRTableModelDataSource(new TestTableModel()));
		JasperViewer jasperViewer = new JasperViewer(jasperPrint);
		jasperViewer.setVisible(true);		
		*/
	}
}

class TestTableModel extends AbstractTableModel implements IFindNavigator {
	/**
     * 
     */
    private static final long serialVersionUID = -3944474322278979151L;
	String findValue = "Find";
	ArrayList<Integer> hits=new ArrayList<Integer>();
	int pointer;
	Hashtable<Integer, IMolecule> molecules;

	
	public TestTableModel() {
		molecules = new Hashtable<Integer, IMolecule>();
		molecules.put(0,MoleculeFactory.make4x3CondensedRings());
		molecules.put(1,MoleculeFactory.makeAdenine());
		molecules.put(2,MoleculeFactory.makeAlkane(6));
		molecules.put(3,MoleculeFactory.makeAlphaPinene());
		molecules.put(4,MoleculeFactory.makeAzulene());
		molecules.put(5,MoleculeFactory.makeBenzene());
		molecules.put(6,MoleculeFactory.makeBicycloRings());
		molecules.put(7,MoleculeFactory.makeBiphenyl());
		molecules.put(8,MoleculeFactory.makeBranchedAliphatic());
		molecules.put(9,MoleculeFactory.makeCyclobutadiene());
		molecules.put(10,MoleculeFactory.makeCyclobutane());
		molecules.put(11,MoleculeFactory.makeCyclohexane());
		molecules.put(12,MoleculeFactory.makeCyclohexene());
		molecules.put(13,MoleculeFactory.makeDiamantane());
		molecules.put(14,MoleculeFactory.makeEthylCyclohexane());
		molecules.put(15,MoleculeFactory.makeEthylPropylPhenantren());
		molecules.put(16,MoleculeFactory.makeFusedRings());
		molecules.put(17,MoleculeFactory.makeIndole());
		molecules.put(18,MoleculeFactory.makeMethylDecaline());
		molecules.put(19,MoleculeFactory.makePhenylAmine());
		molecules.put(20,MoleculeFactory.makePhenylEthylBenzene());
		molecules.put(21,MoleculeFactory.makePiperidine());
		molecules.put(22,MoleculeFactory.makePropylCycloPropane());
		molecules.put(23,MoleculeFactory.makePyridine());
		molecules.put(25,MoleculeFactory.makePyridineOxide());	
		molecules.put(26,MoleculeFactory.makePyrrole());
		molecules.put(27,MoleculeFactory.makeQuinone());
		molecules.put(28,MoleculeFactory.makeSingleRing());
		molecules.put(29,MoleculeFactory.makeSpiroRings());
		molecules.put(30,MoleculeFactory.makeSteran());
		molecules.put(31,MoleculeFactory.makeTetrahydropyran());
		molecules.put(32,MoleculeFactory.makeThiazole());
		
		Enumeration<Integer> e = molecules.keys();
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		 
		while (e.hasMoreElements()) {
			Integer key = e.nextElement();
			try {
				molecules.get(key).setProperty("FLAG",new Boolean(true));
				molecules.get(key).setProperty("NAME","Long Long Name "+key);
				sdg.setMolecule(molecules.get(key));
				sdg.generateCoordinates(new Vector2d(0,1));
				molecules.put(key,sdg.getMolecule());
			} catch (Exception x) {
				
			}
		}
		
	}
	public int getColumnCount() {
		return 10;
	}
	public int getRowCount() {
		return 33;
	}
	public Object getValueAt(int row, int col) {
		
		switch (col) {
		case 0: {
			if (molecules.get(row) ==null) return false;
			else return molecules.get(row).getProperty("FLAG");
		}
		case 2: {
			if (molecules.get(row) ==null) return "";
			return molecules.get(row).getProperty("NAME");
		}		
		case 4: {
			if (molecules.get(row) ==null) return false;
			else return molecules.get(row).getProperty("FLAG");
		}		
		case 1: {
			try {
			IMolecule a = molecules.get(row);
			return a;
			
			} catch (Exception x) {}
		} 
		case 3: {
			return new Double(row/10.0);
		}			
		default: {
			return new Integer((row+1)*(col+1));	
		}
		}
		
		
	}
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		try {
		
			if (columnIndex == 0)
				molecules.get(rowIndex).setProperty("FLAG",Boolean.parseBoolean(value.toString()));
			else {

				super.setValueAt(value, rowIndex, columnIndex);
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex==1) return IAtomContainer.class;
		if (columnIndex==0) return Boolean.class;
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

