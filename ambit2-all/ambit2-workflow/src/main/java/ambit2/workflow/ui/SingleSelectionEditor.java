/* SingleSelectionEditor.java
 * Author: nina
 * Date: Apr 7, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.workflow.ui;

import java.beans.PropertyChangeEvent;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import org.apache.poi.hssf.record.formula.functions.T;

import ambit2.base.data.SelectionBean;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.RadioButtonAdapter;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ValueHolder;

public class SingleSelectionEditor extends AbstractEditor<SelectionBean<T>> {
	protected PresentationModel<SelectionBean<T>> model ;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4805502963679177462L;
	public SingleSelectionEditor() {
		super();
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
	}
	protected void buildPanel() {
		removeAll();
		add(new JLabel(getObject().getTitle()));
		ValueHolder selectionModel = new ValueHolder();
        for (int i=0; i < getObject().getOptions().size();i++) {
        	Object t = getObject().getOptions().get(i);

	        JRadioButton radioButton = new JRadioButton(t.toString());
	        RadioButtonAdapter radioButtonAdapter = new RadioButtonAdapter(selectionModel, t);
	        radioButton.setModel(radioButtonAdapter);		
	        radioButton.setSelected(t == getObject().getSelected());
	        add(radioButton);
		}
        PropertyConnector.connect(selectionModel, "value", getObject(), "selected");
	}
	@Override
	protected void animate(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setObject(SelectionBean<T> object) {
		super.setObject(object);
		model = new PresentationModel<SelectionBean<T>>(getObject());		
		buildPanel();
	}
}
