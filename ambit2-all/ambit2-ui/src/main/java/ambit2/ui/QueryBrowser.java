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

package ambit2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.poi.hssf.util.CellReference;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.ui.table.BrowserModeNavigator;
import ambit2.ui.table.FindNavigator;
import ambit2.ui.table.IBrowserMode;
import ambit2.ui.table.IFilteredColumns;
import ambit2.ui.table.IFindNavigator;
import ambit2.ui.table.IHeaderAction;
import ambit2.ui.table.IPageNavigator;
import ambit2.ui.table.IRecordNavigator;
import ambit2.ui.table.ISortableColumns;
import ambit2.ui.table.PageNavigator;
import ambit2.ui.table.RecordNavigator;
import ambit2.ui.table.IBrowserMode.BrowserMode;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;




public class QueryBrowser<T extends AbstractTableModel> extends JPanel implements PropertyChangeListener  {
	protected final JTable browser_table;
	protected IHeaderAction[] headerActions = getHeaderActions();
	//protected Dimension cellSize = new Dimension(200,200);	
	protected Hashtable<BrowserMode, ImageCellRenderer> imageRenderers = new Hashtable<BrowserMode, ImageCellRenderer>();
	protected BrowserModeCellRenderer cellRenderer = new BrowserModeCellRenderer(BrowserMode.Spreadsheet);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public QueryBrowser(T model) {
		this(model,new Dimension(150,150));
	}
	public QueryBrowser(T model,Dimension cellSize) {
		super(new BorderLayout());
		//this.cellSize = cellSize;
		
		if (model instanceof IBrowserMode) 
			((IBrowserMode)model).addPropertyChangeListener(this);
		browser_table = addWidgets(model);
		
		JScrollPane p = new JScrollPane(browser_table);
		p.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		browser_table.setPreferredScrollableViewportSize(new Dimension(600, 200));
		add(p, BorderLayout.CENTER);
		add(addControls(), BorderLayout.NORTH);
		setMinimumSize(new Dimension(200, 200));
	}
	
	
	protected JComponent addControls() {
		/**
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.putClientProperty("JToolBar.isRollover", Boolean.FALSE);
		*/
		
		FormLayout layout = new FormLayout(
	            "pref, pref",
			"pref,pref");
		PanelBuilder pb = new PanelBuilder(layout);
		JPanel toolBar = pb.getPanel();
		JComponent c = createPageControls(toolBar);
		CellConstraints cc = new CellConstraints();
		if (c != null) pb.add(c,cc.xy(1,1));
		c = createBrowseModeControls(toolBar);
		if (c != null) pb.add(c,cc.xy(2,1));
		c= createRecordControls(toolBar);
		if (c != null) pb.add(c,cc.xy(1,2));
		c = createFindControls(toolBar);
		if (c != null) pb.add(c,cc.xy(2,2));
		return toolBar;
	}
	
