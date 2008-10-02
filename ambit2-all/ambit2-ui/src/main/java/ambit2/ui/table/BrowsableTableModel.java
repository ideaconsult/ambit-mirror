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

import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import ambit2.ui.Utils;

public class BrowsableTableModel extends AbstractTableModel implements IPageNavigator, IRecordNavigator , IBrowserMode, ISortableColumns, IFilteredColumns, IFindNavigator {
	protected PropertyChangeSupport  ps;
	protected AbstractTableModel dataModel;
	protected int pageSize = 10;
	protected int page = 0;
	protected int record = 0;	
	protected int idColumn = 1;
	protected double zoomFactor =1;
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
	
	public BrowsableTableModel(AbstractTableModel dataModel) {
		super();
		ps = new PropertyChangeSupport(this);
		setDataModel(dataModel);
		try {
			selectedIcon = Utils.createImageIcon("images/bullet_blue.png");
		} catch (Exception x) {
			selectedIcon = null;
		}
		try {
			currentIcon = Utils.createImageIcon("images/bullet_red.png");
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
			return rowCount+2;
		}	
		default:	
			return getDataModel().getColumnCount()+2;
		}
	}

	public int getRowCount() {
		int rowCount;
    	if (page < getMaxPages()) rowCount = pageSize;
    	else rowCount = getMaxRecords()-getPage()*getPageSize();

		switch (browserMode) {
		case Matrix:
			return (int)Math.ceil((double)rowCount / getColumnCount());
		case Columns:
			return getDataModel().getColumnCount()+1;			
		default:	
			return rowCount;
		}
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		switch (browserMode) {
		case Matrix:
			return getDataModel().getColumnClass(browserMode.getContentColumn());
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
			return getDataModel().getColumnName(browserMode.getContentColumn());
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
	protected int getDataModelColumn(int column) {
		switch (browserMode) {
		case Matrix:
			return browserMode.getContentColumn();
		case Columns:
			return column-1;
		default: 
			return column-2;
		}
	}

	public Object getValueAt(int row, int col) {
		int record = browserMode.cellToRecord(row, col);
		int realRow = record+getPage()*getPageSize();
		
		switch (browserMode) {
		case Matrix: {
			if (record >= getPageSize()) return "";
			else
				return getDataModel().getValueAt(realRow, browserMode.getIDColumn());
		}
		case Columns: {
			switch (col) {
			case 0: return getDataModel().getColumnName(row);
			default: {
				record = browserMode.cellToRecord(row, col-1);
				realRow = record+getPage()*getPageSize();
				return getDataModel().getValueAt(realRow,row);			
			}
			}

		}	
		default: 
	    	switch (col) {
	    	case 0:  {
	    		ImageIcon icon = null;
	    		if (isFound(realRow))
	    			
	    			if (getRecord() == (realRow)) {
	    				icon = selectedAndCurrentIcon;
	    			} else {
	    				icon = selectedIcon;
	    			}
	    		else if (getRecord() == (realRow))
	    			icon = currentIcon;
	    		return icon;
	    	}	
	    	case 1:
	    		return new Integer(realRow);
	    	default:
	    		return getDataModel().getValueAt(realRow, col-2);
	    	}
		}

	}
	
	public AbstractTableModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(AbstractTableModel dataModel) {
		this.dataModel = dataModel;
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
		fireTableDataChanged();
		
		if ((getRecord() < getPage()*getPageSize()) || (getRecord() >= (getPage()+1)*getPageSize()))
			setRecord(getPage()*getPageSize());
		System.out.println("setPage "+page);
	}

	public void setPageSize(int size) {
		int oldsize = this.pageSize;

		int oldMax = getMaxPages();
		this.pageSize = size;
		ps.firePropertyChange(IPageNavigator.PROPERTY_PAGESIZE, oldsize,page);
		ps.firePropertyChange(IPageNavigator.PROPERTY_MAXPAGES, oldMax,getMaxPages());
		fireTableDataChanged();
	}

	public int getMaxRecords() {
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
		System.out.println("setRecord "+record);
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

	public void zoom(double arg0) {
		double oldZoom = zoomFactor;
		zoomFactor = arg0;
		browserMode.setCellSize(new Dimension(
				(int) Math.round(zoomFactor),
				(int) Math.round(zoomFactor)
				),
				0,0);
		ps.firePropertyChange(IBrowserMode.PROPERTY_ZOOM,oldZoom,zoomFactor);
	}

	public void setFilter(int column, Object value)
			throws UnsupportedOperationException {
		if (dataModel instanceof IFilteredColumns)
			((IFilteredColumns) getDataModel()).setFilter(
					getDataModelColumn(column)
					, value);
		else
			throw new UnsupportedOperationException();
		
	}
	public void dropFilter(int column) throws UnsupportedOperationException {
		if (dataModel instanceof IFilteredColumns)
			((IFilteredColumns) getDataModel()).dropFilter(
					getDataModelColumn(column)
					);
		else
			throw new UnsupportedOperationException();
		
	}
	public void sort(int column, boolean ascending)
			throws UnsupportedOperationException {
		if (dataModel instanceof ISortableColumns)
			((ISortableColumns) getDataModel()).sort(
					getDataModelColumn(column),ascending
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
}
