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

package ambit2.ui.editors;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import ambit2.base.config.Preferences;
import ambit2.base.config.Preferences.VINDEX;
import ambit2.base.config.Preferences.VTAGS;
import ambit2.base.interfaces.IAmbitEditor;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class PreferencesPanel implements IAmbitEditor<Properties>  {
	/**
	 * 
	 */
	protected JComponent component;
	private static final long serialVersionUID = -5074505644698297727L;
	
	public PreferencesPanel() {
		this(VTAGS.values());
	}
	public PreferencesPanel(VTAGS[] tags) {
		setObject(Preferences.getProperties());
		JTabbedPane tp = new JTabbedPane();
		for (VTAGS tag:tags)  tp.addTab(tag.toString(), addWidgets(tag));
		component = tp;
	}
	public boolean isEditable() {
		return true;
	}
	public void setEditable(boolean editable) {
	}
	public JComponent getJComponent() {
		return component;
	}
	public boolean confirm() {
		return true;
	}
	public Properties getObject() {
		return Preferences.getProperties();
	}
	public void setObject(Properties object) {
		
		
	}
	protected JComponent addWidgets(VTAGS tag) {
			int rows = 0;
			
			StringBuilder layout = new StringBuilder();
			String d = "";
			for (int i=0; i < Preferences.default_values.length; i++)  
				if (!tag.equals(Preferences.default_values[i][VINDEX.TAG.ordinal()])) continue;
				else
					if (!(Boolean)Preferences.default_values[i][VINDEX.HIDDEN.ordinal()]) {
						layout.append(d);
						layout.append("pref");
						d = ",";
					}
			
	        FormLayout formlayout = new FormLayout(
	                "right:pref, 3dlu, 350dlu:grow",
	                layout.toString());
	        CellConstraints cc = new CellConstraints();
	        PanelBuilder builder = new PanelBuilder(formlayout);
   			for (int i=0; i < Preferences.default_values.length; i++) {
				if (!tag.equals(Preferences.default_values[i][VINDEX.TAG.ordinal()])) continue;
   				if ((Boolean)Preferences.default_values[i][5]) continue;//hidden
   				rows++;
   				JLabel l = new JLabel(Preferences.default_values[i][1].toString());
   				l.setToolTipText(Preferences.default_values[i][4].toString());
   		        //l.setAlignmentX(CENTER_ALIGNMENT);
   		        l.setPreferredSize(new Dimension(200,24));
   				builder.add(l,cc.xy(1,rows));
   				JComponent c = null;
   				if (Preferences.default_values[i][3] == Boolean.class) {
   					Action a = new CheckBoxAction(i);
   					c = new JCheckBox(a);
   					((JCheckBox)c).setSelected(Boolean.parseBoolean(
   							Preferences.getProperty(Preferences.default_values[i][0].toString())));
   					c.setToolTipText(Preferences.default_values[i][4].toString());   					
   				} else if (Preferences.default_values[i][3] == String.class) {	
   					c = new JFormattedTextField(Preferences.getProperty(Preferences.default_values[i][0].toString()));
   					c.setToolTipText(Preferences.default_values[i][4].toString());
   					c.addPropertyChangeListener("value", new TextPropertyChangeListener(i));

//   					c.setPreferredSize(d);
 //  					c.setMaximumSize(d);
   				}	
   				//c.setPreferredSize(d);
   				builder.add(c,cc.xy(3,rows));
   			}
   			return builder.getPanel();
	
	}
}


class TextPropertyChangeListener implements PropertyChangeListener {
	protected int index = -1;
	public TextPropertyChangeListener(int index) {
		this.index = index;
	}
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof JFormattedTextField) { 
			Preferences.setProperty(Preferences.default_values[index][0].toString(),
					((JFormattedTextField)evt.getSource()).getValue().toString()
					);
		}	
		
	}
}

class CheckBoxAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int index = -1;
	public CheckBoxAction(int index) {
		this.index = index;
	}
		public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox)e.getSource();
	            Preferences.setProperty(Preferences.default_values[index][0].toString(),
	            		new Boolean(cb.isSelected()).toString());
			}

}