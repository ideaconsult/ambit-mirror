/* DescriptorQueryTableModel.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-12 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit.ui.query;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.table.AbstractTableModel;

import ambit.data.AmbitListChanged;
import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitListListener;
import ambit.data.IAmbitObjectListener;
import ambit.database.query.ExperimentQuery;
import ambit.database.query.TemplateFieldQuery;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-12
 */
public class ExperimentsQueryTableModel extends AbstractTableModel implements IAmbitObjectListener, IAmbitListListener {
    protected ExperimentQuery experiments;
    protected static String[] header = {"Use","Field","Condition","Value","","",""};
    protected NumberFormat numberformat = DecimalFormat.getInstance();
    /**
     * 
     */
    public ExperimentsQueryTableModel(ExperimentQuery experiments) {
        super();
        this.experiments = experiments;
        experiments.addAmbitObjectListener(this);
        experiments.addListListener(this);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 6;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return experiments.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        TemplateFieldQuery q = experiments.getFieldQuery(rowIndex);
        boolean b = q.getCondition().equals("between");
        boolean b1 = q.getCondition().equals("ALL");
        switch (columnIndex) {
        case 0: { return new Boolean(q.isEnabled());}
        case 1: { return q;}
        case 2: { return q.getCondition();}
        case 3: { 
            if (b) return TemplateFieldQuery.nf.format(q.getMinValue());
            else if (b1) return ""; 
            	else if (q.isNumeric())
            	return TemplateFieldQuery.nf.format((Double)q.getValue());
            	else return q.getValue();
            	   
        }
	    case 4: { 
	        if (b) return " and ";
	        else return ""; 
	    }                
	    case 5: { 
	        if (b) return TemplateFieldQuery.nf.format(q.getMaxValue());
	        else return "";    
	    }
	    default: return "";
        }
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        TemplateFieldQuery q = experiments.getFieldQuery(rowIndex);
        boolean b = q.getCondition().equals("between");
        switch (columnIndex) {
        case 0: { q.setEnabled(((Boolean)aValue).booleanValue());break;}
        case 2: { experiments.setCondition(rowIndex,aValue.toString());break;}
        case 3: { 
            try {
            if (b) experiments.setMinValue(rowIndex,numberformat.parse(aValue.toString()).doubleValue());
            else {
            	if (q.isNumeric())
            		experiments.setValue(rowIndex,new Double(aValue.toString()));
            	else
            		experiments.setValue(rowIndex,aValue.toString());
            }
            break;
            } catch (ParseException x) {
                
            }
        }
	    case 5: { 
	        try {
	        if (b) experiments.setMaxValue(rowIndex,numberformat.parse(aValue.toString()).doubleValue());
            break;
            } catch (ParseException x) {
                
            }
	    }
	    default: return ;
        }
    }
     /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0: return Boolean.class;
        default: return String.class;
        }
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        TemplateFieldQuery q = experiments.getFieldQuery(rowIndex);
        return (columnIndex != 1)  && (columnIndex != 4) ;
        	
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        return header[column];
    }
    /* (non-Javadoc)
     * @see ambit.data.IAmbitObjectListener#ambitObjectChanged(ambit.data.AmbitObjectChanged)
     */
    public void ambitObjectChanged(AmbitObjectChanged event) {
        if (event.getObject() == experiments) {
        	System.out.println("ambitObjectChanged");
            fireTableDataChanged();
        }    
    }
    public void ambitListChanged(AmbitListChanged event) {
        if (event.getList() == experiments) {
            fireTableStructureChanged();
        }    
    	
    }

	public ExperimentQuery getExperiments() {
		return experiments;
	}

	public void setExperiments(ExperimentQuery experiments) {
		this.experiments = experiments;
		fireTableStructureChanged();
	}
}
