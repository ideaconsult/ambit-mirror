/*
 * Created on 2006-3-5
 *
 */
package ambit2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.io.CompoundImageTools;
import ambit2.ui.table.IBrowserMode;



/**
 * Table cell renderer for Image and IAtomconainer.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-5
 */
public class ImageCellRenderer extends DefaultTableCellRenderer implements
        TableCellRenderer, PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4507345423562728344L;

	protected CompoundImageTools imageTools;
    private final ImageIcon icon = new ImageIcon();
    

	/**
     * 
     */
    public ImageCellRenderer(Dimension dimension) {
        super();
        setHorizontalAlignment(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);
        setIcon(icon);
        imageTools = new CompoundImageTools(dimension);
        imageTools.setBackground(new Color(253,253,253));
    }
    
    public ImageCellRenderer() {
        this(new Dimension(100,100));
        
    }
    public Component getTableCellRendererComponent
    (final JTable table, final Object value, final boolean isSelected,
     final boolean hasFocus, final int row, final int column)
    {
    	imageTools.setBorderColor((isSelected?Color.BLUE:Color.LIGHT_GRAY));
    	if (value instanceof IAtomContainer) {
    		setImage(table, isSelected, imageTools.getImage((IAtomContainer)value));
    		return this;
    	} else if (value instanceof Image) { 
    		setImage(table,isSelected,(Image)value);
    		return this;
    	} else return null;
            
	}
    
    protected void setImage(final JTable table,final boolean isSelected, Image value) {
		setFont(null);
		icon.setImage((Image) value);
    }

	public void propertyChange(PropertyChangeEvent evt) {
		if (IBrowserMode.PROPERTY_ZOOM.equals(evt.getPropertyName())) {
			Double size = ((Double)evt.getNewValue());
			imageTools.setImageSize(new Dimension(size.intValue(),size.intValue()));
			
		}
	}
    public Color getSelectedColor() {
    	if (imageTools == null)
    		return Color.BLUE;
    	else
    		return imageTools.getBorderColor();
	}

	public void setSelectedColor(Color selectedColor) {
		if (imageTools != null)
			imageTools.setBorderColor(selectedColor);
	}
    public Color getBackground() {
    	if (imageTools != null)
    		return imageTools.getBackground();
    	else 
    		return Color.WHITE;
	}

	public void setBackground(Color color) {
		if (imageTools != null)
		imageTools.setBackground(color);
	}
	
}
