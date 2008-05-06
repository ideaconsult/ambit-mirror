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

package ui.data;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.InputStream;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ambit2.ui.data.ExperimentsPanel;
import ambit2.ui.data.HashtableModel;
import ambit2.ui.data.MolPropertiesPanel;
import ambit2.ui.data.QSARPanel;
import ambit2.ui.data.model.ModelTable;
import ambit2.ui.editors.AbstractAmbitEditor;
import ambit2.ui.editors.StudyPanel;
import ambit2.data.experiment.Study;
import ambit2.data.model.Model;
import ambit2.data.molecule.MolProperties;
import ambit2.exceptions.PropertyNotInTemplateException;
import ambit2.io.IColumnTypeSelection;
import ambit2.ui.UITools;

public class QSARMolPropertiesPanel extends MolPropertiesPanel {
    protected JTable qsarTable;
    protected QSARPanel qsarPanel ;
    protected StudyPanel studyPanel;

	public QSARMolPropertiesPanel(MolProperties properties) {
		super(properties);
	}
	protected void addWidgets(MolProperties properties) {
	    this.properties = properties;
	    setLayout(new BorderLayout());
	    propsTable = new JTable(new HashtableModel(properties.getProperties()) {
	    	public String getColumnName(int arg0) {
	    		switch (arg0) {
	    		case 0: return "Property";
	    		case 1: return "Type or value";
	    		default: return "";
	    		}
	    	}
	    },createColumnModel(null));
	    propsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    ListSelectionModel m = new DefaultListSelectionModel();
	    propsTable.setSelectionModel(m);
	    m.addListSelectionListener(new ListSelectionListener() {
	       /* (non-Javadoc)
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e) {
 	        if (e.getValueIsAdjusting()) return;

 	        ListSelectionModel lsm =
 	            (ListSelectionModel)e.getSource();
 	        if (!lsm.isSelectionEmpty()) { 
 	        	setSelected(propsTable.getValueAt(lsm.getMinSelectionIndex(),0));
 	        }
	    }});
     	
	    JScrollPane left = new JScrollPane(propsTable);
	    left.setBorder(BorderFactory.createTitledBorder("All available properties"));
	    left.setMinimumSize(new Dimension(100,100));
		
	    idsTable = new JTable(new HashtableModel(properties.getIdentifiers()),
	            createColumnModel(
	    	            new String[] {
	    	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctSMILES],
	    	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctChemName],
	    	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctCAS],
	    	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctRowID],
	    	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctUnknown]}
	                    ));
		descriptorsTable = new JTable(new HashtableModel(properties.getDescriptors(),true),
		        createColumnModel(
			            new String[] {
			            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctX],
			            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctUnknown]
			                                               }		                
		                ));
	    experimentsTable = new JTable(new HashtableModel(properties.getExperimental()));
	    qsarTable = new JTable(new HashtableModel(properties.getQSAR()),
	            createColumnModel(
	                    new String[] {
	            	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctYpredicted],
	            	            //IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctYobserved],
	            	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctYresidual],
	            	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctEquation],
	            	            IColumnTypeSelection._columnTypesS[IColumnTypeSelection._ctUnknown]
	            	                                               }	                    
	            ));
		right = new JTabbedPane();
		right.setBorder(BorderFactory.createTitledBorder("QSAR model"));
		

        AbstractAmbitEditor ae = new AbstractAmbitEditor("QSAR Model",properties.getQsarModel()) {
        	protected AbstractPropertyTableModel createTableModel(ambit2.data.AmbitObject object) {
        		return new ModelTable((Model)object);
        	};
        };
        ae.setLayout(new BoxLayout(ae,BoxLayout.PAGE_AXIS));
        ae.setPreferredSize(new Dimension(400,(int)ae.getPreferredSize().getHeight()));
        right.add("QSAR Model",ae);

        studyPanel = new StudyPanel(properties.getQsarModel().getStudy());
        right.add("Study",studyPanel);
        qsarPanel = (QSARPanel) createQSARPanel(qsarTable);
		right.add("Predictions",qsarPanel);
		
		right.add("Identifiers",createIDsPanel(idsTable));
		
		right.add("Descriptors",createDescriptorsPanel(descriptorsTable));
		
		right.add("Observed",createExperimentsPanel(experimentsTable));
		
		
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,left,right);
		pane.setDividerLocation(200);
		
		add(pane,BorderLayout.CENTER);
		add(loadsaveButtons(),BorderLayout.SOUTH);
		
	}
	/* (non-Javadoc)
     * @see ambit2.ui.data.MolPropertiesPanel#setProperties(ambit2.data.molecule.MolProperties)
     */
    protected void setProperties(MolProperties newProps) throws Exception {
        super.setProperties(newProps);
        
        ((HashtableModel) qsarTable.getModel()).setTable(properties.getQSAR());
        
    }
	
	public JPanel createExperimentsPanel(JTable table) {
	    return new QSARObservedPanel(properties,table,"Observed")            {
	    	
	        /* (non-Javadoc)
             * @see ambit2.ui.data.HashtablePanel#moveTo()
             */
            protected void moveTo() {
                try {
                	try {
                		properties.moveToExperimental(properties.getSelectedProperty());
	            	} catch (PropertyNotInTemplateException x) {
	            		handleMissingProperty(x);
	            	} 
                    HashtableModel model = (HashtableModel)propsTable.getModel();
                    model.setTable(model.getTable());                    
                    super.moveTo();
                } catch (Exception x) {
                    JOptionPane.showMessageDialog(this,x.getMessage());
                }
            }
            protected void moveBack() {
            	super.moveBack();
            	HashtableModel model = (HashtableModel)propsTable.getModel();
                model.setTable(model.getTable());            	
            }
            protected void guess() {
            	try {
	            	try {
	            		properties.guessExperiments();
	            	} catch (PropertyNotInTemplateException x) {
	            		handleMissingProperty(x);
	            	} 
            	} catch (Exception x) {
            		
            	}
            	super.guess();
            	HashtableModel model = (HashtableModel)propsTable.getModel();
                model.setTable(model.getTable());            	
            }
            
	    };
	}		
}

class QSARObservedPanel extends ExperimentsPanel {
	public QSARObservedPanel(MolProperties properties, JTable table,String caption) {
		super(properties,table,caption);
		
        JButton b = new JButton(new AbstractAction("Study \""+properties.getQsarModel().getStudy() + '"'
        		,UITools.createImageIcon("ambit2/ui/images/template.png")){
	        /* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                Study t = showStudy();
                if (t != null) {

                	putValue(AbstractAction.NAME, "Study \""+t+'"');
                }
            }
	    });
        b.setToolTipText("Experimental data recognition depends on a template. Click here to view the current template or load another one.");
        add(b,BorderLayout.SOUTH);
	}
	protected Study showStudy() {
		StudyPanel p = new StudyPanel(properties.getQsarModel().getStudy());
		JOptionPane.showMessageDialog(this,p,"Study",JOptionPane.PLAIN_MESSAGE);
    	properties.setTemplate(properties.getQsarModel().getStudy().getTemplate());
		return properties.getQsarModel().getStudy();
	}
}