	protected JComponent createPageControls(JComponent toolbar) {
		if (browser_table.getModel() instanceof IPageNavigator) {
			return new PageNavigator(((IPageNavigator)browser_table.getModel()));
		} else
			return null;
	}
	protected JComponent createRecordControls(JComponent toolbar) {
		if (browser_table.getModel() instanceof IRecordNavigator) {
			return new RecordNavigator(((IRecordNavigator)browser_table.getModel()));

		} else
			return null;
	}	
	protected JComponent createBrowseModeControls(JComponent toolbar) {
		if (browser_table.getModel() instanceof IBrowserMode) {
			return new BrowserModeNavigator(((IBrowserMode)browser_table.getModel()));
		} else
			return null;
	}
	protected JComponent createFindControls(JComponent toolbar) {
		if (browser_table.getModel() instanceof IFindNavigator) {
			return new FindNavigator(((IFindNavigator)browser_table.getModel()));
		} else
			return null;

	}		
	protected ImageCellRenderer getImageRenderer(BrowserMode mode) {
		ImageCellRenderer imageRenderer = imageRenderers.get(mode);
		if (imageRenderer == null) {
			imageRenderer = new ImageCellRenderer();
			imageRenderers.put(mode,imageRenderer);
		}			
		return imageRenderer;
	}
	protected JTable addWidgets(T model) {
		
		JTable table = new JTable(model) {
			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				  Object value = getValueAt(row,column);
				  Object renderer = null;
				  if (value !=null) { 
					  renderer = defaultRenderersByColumnClass.get(value.getClass());
		            if (renderer != null) {
		                return (TableCellRenderer)renderer;
		            }
		            else {
		                return getDefaultRenderer(value.getClass().getSuperclass());
		            }
				  } else  
				  return super.getCellRenderer(row,column);

			}
			
			@Override
			public TableCellEditor getCellEditor(int row, int column) {

				  Object value = getValueAt(row,column);
				  Object editor = null;
				  if (value !=null) { 
					  editor = defaultEditorsByColumnClass.get(value.getClass());
				  
		            if (editor != null) {
		                return (TableCellEditor)editor;
		            }
		            else {
		                return getDefaultEditor(value.getClass().getSuperclass());
		            }
				  } else  
					  return super.getCellEditor(row,column);
			}
			
			public void createDefaultColumnsFromModel() {
				TableModel m = getModel();
				BrowserMode mode= BrowserMode.Spreadsheet;
				if (m instanceof IBrowserMode) 
					mode = ((IBrowserMode)m).getBrowserMode();
				else mode = BrowserMode.Spreadsheet;
				if (m != null) {
					// Remove any current columns
					
					TableColumnModel cm = getColumnModel();
					
					while (cm.getColumnCount() > 0) {
						cm.removeColumn(cm.getColumn(0));
					}
					for (int i = 0; i < m.getColumnCount(); i++) {
						EditableHeaderTableColumn newColumn = new EditableHeaderTableColumn();
						newColumn.setModelIndex(i);
						//newColumn.setCellRenderer(cellRenderer);

						addColumn(newColumn);
						
						((EditableHeaderTableColumn)newColumn).setHeaderEditor(
				        		new DefaultCellEditor(
				        				new JComboBox(new HeaderComboBoxModel(this,i,headerActions))));
				        				    
					}
					if (getTableHeader()!=null)
						getTableHeader().setColumnModel(cm);
					
					setCellSize(this);
			        					
				}
				
			};
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setTableHeader(new EditableHeader(table.getColumnModel(),false));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.setIntercellSpacing(new Dimension(3,3));
		if (model instanceof IBrowserMode) 
			((IBrowserMode)model).addPropertyChangeListener(getImageRenderer(BrowserMode.Spreadsheet));		
		table.setDefaultRenderer(Image.class, getImageRenderer(BrowserMode.Spreadsheet));
		table.setDefaultRenderer(IMolecule.class, getImageRenderer(BrowserMode.Spreadsheet));
		table.setDefaultRenderer(Molecule.class, getImageRenderer(BrowserMode.Spreadsheet));
		table.setDefaultRenderer(AtomContainer.class, getImageRenderer(BrowserMode.Spreadsheet));		
		table.setDefaultRenderer(IAtomContainer.class, getImageRenderer(BrowserMode.Spreadsheet));

		/*
		table.setPreferredScrollableViewportSize(new Dimension(
				cellSize.width * 3, (cellSize.height + 30) * 2));
		*/


		ListSelectionListener listener = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) return;
				int row = browser_table.getSelectedRow();
				int col = browser_table.getSelectedColumn();
		        // If cell selection is enabled, both row and column change events are fired
		        if (e.getSource() == browser_table.getSelectionModel()
		              && browser_table.getRowSelectionAllowed()) {
		        	row = browser_table.getSelectedRow();
		        } 
		        if (e.getSource() == browser_table.getColumnModel().getSelectionModel()
		               && browser_table.getColumnSelectionAllowed() ){
		        	col = browser_table.getSelectedColumn();
		        }
	        	if (browser_table.getSelectedRow()>0)      		
	        		setRecord(row,col);		        
		      }
		};
	    table.getSelectionModel().addListSelectionListener(listener);
	    table.getColumnModel().getSelectionModel()
	        .addListSelectionListener(listener);
	    table.setShowHorizontalLines(BrowserMode.Spreadsheet.showGridHorizontal());
	    table.setShowVerticalLines(BrowserMode.Spreadsheet.showGridVertical());
	    table.setGridColor(Color.gray);
	    setCellSize( table);
		return table;
	}	
	protected void setRecord(int row, int col) {
		if ((row < 0) || (col < 0)) return ;
		if (browser_table.getModel() instanceof IRecordNavigator) {
			
			int record = row;
			if (browser_table.getModel() instanceof IBrowserMode) {
				IBrowserMode bm = ((IBrowserMode)browser_table.getModel());
				record = bm.getBrowserMode().cellToRecord(row, col);
				if (record < 0) return ;
			}
			if (browser_table.getModel() instanceof IPageNavigator) {
				IPageNavigator pn = ((IPageNavigator)browser_table.getModel());
				if (record >= pn.getPageSize()) {
					record = (pn.getPageSize()-1) + pn.getPage()*pn.getPageSize();
				} else
					record = record + pn.getPage()*pn.getPageSize();
			}
			((IRecordNavigator) browser_table.getModel()).setRecord(record);
		}
	}
	/*
	public boolean isMatrix() {
		if ((browser_table != null) && (browser_table.getModel() != null) && (browser_table.getModel() instanceof IBrowserMode)) {
			return BrowserMode.Matrix.equals(((IBrowserMode) browser_table.getModel()).getBrowserMode());
		} else return false;
	}
	*/

	public void propertyChange(PropertyChangeEvent evt) {
		if (IPageNavigator.PROPERTY_PAGERECORD.equals(evt.getPropertyName())) {
			Integer record = (Integer) evt.getNewValue();
			try {
				if (browser_table.getModel() instanceof IBrowserMode) {
					
					IBrowserMode bm = ((IBrowserMode)browser_table.getModel());
					int[] cell = bm.getBrowserMode().recordToCell(record);
					if ((cell[0] >= 0) && (cell[0] < browser_table.getRowCount())) {
						browser_table.setRowSelectionInterval(cell[0],cell[0]);
//						browser_table.scrollRectToVisible(aRect)
					}
					if ((cell[1] >= 0) && (cell[1] < browser_table.getColumnCount()))
						browser_table.setColumnSelectionInterval(cell[1],cell[1]);
						
				}				
			} catch (Exception x) {
				x.printStackTrace();
			}
			//browser_table.scrollRectToVisible(browser_table.getCellRect(record, 0, true));
		} else if (IBrowserMode.PROPERTY_MODE.equals(evt.getPropertyName())) {
		
			
			BrowserMode mode = ((BrowserMode)evt.getNewValue());
			cellRenderer.mode = mode;
			browser_table.setDefaultRenderer(Image.class, getImageRenderer(mode));
			browser_table.setDefaultRenderer(IAtomContainer.class, getImageRenderer(mode));
			browser_table.setDefaultRenderer(IMolecule.class, getImageRenderer(mode));
			browser_table.setDefaultRenderer(Molecule.class, getImageRenderer(mode));
			browser_table.setDefaultRenderer(AtomContainer.class, getImageRenderer(mode));			
			browser_table.setRowSelectionAllowed(mode.isRowSelectionAllowed());
			browser_table.setColumnSelectionAllowed(mode.isColumnSelectionAllowed());	
			browser_table.setShowHorizontalLines(mode.showGridHorizontal());
			browser_table.setShowVerticalLines(mode.showGridVertical());
			
			if (browser_table.getModel() instanceof IBrowserMode)  {
				BrowserMode oldmode = ((BrowserMode)evt.getOldValue());
				((IBrowserMode)browser_table.getModel()).removePropertyChangeListener(getImageRenderer(oldmode));				
				((IBrowserMode)browser_table.getModel()).addPropertyChangeListener(getImageRenderer(mode));
				setCellSize(browser_table);
				
			}		
			else {
				browser_table.setRowHeight(
						mode.getCellSize(0,0).height
						);
				browser_table.setRowHeight(1,
						mode.getCellSize(1,0).height
						);
			}

		} else if (IBrowserMode.PROPERTY_ZOOM.equals(evt.getPropertyName())) {
		
			setCellSize(browser_table);
	
		} else if (IPageNavigator.PROPERTY_PAGESIZE.equals(evt.getPropertyName())) {	
			setCellSize(browser_table);
		} else if (IPageNavigator.PROPERTY_PAGE.equals(evt.getPropertyName())) {		
	
			setCellSize(browser_table);
		}
		
		
	}
	protected void setCellSize(JTable browser_table) {
		BrowserMode mode = ((IBrowserMode) browser_table.getModel()).getBrowserMode();
		for (int row = 0; row < browser_table.getRowCount();row++) 
			for (int col=0; col < browser_table.getColumnCount(); col++) {
				TableColumn c = browser_table.getColumnModel().getColumn(col);
				Dimension d = mode.getCellSize(row, col);
				c.setPreferredWidth((int)d.getWidth());
				browser_table.setRowHeight(row,(int)d.getHeight());

			}			

	}
	public static IHeaderAction[] getHeaderActions() {
        IHeaderAction[] actions = new IHeaderAction[4];
        actions[0] = new IHeaderAction() {
            @Override
            public String toString() {
                return "Sort ascending";
            }
            public void action(JTable table, int column, Object value) {
            	if (table.getModel() instanceof ISortableColumns)
            		((ISortableColumns)table.getModel()).sort(column, true);
            	else
            		JOptionPane.showMessageDialog(table, "Unsupported","Error",JOptionPane.OK_OPTION);
                
            }

        };
        actions[1] = new IHeaderAction() {
            @Override
            public String toString() {
                return "Sort descending";
            }
            public void action(JTable table, int column, Object value) {
            	if (table.getModel() instanceof ISortableColumns)
            		((ISortableColumns)table.getModel()).sort(column, false);
            	else
            		JOptionPane.showMessageDialog(table, "Unsupported");
                
            }

        };      
        actions[2] = new IHeaderAction() {
            @Override
            public String toString() {
                return "(All)";
            }
            public void action(JTable table, int column, Object value) {
            	if (table.getModel() instanceof IFilteredColumns)
            		((IFilteredColumns)table.getModel()).dropFilter(column);
            	else
            		JOptionPane.showMessageDialog(table, "Unsupported");
                
            }

        };            
        actions[3] = new IHeaderAction() {
            @Override
            public String toString() {
                return "(Custom filter)";
            }
            public void action(JTable table, int column, Object value) {
            	if (table.getModel() instanceof IFilteredColumns)
            		((IFilteredColumns)table.getModel()).setFilter(column,value);
            	else
            		JOptionPane.showMessageDialog(table, "Unsupported");
                
                
            }

        };            
        return actions;
	}
}

