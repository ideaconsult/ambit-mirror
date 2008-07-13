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

import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitObjectListener;
import ambit.database.query.DescriptorQuery;
import ambit.database.query.DescriptorQueryList;
import ambit.database.query.DistanceQuery;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-12
 */
public class DescriptorQueryTableModel extends AbstractTableModel implements IAmbitObjectListener {
    protected DescriptorQueryList descriptors;
    protected String[] header = {"Use","Descriptor","Atom","Atom","Condition","Value","","",""};
    protected boolean captionsOnly = false;
    protected NumberFormat numberformat = DecimalFormat.getInstance();
    /**
     * 
     */
    public DescriptorQueryTableModel(DescriptorQueryList descriptors,boolean captionsOnly) {
        super();
        setDescriptors(descriptors);
        this.captionsOnly = captionsOnly;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        if (captionsOnly) return 2; else
        return 8;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return descriptors.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        DescriptorQuery q = descriptors.getDescriptorQuery(rowIndex);
        boolean b = q.getCondition().equals("between");
        boolean d = q instanceof DistanceQuery;
        switch (columnIndex) {
        case 0: { return new Boolean(q.isEnabled());}
        case 1: { return q;}
        case 2: { if (d) return ((DistanceQuery) q).getAtom1(); else return "";}
        case 3: { if (d) return ((DistanceQuery) q).getAtom2(); else return "";}
        case 4: { return q.getCondition();}
        case 5: { 
            if (b) return DescriptorQuery.nfGUI.format(q.getMinValue());
            else return DescriptorQuery.nfGUI.format(q.getValue());   
        }
	    case 6: { 
	        if (b) return " and ";
	        else return ""; 
	    }                
	    case 7: { 
	        if (b) return DescriptorQuery.nfGUI.format(q.getMaxValue());
	        else return "";    
	    }
	    default: return "";
        }
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        DescriptorQuery q = descriptors.getDescriptorQuery(rowIndex);
        boolean b = q.getCondition().equals("between");
        boolean d = q instanceof DistanceQuery;
        switch (columnIndex) {
        case 0: { q.setEnabled(((Boolean)aValue).booleanValue());break;}
        case 2: { if (d) ((DistanceQuery) q).setAtom1(aValue.toString());}
        case 3: { if (d) ((DistanceQuery) q).setAtom2(aValue.toString());}        
        case 4: { descriptors.setCondition(rowIndex,aValue.toString());break;}
        case 5: { 
            
            if (b)
            	try {
            	descriptors.setMinValue(rowIndex,numberformat.parse(aValue.toString()).doubleValue());
                } catch (ParseException x) {
                	descriptors.setMinValue(rowIndex,0);
                }
            else try {
                descriptors.setValue(rowIndex,numberformat.parse(aValue.toString()).doubleValue());
            	} catch (ParseException x) {
            		descriptors.setValue(rowIndex,1);
            	}
            break;
            
        }
	    case 7: { 
	        try {
	        if (b) descriptors.setMaxValue(rowIndex,numberformat.parse(aValue.toString()).doubleValue());
            break;
            } catch (ParseException x) {
            	descriptors.setMaxValue(rowIndex,1);
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
        DescriptorQuery q = descriptors.getDescriptorQuery(rowIndex);
        if (q instanceof DistanceQuery) return (columnIndex != 1)  && (columnIndex != 6);
        else return (columnIndex != 1)  && (columnIndex != 6) 
        	&& (columnIndex != 2)  && (columnIndex != 3) ;
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
        if (event.getObject() == descriptors)
            fireTableDataChanged();

    }

	public DescriptorQueryList getDescriptors() {
		return descriptors;
	}

	public void setDescriptors(DescriptorQueryList descriptors) {
		if (this.descriptors != null)
			this.descriptors.removeAmbitObjectListener(this);
		this.descriptors = descriptors;
		if (this.descriptors != null)
			this.descriptors.addAmbitObjectListener(this);
		fireTableStructureChanged();
	}
}
