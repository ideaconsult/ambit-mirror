/**
 * Created on 2005-1-18
 *
 */
package ambit2.ui.domain;

import javax.swing.table.DefaultTableModel;

import ambit2.domain.QSARdatasets;


/**
 * A table model for {@link ambit2.domain.QSARdatasets} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ADDataSetTableModel extends DefaultTableModel {
	protected QSARdatasets datasets = null;
	protected String[] columnNames = 
	{		"Name",  "Endpoint", 
			"No.points", "No.descriptors"};
	/**
	 * 
	 */
	public ADDataSetTableModel(QSARdatasets ds) {
		super();
		this.datasets = ds;
		
	}
    public String getColumnName(int col) {
        return columnNames[col].toString();
    }
    public int getRowCount() { 
    	if (datasets != null)	return datasets.size(); else return 0; 
    }
    public int getColumnCount() { 
    	return columnNames.length; 
    }

    
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
    public Object getValueAt(int row, int col) {
    	if (datasets == null) return new Integer(0);
    	switch (col) {
    	//case 0: return new Boolean(ds.isVisible(row));
    	//case 1: return new Boolean(ds.isAdEstimated(row));    	
    	case 0: return datasets.getCaption(row);
    	case 1:return datasets.getYname(row);
    	case 2:return new Integer(datasets.getNpoints(row));    	
    	case 3:return new Integer(datasets.getNdescriptors(row));
    	}
        return null;
    }
	/*
    
    public void setValueAt(Object value, int row, int col) {
    	if (col ==0) {
    		ds.setVisible(row,((Boolean) value).booleanValue());
    		fireTableCellUpdated(row, col);
    	}
    }
    	*/

    public boolean isCellEditable(int row, int col) {
    	return false;
        //{ if (col > 0) return false; else return true; }
    }
    
	/**
	 * @param datasets The datasets to set.
	 */
	protected void setDatasets(QSARdatasets datasets) {
		this.datasets = datasets;
	}
}
