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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.ImageIcon;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import ambit2.base.data.IFilteredColumns;
import ambit2.base.data.ISelectableRecords;
import ambit2.base.data.ISortableColumns;
import ambit2.ui.Utils;

public class BrowsableTableModel extends AbstractTableModel implements IPageNavigator, IRecordNavigator , IBrowserMode, ISortableColumns, IFilteredColumns, IFindNavigator, ISelectableRecords, TableModelListener {
	protected PropertyChangeSupport  ps;
	protected TableModel dataModel;
	protected int pageSize = 12;
	protected int page = 0;
	protected int record = 0;	
	protected int idColumn = 1;
	protected double[] zoomFactor = new double[] {1.0,1.0};
	protected ImageIcon selectedIcon ;
	protected ImageIcon currentIcon ;
	protected ImageIcon selectedAndCurrentIcon ;
	
	protected BrowserMode browserMode = BrowserMode.Spreadsheet;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1948964692310621358L;

	public BrowsableTableModel() {
		this(null);
	}
	
	public BrowsableTableModel(TableModel dataModel) {
		super();
		ps = new PropertyChangeSupport(this);
		setDataModel(dataModel);
		try {
			selectedIcon = Utils.createImageIcon("images/bullet_blue.png");
		} catch (Exception x) {
			selectedIcon = null;
		}
		try {
			currentIcon = Utils.createImageIcon("images/resultset_next.png");
		} catch (Exception x) {
			currentIcon = null;
		}
		try {
			selectedAndCurrentIcon = Utils.createImageIcon("images/bullet_purple.png");
		} catch (Exception x) {
			selectedAndCurrentIcon = null;
		}				
	}
	public int getColumnCount() {
		switch (browserMode) {
		case Matrix:
			return browserMode.getColumns();
		case Columns: {
			int rowCount;
	    	if (page < getMaxPages()) rowCount = pageSize;
	    	else rowCount = getMaxRecords()-getPage()*getPageSize();			
			return rowCount+1;
		}	
		default:	
			if (getDataModel()!= null)
				return getDataModel().getColumnCount()+2;
			else return 0;
		}
	}

	public int getRowCount() {
		int rowCount;
    	if (page < getMaxPages()) rowCount = pageSize;
    	else rowCount = getMaxRecords()-getPage()*getPageSize();

		switch (browserMode) {
		case Matrix:
			if (getColumnCount()==0) return 1;
			else {
				double rows = (double)(rowCount*2.0) / getColumnCount();
				return 2*(int)Math.ceil(rows);
			}
		case Columns:
			return getDataModel().getColumnCount();			
		default:	
			return rowCount;
		}
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		switch (browserMode) {
		case Matrix:
			return super.getColumnClass(column);	
		case Columns:
			return super.getColumnClass(column);			
		default:	
			switch (column) {
			case 0: return ImageIcon.class;
			case 1: return Integer.class;
			default: return getDataModel().getColumnClass(column-2);
			}
		}
	}
	@Override
	public String getColumnName(int column) {
		switch (browserMode) {
		case Matrix:
			if ((column %2)==1)
				return getDataModel().getColumnName(browserMode.getContentColumn());
			else
				return "#";
		case Columns: {
	    	switch (column) {
	    	case 0: 
	   			return "Properties";
			default:
			/*
			int record = browserMode.cellToRecord(0,column);
			int realRow = record+getPage()*getPageSize();
			*/
				return getDataModel().getColumnName(browserMode.getContentColumn());
	    	}
		}	
		default:	
	    	switch (column) {
	    	case 0: 
	   			return ">";
	    	case 1:
	    		return "#";
	    	default:
	    		return getDataModel().getColumnName(column-2);
	    	}
		}		
		
	}