abstract class MultiCells<Key,T> {
	  protected Hashtable<Key,T> editors;
	  protected T editor, defaultEditor;

	  /**
	   * 
	   */
	  public MultiCells() {
	    editors = new Hashtable<Key,T>();
	    defaultEditor = createDefaultCell();
	  }
	  
	  /**
	   * @param row    table row
	   * @param editor table cell editor
	   */
	  public void add(Key key, T editor) {
	    editors.put(key,editor);
	  }	  
	  protected abstract T createDefaultCell();
		  

}	  
/*
class RowRenderer extends MultiCells<Integer,TableCellRenderer> implements TableCellRenderer {


	  public RowRenderer() {
		  super();
	  }

	  @Override
		protected TableCellRenderer createDefaultCell() {
			  return new DefaultTableCellRenderer();
		}

	  
	  public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		  
		    editor = editors.get(new Integer(row));
		    if (editor == null)
		    	if (value != null) {
		    		editor = table.getDefaultRenderer(value.getClass());
		    	}	

		    if (editor == null) {
		    	  editor = defaultEditor;
		    }
		    return editor.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
	  
	  /*
	  public Component getTableCellEditorComponent(JTable table,
	      Object value, boolean isSelected, int row, int column) {
	    editor = (TableCellEditor)editors.get(new Integer(row));
	    if (editor == null) {
	      editor = defaultEditor;
	    }
	    return editor.getTableCellEditorComponent(table,
	             value, isSelected, row, column);
	  }

	  public Object getCellEditorValue() {
	    return editor.getCellEditorValue();
	  }
	  public boolean stopCellEditing() {
	    return editor.stopCellEditing();
	  }
	  public void cancelCellEditing() {
	    editor.cancelCellEditing();
	  }
	  public boolean isCellEditable(EventObject anEvent) {
	    return editor.isCellEditable(anEvent);
	  }
	  public void addCellEditorListener(CellEditorListener l) {
	    editor.addCellEditorListener(l);
	  }
	  public void removeCellEditorListener(CellEditorListener l) {
	    editor.removeCellEditorListener(l);
	  }
	  public boolean shouldSelectCell(EventObject anEvent) {
	    return editor.shouldSelectCell(anEvent);
	  }
	  
	}
	*/
