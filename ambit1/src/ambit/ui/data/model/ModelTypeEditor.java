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

package ambit.ui.data.model;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import ambit.data.IAmbitEditor;
import ambit.data.model.ModelType;
import ambit.exceptions.AmbitException;

public class ModelTypeEditor extends JComboBox implements IAmbitEditor {
	ModelType modelType;
	public ModelTypeEditor(ModelType modelType) {
		super(modelType.predefinedvalues());
		this.modelType = modelType;
		//setPreferredSize(new Dimension(100,32));
		setMaximumSize(new Dimension(Integer.MAX_VALUE,64));
	}

	public boolean view(Component parent, boolean editable, String title) throws AmbitException {
		return
		JOptionPane.showConfirmDialog(parent,getJComponent(),"Model type",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;
	}

	public JComponent getJComponent() {
		return this;
	}

}


