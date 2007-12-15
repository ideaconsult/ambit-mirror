package ambit.ui.data;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.table.AbstractTableModel;

import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;

import ambit.data.descriptors.AmbitColumnType;

public class HashtableModel extends AbstractTableModel {
	protected String[] columnNames = {"Property","Type",};
	protected Hashtable table;
	protected ArrayList keys;
	protected boolean[] enabled;
    boolean includeTranslated = true;
    protected NumberFormat nf = NumberFormat.getInstance();
    protected boolean showValues = true;
    
	
    public HashtableModel(Hashtable table) {
        this(table,true);
    }
	public HashtableModel(Hashtable table, boolean includeTranslated) {
		super();
		keys = new ArrayList();
		enabled = null;
        this.includeTranslated = includeTranslated;
		setTable(table);
        nf.setMaximumFractionDigits(4);	
	}

	public int getColumnCount() {
		if (showValues) return 2; else return 1;
	}

	public int getRowCount() {
		return keys.size();
	}

	public Object getValueAt(int row, int col) {
	    Object key = keys.get(row);
	    if (key == null) return "NA";
        if (includeTranslated)
    		switch (col) {
    		case 0: return key;
    		case 1: { Object o = table.get(key); 
    			if (o==null) return "NA"; 
                else if (o instanceof Number){
                    return nf.format(((Number)o).doubleValue());
                }  else return o;
                }
    		default: return "";
    		}
        else
            switch (col) {
            case 1: { Object o =key; 
                if (o==null) return "NA"; 
                else return o; 
                }
            case 0: return new Boolean(enabled[row]);
            default: return "";
            }

		
	}
	/* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (includeTranslated)
            switch (columnIndex) {
            case 1: {
                Object key = keys.get(rowIndex);
                Object o = table.get(key);
                if (o instanceof AmbitColumnType)
                    ((AmbitColumnType) o).setName(aValue.toString());
                else table.put(key, aValue);
                break;
            }
            default:
            }
       else
           switch (columnIndex) {
           case 0: {
            enabled[rowIndex] = ((Boolean) aValue).booleanValue();
            break;
           }
           };
    }
	/* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (includeTranslated)
            return columnIndex ==1;
        else
            return columnIndex ==0;
    }

	public Hashtable getTable() {
		if (enabled != null)
			for (int i=0; i < enabled.length; i++) 
				if (!enabled[i]) {
					table.remove(keys.get(i));
				}
		setTable(table);
		return table;
	}
	protected void  sort(ArrayList keys) {
        Collections.sort(keys);
    }
	public void setTable(Hashtable table) {
	    this.table = table;
	    
		keys.clear();
		if (table != null) {
			Enumeration e = table.keys();
			while (e.hasMoreElements()) {
				Object key = e.nextElement();
				if (accept(key,table.get(key)))
				keys.add(key);
			}
            try {
                sort(keys);
            } catch (Exception x) {
                //x.printStackTrace();
            }
			enabled = new boolean[keys.size()];
			for (int i=0; i < enabled.length; i++) enabled[i] = true;
		}
		fireTableStructureChanged();
	}
	public String getColumnName(int arg0) {
		return columnNames[arg0];
	}
	public Class getColumnClass(int columnIndex) {
        if (!includeTranslated && (columnIndex == 0)) return Boolean.class;
		else
		return super.getColumnClass(columnIndex);
	}
	protected boolean accept(Object key,Object value) {
		return true;
	}
    public void put(Object key,Object value) {
        table.put(key,value);
        setTable(table);
    }
    public void enableAll(boolean value) {
        for (int i=0; i < enabled.length; i++) enabled[i] = value;
        fireTableDataChanged();
    }
	public boolean isShowValues() {
		return showValues;
	}
	public void setShowValues(boolean showValues) {
		this.showValues = showValues;
	}
	public String[] getColumnNames() {
		return columnNames;
	}
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
}