	protected int getDataModelColumn(int row, int column) {
		switch (browserMode) {
		case Matrix:
			if ((row %2) == 0)
				if ((column % 2)==1)
					return browserMode.getContentColumn();
				else
					return 0;
			else
				if ((column % 2)==0)
					return -1;
				else
					return browserMode.getIDColumn();				
		case Columns:
			return row;
		default: 
			return column-2;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		int record = browserMode.cellToRecord(rowIndex, columnIndex);
		int realRow = record+getPage()*getPageSize();
		return getDataModel().isCellEditable(realRow, getDataModelColumn(rowIndex,columnIndex));
	}	
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		int record = browserMode.cellToRecord(row, col);
		int realRow = record+getPage()*getPageSize();
		getDataModel().setValueAt(value, realRow, getDataModelColumn(row,col));
	}
	public Object getValueAt(int row, int col) {
		int record = browserMode.cellToRecord(row, col);
		int realRow = record+getPage()*getPageSize();
		
		switch (browserMode) {
		case Matrix: {
			if (record >= getPageSize()) return "";
			else { 
				int realCol = getDataModelColumn(row,col);
				if (realCol != -1)
					return getDataModel().getValueAt(realRow, realCol);
				else return "";
			}
		}
		case Columns: {
			switch (col) {
			case 0: return getDataModel().getColumnName(row);
			default: {
				return getDataModel().getValueAt(realRow,row);			
			}
			}

		}	
		default: 
	    	switch (col) {
	    	case 0:  {
	    		return getRecordMarker(realRow);
	    	}	
	    	case 1:
	    		return new Integer(realRow+1);
	    	default:
	    		try {
	    		return getDataModel().getValueAt(realRow, col-2);
	    		} catch (Exception x) {
	    			return x.getMessage();
	    		}
	    	}
		}

	}
	
	protected ImageIcon getRecordMarker(int realRow) {
		ImageIcon icon = null;
		boolean found = false;
		try {
			found = isFound(realRow);
		} catch (Exception x) {
			found = false;
		}
		if (found)
			if (getRecord() == (realRow)) {
				icon = selectedAndCurrentIcon;
			} else {
				icon = selectedIcon;
			}
		else if (getRecord() == (realRow))
			icon = currentIcon;
		return icon;		
	}
	public TableModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(TableModel dataModel) {
	    if (this.dataModel != null)
	        this.dataModel.removeTableModelListener(this);
		this.dataModel = dataModel;
		if (this.dataModel != null)
			this.dataModel.addTableModelListener(this);
		fireTableStructureChanged();
		setRecord(0);
	}

	public void addPropertyChangeListener(PropertyChangeListener x) {
		ps.addPropertyChangeListener(x);
		
	}

	public int getMaxPages() {
		return getMaxRecords() / getPageSize();
	}

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void nextPage() {
		setPage(page+1);
		
	}

	public void previousPage() {
		setPage(page-1);
		
	}

	public void removePropertyChangeListener(PropertyChangeListener x) {
		ps.removePropertyChangeListener(x);
		
	}

	public void setPage(int page) {
		if (page == this.page) return;
		if (page < 0) return;
		if (page > getMaxPages()) return;
		int oldpage = this.page;
		this.page = page;
		
		ps.firePropertyChange(IPageNavigator.PROPERTY_PAGE, oldpage,page);
		fireTableStructureChanged();
		
		if ((getRecord() < getPage()*getPageSize()) || (getRecord() >= (getPage()+1)*getPageSize()))
			setRecord(getPage()*getPageSize());
	}

	public void setPageSize(int size) {
		int oldsize = this.pageSize;

		int oldMax = getMaxPages();
		this.pageSize = size;
		ps.firePropertyChange(IPageNavigator.PROPERTY_PAGESIZE, oldsize,page);
		ps.firePropertyChange(IPageNavigator.PROPERTY_MAXPAGES, oldMax,getMaxPages());
		fireTableStructureChanged();
	}

	public int getMaxRecords() {
		if (dataModel == null) return 0;
		else
			return dataModel.getRowCount();
	}

	public int getRecord() {
		return record;
	}

	public void next() {
		setRecord(getRecord()+1);
	}

	public void prev() {
		setRecord(getRecord()-1);
	}

	public void setRecord(int record) {
		if (record == this.record) return;
		if (record < 0) return;
		if (record > getMaxRecords()) return;
		int oldrecord = this.record;
		int oldPage = getPage();
		this.record = record;
		ps.firePropertyChange(IRecordNavigator.PROPERTY_RECORD,oldrecord,record);
		setPage(record/pageSize);
		ps.firePropertyChange(IPageNavigator.PROPERTY_PAGERECORD,-1,record-getPage()*getPageSize());		
		if (BrowserMode.Spreadsheet.equals(browserMode)) {
			fireTableCellUpdated(oldrecord-oldPage*getPageSize(), 0);
			fireTableCellUpdated(record-getPage()*getPageSize(), 0);
		}		
	}

