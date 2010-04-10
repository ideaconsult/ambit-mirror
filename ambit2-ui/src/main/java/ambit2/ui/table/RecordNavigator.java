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

package ambit2.ui.table;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ambit2.ui.AbstractAmbitAction;
import ambit2.ui.AbstractPanel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RecordNavigator extends AbstractPanel<IRecordNavigator> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4228397553276899404L;
	protected JLabel label;
	protected JButton prevPage;
	protected JButton nextPage;
	protected JTextField currentPage;
	protected JTextField allPages;


	public RecordNavigator(IRecordNavigator object) {
		super(object);
	}
	public JComponent buildPanel(final IRecordNavigator navigator) {
		FormLayout layout = new FormLayout(
	            "pref, 1dlu,pref,1dlu,fill:25dlu:grow,1dlu,pref,1dlu,fill:25dlu:grow,1dlu,pref",
			"pref");
		
		PresentationModel<IRecordNavigator> presentationModel = new PresentationModel<IRecordNavigator>(navigator);
	        
		label = new JLabel("Record");
        currentPage = BasicComponentFactory.createIntegerField(
                presentationModel.getModel(IRecordNavigator.PROPERTY_RECORD));
        currentPage.setToolTipText("Current record");
        allPages = BasicComponentFactory.createIntegerField(
                presentationModel.getModel(IRecordNavigator.PROPERTY_MAXRECORDS));
        allPages.setToolTipText("All pages");
        allPages.setEditable(false);
		prevPage = new JButton(new AbstractAmbitAction("<","images/resultset_previous.png","Previous record") {			
			public void actionPerformed(ActionEvent e) {
				navigator.prev();
			}
		});
		nextPage = new JButton(new AbstractAmbitAction(">","images/resultset_next.png","Next record") {			
			public void actionPerformed(ActionEvent e) {
				navigator.next();
			}
		});		
        PanelBuilder panel = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();        
        panel.add(label, cc.xy(1,1));
        panel.add(prevPage, cc.xy(3,1));
        panel.add(currentPage, cc.xy(5,1));
        panel.add(new JLabel("/"), cc.xy(7,1));
        panel.add(allPages, cc.xy(9,1));        
        panel.add(nextPage, cc.xy(11,1));

        
        return panel.getPanel();
	}	

}
