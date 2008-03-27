/*
 * Created on 2006-3-5
 *
 */
package ambit2.ui.data;

import java.awt.Dimension;

import ambit2.data.AmbitList;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-5
 */
public class GridTableModelImages extends GridTableModel {
    protected CompoundImageTools imageTools;
 
    /**
     * 
     * @param list {@link AmbitList} to be displayed
     * @param columns  Number of columns in the grid
     * @param cellSize The size off the grid cell
     */
    public GridTableModelImages(AmbitList list, int columns,Dimension cellSize) {
        super(list, columns);
        imageTools = new CompoundImageTools(cellSize);
    }
    /* (non-Javadoc)
     * @see ambit2.ui.data.GridTableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object o = super.getValueAt(rowIndex, columnIndex);
        return imageTools.getImage(o);
    }
    public Dimension getImageSize() {
        return imageTools.getImageSize();
    }

}
