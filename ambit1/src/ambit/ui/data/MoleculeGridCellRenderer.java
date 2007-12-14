/*
 * Created on 2006-3-5
 *
 */
package ambit.ui.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.renderer.Renderer2DModel;

import ambit.data.molecule.AmbitPoint;
import ambit.data.molecule.Compound;
import ambit.ui.data.molecule.Panel2D;



/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-5
 */
public class MoleculeGridCellRenderer extends JPanel implements
        TableCellRenderer {
    protected JLabel label;
    protected Panel2D panel2D;
    protected int number;
    protected Border selectedBorder = null;
    protected Border unselectedBorder = null;
    public MoleculeGridCellRenderer(Dimension size) {
        super();
        panel2D = new Panel2D(size);
        addWidgets();
    }    
    public MoleculeGridCellRenderer(IAtomContainer a) {
        super();
        panel2D = new Panel2D(new Dimension(200, 200));
        addWidgets();
    }
    public MoleculeGridCellRenderer(Compound a) {
        super();
        panel2D = new Panel2D(a.getMolecule(),new Renderer2DModel(),new Dimension(200, 200));
        addWidgets();
    }    
    protected void addWidgets() {
        setLayout(new BorderLayout());
        add(panel2D,BorderLayout.CENTER);

        label = new JLabel();
        label.setOpaque(true);
        label.setBackground(Color.white);
        add(label,BorderLayout.NORTH);
        

    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) return null;
        
        IMolecule m = null;
        String name = "";
        if (value instanceof Compound) {
            m = ((Compound) value).getMolecule();
            name = ((Compound) value).getName();
        }
        else if (value instanceof IMolecule)  m = (IMolecule) value;
        else if (value instanceof AmbitPoint) {
            m = ((AmbitPoint) value).getCompound().getMolecule();
            name = ((AmbitPoint) value).getCompound().getName();
        }
        
        try {
            panel2D.setAtomContainer(m,!GeometryTools.has2DCoordinates(m));
            if (name.equals(""))
	            if (m.getProperty(CDKConstants.NAMES) != null)
	                name = m.getProperty(CDKConstants.NAMES).toString();
	            else name = "";
            
        } catch (Exception x) {
            panel2D.setAtomContainer(null,false);
        }
        setNumber(row*table.getColumnCount()+column);
        label.setText(Integer.toString(number+1) + "." + name);
        
        if (isSelected) {
            if (selectedBorder == null) 
                selectedBorder = BorderFactory.createLineBorder(table.getSelectionBackground(),3); 
            setBorder(selectedBorder);
        }
        else {
            if (unselectedBorder == null)
                unselectedBorder = BorderFactory.createLineBorder(table.getBackground()); 
            setBorder(unselectedBorder);
        }
        return this;
    }

    public synchronized int getNumber() {
        return number;
    }
    public synchronized void setNumber(int number) {
        this.number = number;

    }
  
}
