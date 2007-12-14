/* AbstractAmbitEditor.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-20 
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

package ambit.ui.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;

import ambit.data.AmbitObject;
import ambit.data.IAmbitEditor;
import ambit.exceptions.AmbitException;
import ambit.ui.SpringUtilities;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-20
 */
public abstract class AbstractAmbitEditor extends JPanel implements IAmbitEditor, TableCellRenderer, TableModelListener{
    protected String caption;
    protected AmbitObject object;
	protected AbstractPropertyTableModel model;
	protected JComponent[] text;
	protected JLabel[] textLabels;
	protected JScrollPane labelPane = null;
	protected JLabel label = null;
	protected JButton button = null;
	protected Border selectedBorder = BorderFactory.createLineBorder(Color.blue);
	protected Border unselectedBorder = BorderFactory.createEmptyBorder();
	protected boolean editable=false;
    /**
     * 
     */
    public AbstractAmbitEditor(String caption, AmbitObject object) {
        super();
        this.caption = caption;
        this.object = object;
        model = createTableModel(object);
        model.addTableModelListener(this);
        addWidgets(this);
        
        
    }
    protected abstract AbstractPropertyTableModel createTableModel(AmbitObject object);
    
    protected void addWidgets(JComponent component) {
        component.setLayout(new SpringLayout());
        text = new JComponent[model.getRowCount()];
        //JPanel p = null;
        textLabels = new JLabel[model.getRowCount()];
        
        Dimension dlarge = new Dimension(100,24);
        Dimension dsmall = new Dimension(90,24);
        Dimension d = dlarge;
        
        int h = 0;
        int w = 0;
                
        int offset = 2;
        for (int i=0; i < model.getRowCount(); i++) {
        	Object o = model.getValueAt(i,1);
        	textLabels[i] = new JLabel(model.getValueAt(i,0).toString());
        	textLabels[i].setToolTipText(model.getValueAt(i,0).toString());
        	textLabels[i].setPreferredSize(dsmall);
        	textLabels[i].setMinimumSize(dsmall);
        	textLabels[i].setFocusable(false);
        	component.add(textLabels[i]);
        	
        	if (!model.isExpanded(i)) {
        		JPanel p = new JPanel(new BorderLayout());
        		JLabel t = new JLabel("");
        		if (o != null) {
        			setLabelText(t, o.toString());
        			
        		}

        		JButton b = new JButton(new ExpandAction(this,i, t) {
        			public void actionPerformed(ActionEvent e) {
        				Object o = model.getValueAt(index,1);
        				if (o==null) {
        					JOptionPane.showMessageDialog(getParentComponent(), "No data");
        					return;
        				}
        				
        				if (o instanceof AmbitObject) try {
	        					boolean ed = ((AmbitObject) o).isEditable();
	        					((AmbitObject) o).editor(ed).view(
	        							getParentComponent(), ed, model.getValueAt(index,0).toString());
	        					model.setValueAt(model.getValueAt(index,1),index, 1);
	        					setLabelText(label,model.getValueAt(index,1).toString());
        					} catch (AmbitException x) {
        						x.printStackTrace();
        					}
        				else if (o instanceof Hashtable) {
        					JScrollPane p = createEditorForHashtable((Hashtable)o);
        					JOptionPane.showConfirmDialog(getParentComponent(),
        							p,
        							model.getValueAt(index, 0).toString(),
        							JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE); 
        					setLabelText(label,model.getValueAt(index,1).toString());
        				}
        				//model.fireTableRowsUpdated(index,index);
        				
        			}
        		});
        		b.setMinimumSize(dsmall);
        		p.setMinimumSize(dlarge);
        		p.setPreferredSize(new Dimension(Integer.MAX_VALUE,32));
        		p.add(t,BorderLayout.CENTER);
        		p.add(b,BorderLayout.EAST);
        		textLabels[i].setLabelFor(p);
        		text[i] = p;
        	} else
        		if (o instanceof Hashtable) {
        			text[i] = createEditorForHashtable((Hashtable) o);
        			text[i].setPreferredSize(new Dimension(100,200));
        		} else if (o instanceof AmbitObject) {
	        	    text[i] = ((AmbitObject) o).editor(((AmbitObject) o).isEditable()).getJComponent();
	        	    //text[i].setBorder(BorderFactory.createTitledBorder(model.getValueAt(i,0).toString()));
	        	    text[i].setBorder(BorderFactory.createEtchedBorder());
	        	    
	        	} else {
	        		Object value = model.getValueAt(i,1);
	        		if (value == null) value = "";
	        		JComponent t = null;
	        		if (value instanceof Boolean) {
	        			t =new JCheckBox("",((Boolean)value).booleanValue());
	        			((JCheckBox) t).addItemListener(new ItemListener() {
	        				public void itemStateChanged(ItemEvent e) {
	        					JCheckBox source = (JCheckBox) e.getItemSelectable();
			        			model.setValueAt(source.isSelected(),
			        					Integer.parseInt(source.getName()), 
			        					1);      					
	        				}        				
	        			}
	        			); 
	        		} else {	
	        			t =new JFormattedTextField(value.toString() );
		        	    ((JFormattedTextField)t).setEditable((object != null) && object.isEditable());
		        	    ((JFormattedTextField)t).setFocusLostBehavior(JFormattedTextField.COMMIT);
		        	    ((JFormattedTextField)t).addActionListener(new ActionListener() {
			        		public void actionPerformed(java.awt.event.ActionEvent e) {
			        			
			        			model.setValueAt(((JTextField)e.getSource()).getText(), 
			        					Integer.parseInt(e.getActionCommand()), 
			        					1);
			        		};
			        	});
	        		}   	
		           	
		           	t.setPreferredSize(d);
		        	t.setMaximumSize(new Dimension(Integer.MAX_VALUE,24));
		        	t.setMinimumSize(new Dimension(32,24));
		           	
		            	        	
		        	text[i] = t;
		        	textLabels[i].setLabelFor(text[i]);
		        	text[i].setToolTipText(value.toString());
	        	}
        		
	 
	        	text[i].setName(Integer.toString(i));
		        text[i].addPropertyChangeListener("value",new PropertyChangeListener() {
	        		public void propertyChange(PropertyChangeEvent evt) {
	        			for (int i=0; i < text.length;i++)
	       				if ((text[i]!=null) && (text[i].equals(evt.getSource())))
		        			try {
		        			model.setValueAt(evt.getNewValue(), 
		        					Integer.parseInt(
		        							((JComponent)(evt.getSource())).getName()), 
		        					1);
		        			} catch (Exception x) {
		        				
		        			}
	        			
	        		}
        	});
        	component.add(text[i]);
        	/*
        	h += (text[i].getPreferredSize().height +offset);
        	
        	if (w < text[i].getPreferredSize().width) w = text[i].getPreferredSize().width;
			*/
        }
        /*
        Dimension d1 = new Dimension(w,h);
        component.setMinimumSize(d1);
        component.setPreferredSize(d1);
        */
        SpringUtilities.makeCompactGrid(component, model.getRowCount(), 2, 2,2, offset,offset);
        
        
        
    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#view(javax.swing.JComponent, boolean)
     */
    public boolean view(Component parent, boolean editable, String title) throws AmbitException {
        return 
        (JOptionPane.showConfirmDialog(parent,getJComponent(),caption,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION);

    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitEditor#getJComponent()
     */
    public JComponent getJComponent() {
        return this;
    }
	public AmbitObject getObject() {
		return object;
	}
	public void setObject(AmbitObject object) {
		this.object = object;
		model.setObject(object);
	}
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (label == null) { 
			label = new JLabel(); //label.setEditable(false); 
			labelPane = new JScrollPane(label);
			
		}
		if (value instanceof AmbitObject)  {
			setObject((AmbitObject)value);
			StringBuffer b = new StringBuffer();
			b.append("<html><table color=#000000 border=0>");
			for (int rowIndex=0; rowIndex < model.getRowCount(); rowIndex++) {
				b.append("<tr color=\"");
				b.append("#0000FF");
				b.append("\"><td ><b><i>");
				b.append(model.getValueAt(rowIndex, 0).toString());
				b.append(":</i></b></td><td border=0>");
				
				b.append(model.getValueAt(rowIndex, 1).toString());
				b.append("</td></tr>");
			}
			b.append("</html>");
			label.setText(b.toString());
		}
		if (isSelected) label.setBorder(selectedBorder);
		else label.setBorder(unselectedBorder);
		return label;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	protected JScrollPane createEditorForHashtable(Hashtable hashtable) {
		HashtableModel hashtableModel = new HashtableModel(hashtable) {
			public String getColumnName(int arg0) { 
				switch (arg0) {
				case 0: return "Name";
				case 1: return "Value";
				default: return "";
				}
			};
		};
		JTable table = new JTable(hashtableModel);
        table.getTableHeader().setReorderingAllowed(false);
		table.setSurrendersFocusOnKeystroke(true);
		JScrollPane t = new JScrollPane(table);
		t.setPreferredSize(new Dimension(100,200));
		return t;
	}
	protected void setLabelText(JLabel t, String s) {
		if (s.length() > 40)
			t.setText(s.substring(0,40));
		else t.setText(s);
		t.setToolTipText(s);

	}
	public void tableChanged(TableModelEvent e) {
		if (textLabels == null) return;
		for (int i=0; i < model.getRowCount();i++) 
			textLabels[i].setText(model.getValueAt(i,0).toString());
		
	}
	
}

abstract class ExpandAction extends AbstractAction {
	protected int index;
	protected JComponent parentComponent;
	protected JLabel label;
	public JComponent getParentComponent() {
		return parentComponent;
	}

	public void setParentComponent(JComponent parentComponent) {
		this.parentComponent = parentComponent;
	}

	public ExpandAction(JComponent parent, int index, JLabel label) {
		super("...");
		setParentComponent(parent);
		this.index = index;
		this.label = label;
	}

}
