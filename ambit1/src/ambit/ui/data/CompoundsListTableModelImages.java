/*
 * Created on 2006-3-5
 *
 */
package ambit.ui.data;

import java.awt.Dimension;

import javax.swing.table.AbstractTableModel;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;

import ambit.data.AmbitList;
import ambit.data.AmbitListChanged;
import ambit.data.IAmbitListListener;
import ambit.data.AmbitObjectChanged;
import ambit.data.molecule.AmbitPoint;
import ambit.data.molecule.Compound;
import ambit.misc.AmbitCONSTANTS;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-5
 */
public class CompoundsListTableModelImages extends AbstractTableModel implements ICompoundsListTableModel, IAmbitListListener {
    protected String[] columnNames={"No.",
            CDKConstants.NAMES,CDKConstants.CASRN,"SMILES","Structure",
            "Predicted","Observed","Domain","Structural domain"};
    protected AmbitList list;
    protected CompoundImageTools imageTools;

    /**
     * 
     * @param list
     * @param cellSize
     */
    public CompoundsListTableModelImages(AmbitList list,Dimension cellSize) {
        super();
        this.list = list;
        this.list.addAmbitObjectListener(this);
        imageTools = new CompoundImageTools(cellSize);
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columnNames.length;
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return list.size();
    }
    /* (non-Javadoc)
     * @see ambit.ui.data.GridTableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object o = list.getItem(rowIndex);
        if (o == null) return null;
        else 
            switch (columnIndex) {
            case 0: return Integer.toString(rowIndex+1);
            case 1: return getName(o);
            case 2: return getCASRN(o);
            case 3: return getSMILES(o);
            case 4: return imageTools.getImage(o);
            case 5: return getPredicted(o);
            case 6: return getObserved(o);
            case 7: return getDomain(o);
            case 8: return getStructuralDomain(o);
            default: return "";
            }
        
    }
    public String getName(Object o) {
        if (o instanceof Compound) 
            return ((Compound)o).getName();
        else if (o instanceof AmbitPoint) return ((AmbitPoint)o).getCompound().getName();
        else if (o instanceof Molecule) {
            Object n = ((Molecule) o).getProperty(CDKConstants.NAMES);
            if (n != null) return n.toString(); else return "";
        }
        else return "";
    }
    public String getCASRN(Object o) {
        if (o instanceof Compound) 
            return ((Compound)o).getName();
        else if (o instanceof AmbitPoint) return ((AmbitPoint)o).getCompound().getCAS_RN();
        else if (o instanceof Molecule) {
            Object n = ((Molecule) o).getProperty(CDKConstants.CASRN);
            if (n != null) return n.toString(); else return "";
        }
        else return "";
    }
    public String getSMILES(Object o) {
        if (o instanceof Compound) 
            return ((Compound)o).getSMILES();
        else if (o instanceof AmbitPoint) return ((AmbitPoint)o).getCompound().getSMILES();
        else if (o instanceof Molecule) {
            Object n = ((Molecule) o).getProperty(AmbitCONSTANTS.SMILES);
            if (n != null) return n.toString(); else return "";
        }
        else return "";
    }
    public String getPredicted(Object o) {
        if (o instanceof Compound) 
            return "-";
        else if (o instanceof AmbitPoint) return ((AmbitPoint) o).getYPredictedString();
        else if (o instanceof Molecule) {
            Object n = ((Molecule) o).getProperty("Predicted");
            if (n != null) return n.toString(); else return "-";
        }
        else return "-";
    }   
    //TODO
    public String getObserved(Object o) {
        if (o instanceof Compound) 
            return "-";
        else if (o instanceof AmbitPoint) return ((AmbitPoint) o).getYObservedString();
        else if (o instanceof Molecule) {
            Object n = ((Molecule) o).getProperty("Observed");
            if (n != null) return n.toString(); else return "-";
        }
        else return "-";
    }    
    //TODO
    public String getDomain(Object o) {
        if (o instanceof Compound) 
            return "-";
        else if (o instanceof AmbitPoint) return ((AmbitPoint) o).getDomainString();
        else if (o instanceof Molecule) {
            Object n = ((Molecule) o).getProperty("Domain");
            if (n != null) return n.toString(); else return "-";
        }
        else return "-";
    }
    //TODO
    public String getStructuralDomain(Object o) {
        if (o instanceof Compound) 
            return "";
        else if (o instanceof AmbitPoint) return ((AmbitPoint) o).getStructuralDomainString();
        else if (o instanceof Molecule) {
            Object n = ((Molecule) o).getProperty("Structural domain");
            if (n != null) return n.toString(); else return "";
        }
        else return "";
    }    
    
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        return columnNames[column];
    }
    public Dimension getImageSize() {
        return imageTools.getImageSize();
    }
    public Class getColumnClass(int column) {
        return getValueAt(0,column).getClass();
      }
    /* (non-Javadoc)
     * @see ambit.ui.data.ICompoundsListTableModel#getList()
     */
    public AmbitList getList() {
     
        return list;
    }
    public void setList(AmbitList list) {
    	this.list = list;
    	fireTableStructureChanged();
    	
    }
    public void ambitListChanged(AmbitListChanged event) {
    	fireTableStructureChanged();
    	
    }
    public void ambitObjectChanged(AmbitObjectChanged event) {
    	fireTableDataChanged();
    	
    }
}
