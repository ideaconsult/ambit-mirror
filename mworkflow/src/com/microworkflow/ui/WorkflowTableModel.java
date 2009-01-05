package com.microworkflow.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.microworkflow.process.Activity;
import com.microworkflow.ui.ILookAtActivity;
import com.microworkflow.ui.WorkflowTools;

public class WorkflowTableModel extends AbstractTableModel  {
    ArrayList<Activity> activities;
    protected String[] columnNames = {"No.","Running","Activity"};
    protected int selected = -1;
    protected static final String PTR= ">>";
    protected static final String NA= "";
    /**
     * 
     */
    private static final long serialVersionUID = -3757282646931180423L;
    public WorkflowTableModel(ArrayList<Activity> activities) {
        super();
        setActivities(activities);
    }
    public ArrayList<Activity> getActivities() {
		return activities;
	}
	public void setActivities(ArrayList<Activity> activities) {
		this.activities = activities;
        fireTableStructureChanged();
	}
	public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
    	if (activities == null) return 0;
    	else
    		return activities.size();
    }

    public Object getValueAt(int row, int col) {
        switch (col) {
        case 0:
            return row+1;
        case 2:
        	if (selected == row)
        		return PTR; 
        	else return NA;            
        case 1:
            return activities.get(row).getName();

        default:
            return row;
        }
    }
    public Activity getActivity(int row) {
    	return activities.get(row);
    }

    public int findRow(Activity activity) {
    	int old_row = getSelected();
        setSelected(activities.indexOf(activity));
        int row = getSelected();
        fireTableCellUpdated(old_row ,1);
        fireTableCellUpdated(row ,1);
        return row;
    }
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }
	public int getSelected() {
		return selected;
	}
	public void setSelected(int selected) {
		this.selected = selected;
	}

}

