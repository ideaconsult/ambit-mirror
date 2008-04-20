/* QMRFAttachmentsBrowser.java
 * Author: Nina Jeliazkova
 * Date: Feb 24, 2007 
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

package ambit2.data.qmrf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import ambit2.exceptions.AmbitException;
import ambit2.io.AmbitFileFilter;
import ambit2.io.MyIOUtilities;
import ambit2.ui.ColorTableCellRenderer;
import ambit2.ui.UITools;
import ambit2.ui.editors.IAmbitEditor;

public class QMRFAttachmentsBrowser extends JPanel implements IAmbitEditor<QMRFAttachmentsList> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9096127438496360904L;
	protected boolean attachment_editable = true;
    protected final JTable table;
    public transient static final String[] structures_extensions = {".sdf",".csv",".smi",".txt",".mol",".cml"};
	public transient static final String[] structures_extensions_Description = 
		{"SDF files with chemical compounds (*.sdf)",
		"CSV files (Comma delimited) *.csv)",
		"SMILES files (*.smi)",
		"Text files (Tab delimited) (*.txt)",
		"MDL MOL files (*.mol)",
		"Chemical Markup Language files (*.cml)"
		};	
    
    
    protected QMRFAttachmentsBrowser(QMRFAttachmentsList attachments, boolean editable) {
        super();
        setEnabled(editable);
        setAttachment_editable(editable);
        setLayout(new BorderLayout());
        JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
        toolbar.setFloatable(true);
        Action a = new AbstractAction("Add",UITools.createImageIcon("ambit2/ui/images/attachment.png")) {
            public void actionPerformed(ActionEvent arg0) {
            	try {
            		addAttachment();
            	} catch (Exception x) {
            		JOptionPane.showMessageDialog(null,x);		
            	}
                
            }
        };
        a.setEnabled(editable);
        JButton button = new JButton(a);
        if (editable)
        	button.setToolTipText("Click here to select a file and add it to the list");
        else
        	button.setToolTipText("Attachment adding not allowed.");
        toolbar.add(button);

        AbstractTableModel model = new QMRFAttachmentsTableModel(attachments);
        table = new JTable(model,createColumnModel(model));
        table.setSurrendersFocusOnKeystroke(true);
        table.setShowGrid(false);
        table.getColumnModel().setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setBackground(Color.white);
        //table.setTableHeader(null);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col==4) {
                	if (isAttachment_editable())
                		((QMRFAttachmentsTableModel)table.getModel()).delete(row);
                	else 
                		JOptionPane.showMessageDialog(null,"Removing attachments not allowed!");	
                } else if (col==5) {
                    QMRFAttachment q = ((QMRFAttachmentsTableModel)table.getModel()).getAttachments().get(row);
                    boolean text = false;
                    String ext = '.' + q.getFiletype().toLowerCase()  ;
                    for (int i=0; i < structures_extensions.length;i++)
                    	if (structures_extensions[i].equals(ext)) {
                    		text = true;
                    		break;
                    	}
                	try {
                		File file = MyIOUtilities.selectFile(null, "save", "", new String[] {ext},new String[] {ext}, false);
                		if (file != null) {
                			q.writeContent(file);
	                    	JOptionPane.showMessageDialog(null,"Saved to "+file);
                		}
	
	                    //}
                	} catch (Exception x) {
                		JOptionPane.showMessageDialog(null,x);	
                	}
                	                    	
                }    

            }
            
        });
        table.setOpaque(true);
        table.setMinimumSize(new Dimension(100,64));
        
        JScrollPane sp = new JScrollPane(table);
        sp.setMinimumSize(new Dimension(100,64));
        table.getParent().setBackground(Color.white);
        sp.setBackground(Color.white);
        add(sp,BorderLayout.CENTER);
        add(toolbar,BorderLayout.EAST);
        setBackground(Color.white);
        
    }
    protected TableColumnModel createColumnModel(TableModel model) {
        DefaultTableColumnModel m = new DefaultTableColumnModel();
        ColorTableCellRenderer r = new ColorTableCellRenderer();
        for (int i=0; i < model.getColumnCount();i++) {
            TableColumn c = new TableColumn(i);
            c.setHeaderValue(model.getColumnName(i));
            if ((i % 2) == 1) c.setMaxWidth(100);
            if (i!=3)
            	c.setCellRenderer(r);
            m.addColumn(c);
        }
        return m;    
    }
    public JComponent getJComponent() {
        return this;
    }

    public void setEditable(boolean editable) {
        // TODO Auto-generated method stub

    }

    public boolean view(Component parent, boolean editable, String title)
            throws AmbitException {
        setEditable(editable);
        return JOptionPane.showConfirmDialog(parent,this) == JOptionPane.OK_OPTION;
    }
    protected void addAttachment() throws Exception {
    	String[] ext = null;
    	String[] extdescr = null;
    	if (((QMRFAttachmentsTableModel)table.getModel()).attachments.name.equals("attachment_documents")) {
    		ext = new String[] {"*.*"};
    		extdescr = new String[] {"All files"};
    	} else {
    		ext = structures_extensions;
    		extdescr = structures_extensions_Description;
    	}
    	
        File file = selectFile(this,"",
                "",
                ext,extdescr,true,null);
        if (file !=null)
        ((QMRFAttachmentsTableModel)table.getModel()).add(file);
       
    }
	public boolean isEditable() {
		return true;
	}
	
	public static File selectFile(Component frame, String caption,
	        String currentDirectory,
	        String[] ext,String[] extDescription, boolean open, JComponent accessoryPanel) {
		JFileChooser fc = new JFileChooser();
		
		
		for (int i=0; i < ext.length; i++)
			fc.addChoosableFileFilter(new AmbitFileFilter(ext[i],extDescription[i]));
		fc.setFileFilter(fc.getChoosableFileFilters()[1]);
		fc.setCurrentDirectory(new File(currentDirectory));
		
		if (accessoryPanel != null) {
			fc.setAccessory(accessoryPanel);
			if (accessoryPanel instanceof PropertyChangeListener)
				fc.addPropertyChangeListener((PropertyChangeListener)accessoryPanel);
		}
        int returnVal;
        if (open) returnVal = fc.showOpenDialog(frame);
		else  returnVal =  fc.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            FileFilter ff = fc.getFileFilter();
            if (ff instanceof AmbitFileFilter)
            	file = ((AmbitFileFilter)ff).changeExtension(file);
            
            fc = null;
            
            return file;
        } 
        fc = null;
        return null;
	}
	public boolean isAttachment_editable() {
		return attachment_editable;
	}
	public void setAttachment_editable(boolean attachment_editable) {
		this.attachment_editable = attachment_editable;
	}
	public void setObject(QMRFAttachmentsList object) {
		// TODO Auto-generated method stub
		
	}
	public QMRFAttachmentsList getObject() {
		// TODO Auto-generated method stub
		return null;
	}
}

class QMRFAttachmentsTableModel extends AbstractTableModel {
    protected  QMRFAttachmentsList attachments;
    public QMRFAttachmentsTableModel(QMRFAttachmentsList attachments) {
        super();
        this.attachments = attachments;
    }
    public int getColumnCount() {
        return 6;
    }

    public int getRowCount() {
        return attachments.size();
    }

    public Object getValueAt(int row, int col) {
        switch (col) {
        case 0:
            return attachments.get(row).getUrl();
        case 1:
            return attachments.get(row).getFiletype();
        case 2:
            return attachments.get(row).getDescription();
        case 3:
            return attachments.get(row).getEmbedded();            
        case 4:
            return "<html><u><b>Remove</b></u></html>";
        case 5:
            return "<html><u><b>Download</b></u></html>";             
        default:
            return "";
        }
    }
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
        case 2:
            attachments.get(rowIndex).setDescription(aValue.toString());
        case 3:
            attachments.get(rowIndex).setEmbedded((Boolean)aValue);            
        }

    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 2); 
        //|| (columnIndex == 3);
    }
    public synchronized QMRFAttachmentsList getAttachments() {
        return attachments;
    }
    public synchronized void setAttachments(QMRFAttachmentsList attachments) {
        this.attachments = attachments;
    }
    @Override
    public String getColumnName(int arg0) {
        switch (arg0) {
        case 0:
            return "File";
        case 1:
            return "File format";
        case 2:
            return "Description";
        case 3:
            return "Embedded";            
        case 4:
            return "Action";
        case 5:
            return "View";            
        default:
            return "";
        }

    }
    @Override
    public Class<?> getColumnClass(int columnIndex) {
    	 switch (columnIndex) {
         case 3:
             return Boolean.class;            
         default:
             return super.getColumnClass(columnIndex);
         }
    }
    public QMRFAttachment delete(int row) {
    	
        QMRFAttachment a = attachments.remove(row);
        fireTableStructureChanged();
        return a;
    }
    public void add(File file) throws Exception {
        attachments.add(file);
        fireTableStructureChanged();
    }
}
