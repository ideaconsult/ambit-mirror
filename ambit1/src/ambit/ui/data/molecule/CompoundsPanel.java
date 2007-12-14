/*
Copyright (C) 2005-2007  

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

package ambit.ui.data.molecule;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import ambit.data.molecule.DataContainer;
import ambit.data.molecule.IAtomContainersList;
import ambit.ui.data.RandomAccessFileTableModel;

public class CompoundsPanel extends JPanel implements Observer {
	protected JPanel panel;
	protected DataContainer dataContainer;
	protected AbstractAction editAction  = null;
	public CompoundsPanel(DataContainer model, AbstractAction editAction, Color bgColor, Color fColor,int split) {
		super(new BorderLayout());
		this.dataContainer = model;
		this.editAction = editAction;
		model.addObserver(this);
		model.setSelectedIndex(model.getSelectedIndex());
		panel = createPanel(bgColor, fColor, split);
		
		add(panel,BorderLayout.CENTER);
		
	}
	protected JPanel createPanel(Color bgColor, Color fColor,int split) {
		RandomAccessFileTableModel model = new RandomAccessFileTableModel(dataContainer.getContainers());
		model.setMultiColumn(true);
		return new AtomcontainersListSpreadsheet(model,new Dimension(150,150)) {
			@Override
			protected void cellSelected(int row, int column) {
				if (row >=0)
					dataContainer.setSelectedIndex(row);
			}
			@Override
			protected void first() {
				super.first();
				dataContainer.setSelectedIndex(0);
			}
			@Override
			protected void last() {
				super.last();
				dataContainer.setSelectedIndex(dataContainer.getMoleculesCount()-1);				
			}
			@Override
			protected void nextRecord(int move) throws Exception {
				super.nextRecord(move);
				dataContainer.setSelectedIndex(dataContainer.getSelectedIndex());
			}
		};
		//return new CompoundPanel(model,editAction,bgColor,fColor,split);
	}
	public void update(Observable o, Object arg) {
		if (panel instanceof CompoundPanel) {
			((CompoundPanel)panel).update(o, arg);
		} else if (panel instanceof AtomcontainersListSpreadsheet) {
			AtomcontainersListSpreadsheet sp = ((AtomcontainersListSpreadsheet) panel);
			RandomAccessFileTableModel model = ((RandomAccessFileTableModel)sp.getTable().getModel());
			sp.label.setText(model.toString());
			IAtomContainersList l = sp.getReader();
			if (l != dataContainer.getContainers())
				model.setReader(dataContainer.getContainers());
			else
				model.fireTableDataChanged();
		}
		
	}
}


