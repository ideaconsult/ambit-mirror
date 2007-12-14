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

package ambit.database.aquire;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ambit.data.IAmbitEditor;
import ambit.data.experiment.Experiment;
import ambit.exceptions.AmbitException;
import ambit.ui.data.HashtableModel;
import ambit.ui.data.HashtablePanel;

public class ExperimentEditor extends JPanel implements IAmbitEditor {
	protected Experiment experiment;
	protected JTable table;
	public ExperimentEditor(Experiment experiment, boolean editable) {
		super(new BorderLayout());
		setExperiment(experiment);
		setEditable(editable);
		addWidgets();
	}
	protected void addWidgets() {
	      this.table = new JTable(new HashtableModel(experiment.getResults(),true) {
      		
      		@Override
  	    	public String getColumnName(int arg0) {
  	    		switch (arg0) {
  	    		case 0: return "Name";
  	    		case 1: return "Value";
  	    		default: return "";
  	    		}
  	    	}
      		
  	    	public boolean isCellEditable(int rowIndex, int columnIndex) {
  	    		return isEditable();
  	    	}

	      },createColumnModel(null)) {
  		@Override
  		public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
  			Component c = super.prepareRenderer(renderer, row, column);
  			if ((column>0) && (c instanceof JComponent)) {
  				JComponent jc = (JComponent)c;
  				jc.setToolTipText(getValueAt(row, 0).toString()+ " = "+getValueAt(row, 1).toString());
  			}
  			return c;
  		}        	

      };
	    table.setSurrendersFocusOnKeystroke(true);
	    JScrollPane sp = new JScrollPane(table);
	    sp.setMinimumSize(new Dimension(200,200));
	    sp.setPreferredSize(new Dimension(250,250));
	    add(sp,BorderLayout.CENTER);
	    table.setToolTipText("<html>Select row and then click move buttons to move the property>/html>");
	    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    ListSelectionModel m = new DefaultListSelectionModel();
	    table.setSelectionModel(m);
	}
	public JComponent getJComponent() {
		return this;
	}

	public boolean isEditable() {
		if (experiment != null)
			return experiment.isEditable();
		else return false;
	}

	public void setEditable(boolean editable) {
		experiment.setEditable(editable);

	}

	public boolean view(Component parent, boolean editable, String title)
			throws AmbitException {
		// TODO Auto-generated method stub
		return false;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	public TableColumnModel createColumnModel(String[] options) {
	    TableColumnModel m = new DefaultTableColumnModel();
        
        TableColumn c = new TableColumn(0);
	    c.setHeaderValue("Property name");
	    m.addColumn(c);
	    m.addColumn(new TableColumn(1));
        JComboBox comboBox;
        
        if (options != null) {
			comboBox = new JComboBox();
			for (int i =0; i < options.length; i++)
				comboBox.addItem(options[i]);
			comboBox.setEnabled(true);
			m.getColumn(1).setCellEditor(new DefaultCellEditor(comboBox));
        }
        //else m.getColumn(1).setCellEditor(new AmbitCellEditor());
        return m;
	}
}


