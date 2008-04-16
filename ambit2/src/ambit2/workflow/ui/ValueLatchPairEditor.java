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

package ambit2.workflow.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JToolBar;

import ambit2.config.EditorPreferences;
import ambit2.ui.EditorPanel;
import ambit2.ui.editors.IAmbitEditor;

import com.microworkflow.events.WorkflowContextEvent;
import com.microworkflow.process.ValueLatchPair;
import com.microworkflow.ui.IWorkflowContextFactory;

public class ValueLatchPairEditor<T> extends AbstractEditor<ValueLatchPair<T>>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3962390331684939901L;
	protected EditorPanel editorPanel = new EditorPanel();
	protected JButton nextButton ;
	public ValueLatchPairEditor() {
		this(null);
	}
	public ValueLatchPairEditor(IWorkflowContextFactory wfcfactory) {
		setLayout(new BorderLayout());
		setWfcfactory(wfcfactory);
		nextButton = new JButton(new AbstractAction("Next") {
			public void actionPerformed(ActionEvent e) {
				//this should open the latch
				getObject().getLatch().setValue(getObject().getValue());
				setObject(null);
			}
		});
		add(nextButton,BorderLayout.SOUTH);
		
		editorPanel.setPreferredSize(new Dimension(200,200));
		add(editorPanel, BorderLayout.CENTER);
		setAnimate(true);
	}
	
	@Override
	public void setObject(ValueLatchPair<T> object) {
		super.setObject(object);
		if (object == null) {
			editorPanel.setEditor(null);
			nextButton.setVisible(false);
		}
		else 
			try {
				IAmbitEditor editor = EditorPreferences.getEditor(object.getValue());
				editorPanel.setEditor(editor);
				nextButton.setVisible(true);
			} catch (Exception x) {
				editorPanel.setEditor(null);
				nextButton.setVisible(false);
				x.printStackTrace();
			}		
	}
    public void propertyChange(PropertyChangeEvent arg0) {
        if (WorkflowContextEvent.WF_ANIMATE.equals(arg0.getPropertyName()))
            setAnimate((Boolean) arg0.getNewValue());
       	animate(arg0);
    }	
	@Override
	protected void animate(PropertyChangeEvent arg0) {
		System.out.println(arg0);
		if (arg0.getNewValue() instanceof ValueLatchPair) 
			setObject(((ValueLatchPair)arg0.getNewValue()));
		
	}
	@Override
	public void clear() {
		if (this.object != null)
			setObject(null);
		
	}
}


