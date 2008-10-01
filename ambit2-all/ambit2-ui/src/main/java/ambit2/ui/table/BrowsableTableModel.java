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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.table.AbstractTableModel;

public class BrowsableTableModel extends AbstractTableModel implements IPageNavigator, IRecordNavigator , IBrowserMode {
	protected PropertyChangeSupport  ps;
	protected AbstractTableModel dataModel;
	protected int pageSize = 10;
	protected int page = 0;
	protected int record = 0;	
	protected int idColumn = 1;
	protected double zoomFactor =1;
	
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
	}
	public int getColumnCount() {
		if (BrowserMode.Matrix.equals(browserMode))
			return browserMode.getColumns();
		else	
			return getDataModel().getColumnCount()+2;
	}

	public int getRowCount() {
		int rowCount;
    	if (page < getMaxPages()) rowCount = pageSize;
    	else rowCount = getMaxRecords()-getPage()*getPageSize();
    	
    	if (BrowserMode.Matrix.equals(browserMode)) {
    		return (int)Math.ceil((double)rowCount / getColumnCount());
    	}
    	else	
    		return rowCount;
	}

	@Override
	public String getColumnName(int column) {
		if (BrowserMode.Matrix.equals(browserMode)) {
			return getDataModel().getColumnName(browserMode.getContentColumn());
		} else
	    	switch (column) {
	    	case 0: 
	   			return ">";
	    	case 1:
	    		return "#";
	    	default:
	    		return getDataModel().getColumnName(column-2);
	    	}
		
	}
	public Object getValueAt(int row, int col) {
		
		if (BrowserMode.Matrix.equals(browserMode)) {
			int thisRow = row*getColumnCount()+col;
			int realRow = thisRow+getPage()*getPageSize();
			//return new Integer(realRow);
			return getDataModel().getValueAt(realRow, browserMode.getIDColumn());
		} else {
			int realRow = row+getPage()*getPageSize();
	    	switch (col) {
	    	case 0: 
	    		if (getRecord() == (realRow))
	    			return ">";
	    		else 
	    			return "";
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
				));
		ps.firePropertyChange(IBrowserMode.PROPERTY_ZOOM,oldZoom,zoomFactor);
	}

}
