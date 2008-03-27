/* HashtablePanel.java
 * Author: Nina Jeliazkova
 * Date: Nov 6, 2006 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
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

package ambit2.ui.data;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ambit2.data.molecule.PropertyTranslator;
import ambit2.ui.UITools;

public class HashtablePanel extends JPanel implements Observer {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3677239252527149540L;
	protected String selectedProperty;
    protected PropertyTranslator properties;
    protected String type;
    protected JTable table;
    protected JToolBar toolbar;
    protected String caption;
    public HashtablePanel(PropertyTranslator properties,final String type, String caption, String[] options) {
        super(new BorderLayout());
        this.caption = caption;
        this.type = type;
        this.properties = properties;
        properties.addObserver(this);
        this.table = new JTable(new HashtableModel(properties.getProperties(type),true) {
        		
        		@Override
    	    	public String getColumnName(int arg0) {
    	    		switch (arg0) {
    	    		case 0: return "Field";
    	    		case 1: if (type ==null) return "Value"; else return "New name";
    	    		default: return "";
    	    		}
    	    	}
        		
    	    	public boolean isCellEditable(int rowIndex, int columnIndex) {
    	    		return (columnIndex>0) && (type!=null);
    	    	}

        },createColumnModel(options)) {
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
        
        m.addListSelectionListener(new ListSelectionListener() {
           /* (non-Javadoc)
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) return;

            ListSelectionModel lsm =
                (ListSelectionModel)e.getSource();
            if (!lsm.isSelectionEmpty()) { 
                setSelected(lsm.getMinSelectionIndex());
            }
        }});
        this.table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
             if (e.getClickCount() == 2){
                System.out.println(" double click" );
                }
             }
            } );

        
        if (type != null) {
        
	        toolbar = new JToolBar();
	        toolbar.setOrientation(JToolBar.VERTICAL);
	        toolbar.setFloatable(false);
	        //left.setLayout(new BoxLayout(left,BoxLayout.PAGE_AXIS));
	        JButton b;
	        Dimension d = new Dimension(72,24);
	        toolbar.setPreferredSize(d);
	        add(toolbar,BorderLayout.WEST);
	     
	        
	        b = new JButton(new AbstractAction("",UITools.createImageIcon("ambit2/ui/images/arrowright_green_16.png")){

	            /* (non-Javadoc)
	             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	             */
	            public void actionPerformed(ActionEvent e) {
	                moveTo();
	            }
	        });
	        b.setToolTipText("Move selected property to "+caption);
	        b.setPreferredSize(d);
	        toolbar.add(b);
	        b = new JButton(new AbstractAction("",UITools.createImageIcon("ambit2/ui/images/arrowleft_green_16.png")){
	            
	            /* (non-Javadoc)
	             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	             */
	            public void actionPerformed(ActionEvent e) {
	                moveBack();
	            }
	            
	        });
	        b.setPreferredSize(d);
	        b.setToolTipText("Move selected "+caption + " back to the property studyList");
            toolbar.add(b);
            
            b = new JButton(new AbstractAction("Guess"){
                /* (non-Javadoc)
                 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
                 */
                public void actionPerformed(ActionEvent e) {
                    guess();
                }
            });
            b.setPreferredSize(d);
            b.setToolTipText("Guess which properties can be "+caption);
            toolbar.add(b);
            
            b = new JButton(new AbstractAction("Move all"){
                /* (non-Javadoc)
                 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
                 */
                public void actionPerformed(ActionEvent e) {
                    moveAllTo();
                }
            });
            b.setPreferredSize(d);
            b.setToolTipText("Move all properties to "+caption);
            
            
	        toolbar.add(b);
        }   
        
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
    protected void setSelected(int selectedIndex) {
        selectedProperty = table.getValueAt(selectedIndex,0).toString();
        if (type==null) properties.setSelectedProperty(selectedProperty);
        //System.out.println("Selected " +selectedProperty);
    }
    protected void moveTo() {
    	   //System.out.println(properties.getSelectedProperty());
        properties.moveTo(type,properties.getSelectedProperty());
        properties.setSelectedProperty(null);
    }
    protected void moveAllTo() {
       //System.out.println(properties.getSelectedProperty());
     properties.moveTo(type);
     properties.setSelectedProperty(null);
 }    
    
    protected void moveBack() {
        properties.moveBack(type,selectedProperty);
        selectedProperty = null;
    }
    protected void guess() {
    	properties.guess(type);
    }     

    public void update(Observable o, Object arg) {
    	((HashtableModel)table.getModel()).setTable(properties.getProperties(type));
    	
    }
}