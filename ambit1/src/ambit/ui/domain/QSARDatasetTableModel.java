/**
 * Created on 2005-3-29
 *
 */
package ambit.ui.domain;

import javax.swing.table.AbstractTableModel;

import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitObjectListener;
import ambit.domain.AllData;
import ambit.domain.QSARDataset;


/**
 * A table model for {@link ambit.domain.AllData} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class QSARDatasetTableModel extends AbstractTableModel  implements IAmbitObjectListener {
	protected QSARDataset dataset = null;
	/**
	 * 
	 */
	public QSARDatasetTableModel() {
		this(null);

	}	
	public QSARDatasetTableModel(QSARDataset data) {
		super();
		setDataset(data);

	}
    public String getColumnName(int col) {
        if (dataset == null) return ""; 
        switch (col) {
        case 0: return "No.";
        case 1: return "CAS RN";
        case 2: return "Name";
        case 3: return  dataset.getData().getYPredictedName();
        case 4: return  dataset.getData().getYObservedName();
        default: return dataset.getData().getXName(col-5);
        }
    }	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
	    if (dataset == null) return 5;
		return 5 + dataset.getNdescriptors();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
	    if (dataset == null) return 0;
		return dataset.getNpoints();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
	    if (dataset == null) return "";
		AllData list = dataset.getData();
		switch (columnIndex) {
		case 0: {
			return new Integer(rowIndex+1);
		}
		case 1: {
			return list.getCompoundCAS(rowIndex);
		}
		case 2: {
			return list.getCompoundName(rowIndex);
		}		
		case 3: {
			return list.getYPredictedString(rowIndex);
		}
		case 4: {
			return list.getYObservedString(rowIndex);
		}
		default: {
		    try {
			return list.getDataItemToString(rowIndex,columnIndex-5);
		    } catch (Exception x) {
		        return "NA";
		    }
		}
		}
	}

	
    public void ambitObjectChanged(AmbitObjectChanged event) {
    	fireTableStructureChanged();
    	
    }
	public QSARDataset getDataset() {
		return dataset;
	}
	public void setDataset(QSARDataset dataset) {
		
    	if (this.dataset != null) this.dataset.removeAmbitObjectListener(this);
    	this.dataset = dataset;
        if (dataset != null)
        	this.dataset.addAmbitObjectListener(this);
        fireTableStructureChanged();
		
	}
	
}
