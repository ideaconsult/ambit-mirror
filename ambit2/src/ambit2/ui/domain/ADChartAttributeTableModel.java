/*
 * Created on 2005-7-12
 *
 */
package ambit2.ui.domain;

import javax.swing.table.AbstractTableModel;

import ambit2.data.AmbitObject;
import ambit2.domain.QSARDataset;

/**
 * TODO add description
 * @author Vedina
 * <b>Modified</b> 2005-7-12
 */
public class ADChartAttributeTableModel extends AbstractTableModel {
    //data
	protected QSARDataset ds = null;
	protected IADPlotsType plotsType = null;

	protected String columnNames[] = {"X","Name","Y"};
	protected String longValues[] = {"X","1234567890","Y"};
	/**
	 * 
	 */
	public ADChartAttributeTableModel(QSARDataset ds,IADPlotsType plotsType) {
		super();
		this.ds = ds;
		this.plotsType = plotsType;
		int n = ds.getNdescriptors();
		if (n > 0) plotsType.setXindex(0);
		if (n > 1) plotsType.setYindex(1); else plotsType.setYindex(0);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
	    if (plotsType.isXDescriptor()) return ds.getNdescriptors();
	    else return 1;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
	    if (plotsType.isXDescriptor()) return columnNames.length;
	    else return 0;	    
		
	}
	public void setPlotType(String name) {
		plotsType.setName(name);
		fireTableDataChanged();
		ds.fireAmbitObjectEvent((AmbitObject)plotsType);		
	}
    /* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0: {
			if (((Boolean) aValue).booleanValue()) {
				fireTableRowsUpdated(plotsType.getXindex(),plotsType.getXindex());			    
				plotsType.setXindex(rowIndex);
				fireTableRowsUpdated(rowIndex,rowIndex);
				ds.fireAmbitObjectEvent((AmbitObject)plotsType);
				break;
			} 
		}
		case 2: {
			if (((Boolean) aValue).booleanValue()) {
				fireTableRowsUpdated(plotsType.getYindex(),plotsType.getYindex());			    
				plotsType.setYindex(rowIndex);
				fireTableRowsUpdated(rowIndex,rowIndex);
				ds.fireAmbitObjectEvent((AmbitObject)plotsType);
			}	
		}
		}

	}
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0: {
		    if (plotsType.isXDescriptor())
		        return new Boolean(plotsType.getXindex() == rowIndex);
			else return plotsType.getXLabel(ds);
		}
		case 2: {
		    if (plotsType.isYDescriptor())
		        return new Boolean(plotsType.getYindex() == rowIndex);
			else return plotsType.getYLabel(ds);		    
		}
		case 1: {
		    if (plotsType.isXDescriptor() || plotsType.isYDescriptor())
		        return ds.getXname(rowIndex);
		    else return "";
		}		
		default: {
			return null;
		}
		}	        
	}

    public String getColumnName(int col) {
        return columnNames[col].toString();
    }
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		
	    return ((columnIndex == 0) && (plotsType.isXDescriptor())
	            ||
	            ((columnIndex == 2) && (plotsType.isYDescriptor())));
	}
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
	public void updateData(QSARDataset ds) {
		this.ds = ds;
		fireTableDataChanged();
	}        
	public void updateData(QSARDataset ds,IADPlotsType plotsType) {
		this.ds = ds;
		this.plotsType = plotsType;
		fireTableDataChanged();
	}    
}
