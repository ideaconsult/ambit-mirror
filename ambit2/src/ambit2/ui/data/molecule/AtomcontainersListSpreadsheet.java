/* AtomcontainersListSpreadsheet.java
 * Author: Nina Jeliazkova
 * Date: Mar 11, 2007 
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

package ambit2.ui.data.molecule;


import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.data.molecule.IAtomContainersList;
import ambit2.data.molecule.IMoleculesIterator;
import ambit2.ui.AmbitColors;
import ambit2.ui.UITools;
import ambit2.ui.actions.process.MoleculeEditAction;
import ambit2.ui.data.ImageCellRenderer;
import ambit2.ui.data.MoleculeGridCellRenderer;
import ambit2.ui.data.PropertiesCellRenderer;
import ambit2.ui.data.RandomAccessFileTableModel;

public class AtomcontainersListSpreadsheet extends JPanel {

    protected JTable table;

    protected JLabel label;

    protected boolean spreadsheet = true;

    protected Dimension cellSize;
    
    public AtomcontainersListSpreadsheet(RandomAccessFileTableModel model, Dimension cellSize) {
        super(new BorderLayout());
        this.cellSize = cellSize;
        table = addWidgets(model);
        
        label = new JLabel(model.toString());
        JScrollPane p = new JScrollPane(table);
        p.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        table.setPreferredScrollableViewportSize(new Dimension(600, 200));
        table.setDefaultRenderer(Hashtable.class, new PropertiesCellRenderer());
        table.setDefaultEditor(Hashtable.class, new PropertiesCellRenderer());
        final String title = "Sizing";
        table.getInputMap().put(      
        		KeyStroke.getKeyStroke(KeyEvent.VK_S , InputEvent.CTRL_MASK) ,
        		title);    
        table.getActionMap().put(title, new AbstractAction(title) {
            public void actionPerformed(ActionEvent e) {
                RandomAccessFileTableModel model = (RandomAccessFileTableModel) table.getModel();
                IAtomContainersList list = model.getReader();
                if (list == null) return;
                else {
                	int record = list.getSelectedIndex();
                	if (record >=0) {
        	        	String size = JOptionPane.showInputDialog(
        	        			table,
        	        			"Setting the height of the row: " + record , 
        	        			table.getRowHeight(record) );         
        	        	if(size == null) return; 
        	        	int h = Integer.parseInt(size);
        	        	table.setRowHeight(record, h);
        	        	setCellHeight(h);
                	}
                }
            }  
            }
        ); 
        HeightListener hl = new HeightListener();
        table.addMouseListener(hl);
        table.addMouseMotionListener(hl);
        table.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		super.mouseClicked(e);
       			cellSelected(table.rowAtPoint(e.getPoint()), table.columnAtPoint(e.getPoint()));
        	}
        });
        add(p, BorderLayout.CENTER);
        add(label, BorderLayout.NORTH);
        add(addControls(), BorderLayout.SOUTH);
        setMinimumSize(new Dimension(200, 200));
        setSpreadsheet(false);        
    }
    protected void setCellHeight(int h) {
    	cellSize.height = h;
    	
    }
    protected void cellSelected(int row,int column) {
    	
    }
    protected IAtomContainersList getReader() {
        RandomAccessFileTableModel model = (RandomAccessFileTableModel) table.getModel();
        if (model.getRowCount() > 0) return model.getReader(); else return null;
    }
    protected JComponent addControls() {
        JToolBar p = new JToolBar();
        p.setFloatable(false);
        p.add(new MoleculesTableAction("Info",UITools.createImageIcon("ambit2/ui/images/dataset.png"),"Dataset information") {
            public void actionPerformed(ActionEvent e) {
                info();
            }
        });                     
        p.add(new MoleculesTableAction("First",UITools.createImageIcon("ambit2/ui/images/resultset_first.png"),
                "Go to the first record") {
            public void actionPerformed(ActionEvent e) {
                first();
            }
        });
        p.add(new MoleculesTableAction("Previous",UITools.createImageIcon("ambit2/ui/images/resultset_previous.png"),
        	"Go to the previous record") {
	    public void actionPerformed(ActionEvent e) {
	    	try {
	        nextRecord(-1);
	    	} catch (Exception x) {
	    		x.printStackTrace();
	    	}
	    }
        });        
        p.add(new MoleculesTableAction("Previous",UITools.createImageIcon("ambit2/ui/images/resultset_next.png"),
	    	"Go to the next record") {
	    public void actionPerformed(ActionEvent e) {
	    	try {
		        nextRecord(1);
		    } catch (Exception x) {
		    		x.printStackTrace();
		    }
	    }
	    });         
        p.add(new MoleculesTableAction("Last",UITools.createImageIcon("ambit2/ui/images/resultset_last.png"),"Go to the last record") {
            public void actionPerformed(ActionEvent e) {
                last();
            }
        });
        p.add(new MoleculesTableAction("Go to record",UITools.createImageIcon("ambit2/ui/images/enter.png"),"Go to record number") {
            public void actionPerformed(ActionEvent e) {
                gotoRecord();
            }
        });
        p.add(new MoleculesTableAction("Search",UITools.createImageIcon("ambit2/ui/images/search.png"),"Search by CAS RN") {
            public void actionPerformed(ActionEvent e) {
                lookup(CDKConstants.CASRN);
            }
        });
        p.add(new MoleculesTableAction("Append",UITools.createImageIcon("ambit2/ui/images/table_row_insert.png"),"Append a new record") {
            public void actionPerformed(ActionEvent e) {
                addAtomContainter();
            }
        });
        p.add(new MoleculesTableAction("Delete",
                UITools.createImageIcon("ambit2/ui/images/table_row_delete.png"),"Delete current record") {
            public void actionPerformed(ActionEvent e) {
                deleteAtomContainter();
            }
        });
        p.add(new MoleculesTableAction("Edit",UITools.createImageIcon("ambit2/ui/images/draw_16.png"),"Edit current structure") {
            public void actionPerformed(ActionEvent e) {
                editAtomContainter();
            }
        });

         
        return p;

    }
    protected void editProperties() {
        try {
            RandomAccessFileTableModel model = (RandomAccessFileTableModel) table.getModel();
            IAtomContainersList list = getReader();
            if (list == null) return;
            int record = table.getSelectedRow();
            if (record == -1)
            	record = model.getReader().getSelectedIndex();
            list.setSelectedIndex(record);
            MoleculeEditAction ma = new MoleculeEditAction(null,list, null);
            ma.actionPerformed(null);
            model.setTable(model.isTable());

                
        } catch (Exception x) {
            JOptionPane.showMessageDialog(this, x);
        }
    }

    protected void first() {
        try {
            goToRecord(0);
        } catch (Exception x) {
        }
    }

    protected void last() {
        try {
            goToRecord(table.getRowCount() - 1);
        } catch (Exception x) {
        }
    }

    protected void gotoRecord() {
        String s = JOptionPane.showInputDialog(this,"Record number","Go to record",JOptionPane.PLAIN_MESSAGE);
        if ((s != null) && (s.length() > 0)) try {
            int record = Integer.parseInt(s);
            goToRecord(record-1);
        } catch (Exception x) {
            
        }
    }
    
    protected void info() {
        
    }
    protected void nextRecord(int move) throws Exception {
        if (move == 0) return;
        IAtomContainersList reader = getReader();
        if (reader != null) {
        	int record = reader.getSelectedIndex() + move;
            reader.setSelectedIndex(record);
            table.setRowSelectionInterval(record, record);
            table.scrollRectToVisible(table.getCellRect(record, 0, true));
        }

    }    
    protected void goToRecord(int record) throws Exception {
        if (record < 0) return;
        IAtomContainersList reader = getReader();
        if (reader != null) {
            reader.setSelectedIndex(record);
            table.setRowSelectionInterval(record, record);
            table.scrollRectToVisible(table.getCellRect(record, 0, true));
        }

    }
    protected void addAtomContainter() {
        try {
            RandomAccessFileTableModel model = (RandomAccessFileTableModel) table.getModel();
            IAtomContainersList list = getReader();
            if (list == null) return;
            if (list instanceof IMoleculesIterator) {
            	

                ((IMoleculesIterator) list).addAtomContainer(MoleculeFactory.makeAlkane(1));
                
                //update
                model.setTable(model.isTable());
                goToRecord(list.getAtomContainerCount()-1);
            } else JOptionPane.showMessageDialog(this, "This dataset is read only!");
                
        } catch (Exception x) {
            JOptionPane.showMessageDialog(this, x);
        }
    }
    protected void deleteAtomContainter() {
        try {
            RandomAccessFileTableModel model = (RandomAccessFileTableModel) table.getModel();
            if (model.getRowCount() == 0) return;
            IAtomContainersList list = getReader();
            if (list == null) return;
            if (list instanceof IMoleculesIterator) {
                int record = table.getSelectedRow();
                if (record == -1)
                	record = model.getReader().getSelectedIndex();
                if (record >=0) {
                    ((IMoleculesIterator) list).removeAtomContainer(record);
                    //update
                    model.setTable(model.isTable());
                    goToRecord(record-1);
                }
            } else JOptionPane.showMessageDialog(this, "This dataset is read only!");
                
        } catch (Exception x) {
            JOptionPane.showMessageDialog(this, x);
        }
    }
    protected void editAtomContainter() {
        try {
            RandomAccessFileTableModel model = (RandomAccessFileTableModel) table.getModel();
            if (model.getRowCount() == 0) return;
            IAtomContainersList list = model.getReader();
            if (list == null) return;
            int record = table.getSelectedRow();
            
            if (record == -1)
            	record = model.getReader().getSelectedIndex();
            if (record ==-1) {
                JOptionPane.showMessageDialog(this, "No record selected! Click on a record to select it.");
                return;
            }
            list.setSelectedIndex(record);
            MoleculeEditAction ma = new MoleculeEditAction(null,list, null);
            ma.actionPerformed(null);
            model.setTable(model.isTable());
            goToRecord(record);
        } catch (Exception x) {
            JOptionPane.showMessageDialog(this, x);
        }
    }   
    protected void lookup(String lookupProperty) {
        String s = JOptionPane.showInputDialog(this,"Search by "+lookupProperty,lookupProperty,JOptionPane.PLAIN_MESSAGE);
        if ((s != null) && (s.length() > 0)) try {
            IAtomContainersList reader = getReader();
            if (reader == null) return;
            int record = reader.indexOf(lookupProperty, s);
            goToRecord(record);
        } catch (Exception x) {
            
        }           
    }
    public synchronized boolean isSpreadsheet() {
        return spreadsheet;
    }

    public synchronized void setSpreadsheet(boolean spreadsheet) {
        int row = table.getSelectedRow();

        this.spreadsheet = spreadsheet;
        ((RandomAccessFileTableModel) table.getModel()).setTable(spreadsheet);
        if (spreadsheet) {
            table.setRowHeight(18);
        } else {
            table.setRowHeight(cellSize.height + 24);
            table.setDefaultRenderer(Image.class, new ImageCellRenderer());
        }
        if (row > 0) {
            table.setRowSelectionInterval(row, row);
            table.scrollRectToVisible(table.getCellRect(row, 0, true));
        }
    }    
    protected JTable addWidgets(RandomAccessFileTableModel model) {

        JTable table = new JTable(model) {
            public void createDefaultColumnsFromModel() {
                TableModel m = getModel();
                if (m != null) {
                    // Remove any current columns
                    TableColumnModel cm = getColumnModel();
                    while (cm.getColumnCount() > 0) {
                        cm.removeColumn(cm.getColumn(0));
                    }
                    // Create new columns from the data model info
                    final int columnSize[] = { 32, 64, 100, 48, 48 };
                    for (int i = 0; i < m.getColumnCount(); i++) {
                        TableColumn newColumn = new TableColumn(i);
                        if (spreadsheet)
                            if (i < 2) {
                                newColumn.setPreferredWidth(columnSize[i]);
                                newColumn
                                        .setCellRenderer(new ColorTableCellRenderer());
                            } else {
                                newColumn.setPreferredWidth(64);
                                newColumn
                                        .setCellRenderer(new NumbersRenderer());
                            }
                        else {
                        	if (((RandomAccessFileTableModel)m).isMultiColumn())
                        		switch (i) {
                        		case 0: { newColumn.setPreferredWidth(columnSize[0]);
                        			newColumn.setCellRenderer(new ColorTableCellRenderer());
                        			break;}
                        		case 1: { newColumn.setPreferredWidth(cellSize.width); break;}
                        		default: { newColumn.setPreferredWidth(columnSize[2]); 
                        			newColumn.setCellRenderer(new ColorTableCellRenderer());
                        			break;}
                        		}
                        		       
                        	else {
                        		newColumn.setPreferredWidth(cellSize.width);
                                                 		
                        	}	
                        }
                        addColumn(newColumn);
                    }
                }

            };
        };
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);

        table.setDefaultRenderer(IAtomContainer.class,
                new MoleculeGridCellRenderer(cellSize));
        table.setDefaultRenderer(IMolecule.class, new MoleculeGridCellRenderer(
                cellSize));
        table.setDefaultRenderer(Image.class, new ImageCellRenderer());

        table.setPreferredScrollableViewportSize(new Dimension(
                cellSize.width * 3, (cellSize.height + 30) * 2));
        table.setSelectionBackground(AmbitColors.DarkClr);
        table.setSelectionForeground(AmbitColors.BrightClr);
        return table;
    }

    public synchronized JTable getTable() {
        return table;
    }

    public synchronized void setTable(JTable table) {
        this.table = table;
    }

    class HeightListener extends MouseInputAdapter {   
    	Point first , last;    
    public void mouseDragged(MouseEvent e) {      
    	if(first == null) {        
    		first = e.getPoint();        
    		table.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));      
    	}
    	last = e.getPoint();    
    }    
    public void mouseReleased(MouseEvent e) {      
    	if(first == null) return;      
    	int height = (int) (last.getY() - first.getY());      
    	if(height == 0) {        
    		table.setCursor(Cursor.getDefaultCursor());        
    		first = null;        return;      
    	}      
    	int row = table.rowAtPoint(first);      
    	int rowHeight = table.getRowHeight(row);      
    	table.setRowHeight(row, rowHeight + height);      
    	table.setCursor(Cursor.getDefaultCursor());      
    		first = null;    
    }           
    }

	public Dimension getCellSize() {
		return cellSize;
	}
	public void setCellSize(Dimension cellSize) {
		this.cellSize = cellSize;
		((RandomAccessFileTableModel) table.getModel()).setCellSize(cellSize);
	}

}
abstract class  MoleculesTableAction extends AbstractAction {
    public MoleculesTableAction(String caption, Icon icon, String hint) {
        super(caption,icon);
        putValue(AbstractAction.SHORT_DESCRIPTION, hint);
    }
}