	public BrowserMode getBrowserMode() {
		return browserMode;
	}

	public int getIDColumn() {
		return idColumn;
	}

	public void setBrowserMode(BrowserMode mode) {
		BrowserMode old = this.browserMode;
		this.browserMode = mode;
		ps.firePropertyChange(IBrowserMode.PROPERTY_MODE,old,browserMode);
		fireTableStructureChanged();
	}

	public void setIDColumn(int id) {
		int oldid = this.idColumn;
		this.idColumn = id;
		ps.firePropertyChange(IBrowserMode.PROPERTY_MODE,oldid,idColumn);
		
	}

	public void zoom(double x,double y) {
		ps.firePropertyChange(IBrowserMode.PROPERTY_ZOOM,browserMode.getCellSize(0,0),browserMode.zoom(x,y));
	}

	public void setFilter(int column, Object value)
			throws UnsupportedOperationException {
		if (dataModel instanceof IFilteredColumns)
			((IFilteredColumns) getDataModel()).setFilter(
					getDataModelColumn(0,column)
					, value);
		else
			throw new UnsupportedOperationException();
		
	}
	public void dropFilter(int column) throws UnsupportedOperationException {
		if (dataModel instanceof IFilteredColumns)
			((IFilteredColumns) getDataModel()).dropFilter(
					getDataModelColumn(0,column)
					);
		else
			throw new UnsupportedOperationException();
		
	}
	public void sort(int column, boolean ascending)
			throws UnsupportedOperationException {
		if (dataModel instanceof ISortableColumns)
			((ISortableColumns) getDataModel()).sort(
					getDataModelColumn(0,column),ascending
					);
		else
			throw new UnsupportedOperationException();
	}

	public boolean isCompleted() {
		if (dataModel instanceof IFindNavigator)
			return ((IFindNavigator)dataModel).isCompleted();
		else 
			return false;
	}
	public int findNext()throws UnsupportedOperationException {
		if (dataModel instanceof IFindNavigator) {
			int record = ((IFindNavigator)dataModel).findNext();
			setRecord(record);
			return record;
		}	
		else throw new UnsupportedOperationException();
		
	}

	
	public int findPrevious() throws UnsupportedOperationException {
		if (dataModel instanceof IFindNavigator) {
			int record = ((IFindNavigator)dataModel).findPrevious();
			setRecord(record);
			return record;			
		} else throw new UnsupportedOperationException();
	}
	public Object getValue() {
		//return findValue;
		if (dataModel instanceof IFindNavigator)
			return ((IFindNavigator)dataModel).getValue();
		else return "";
	}

	public void setValue(Object value) {
		//findValue = value.toString();
		if (dataModel instanceof IFindNavigator)
			((IFindNavigator)dataModel).setValue(value);
		
	}

	public void setCompleted(boolean value) {
		if (dataModel instanceof IFindNavigator)
			((IFindNavigator)dataModel).setCompleted(value);
		
	}
	public int find() throws UnsupportedOperationException {
		if (dataModel instanceof IFindNavigator) 
			return ((IFindNavigator)dataModel).find();
		else throw new UnsupportedOperationException();
	}
	public boolean isFound(int record) {
		if (dataModel instanceof IFindNavigator) 
			return ((IFindNavigator)dataModel).isFound(record);
		else throw new UnsupportedOperationException();		
	}
	
	public void tableChanged(TableModelEvent e) {
	    if (TableModelEvent.HEADER_ROW == e.getFirstRow()) {
	        ps.firePropertyChange(IPageNavigator.PROPERTY_MAXPAGES, -1,getMaxPages());
	        ps.firePropertyChange(IRecordNavigator.PROPERTY_MAXRECORDS, -1,getMaxRecords());
	    }
	        
	    fireTableChanged(e);
	    
	}
	public void setSelection(SELECTION_MODE mode) {
		if ((dataModel!=null) && (dataModel instanceof ISelectableRecords))
			((ISelectableRecords) dataModel).setSelection(mode);
	}
	public SELECTION_MODE getSelection() {
		if ((dataModel!=null) && (dataModel instanceof ISelectableRecords))
			return ((ISelectableRecords) dataModel).getSelection();
		else return SELECTION_MODE.NO_CHANGE;

	}
}
