/*
 * Created on 2006-3-5
 *
 */
package ambit2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.io.ICompoundImageTools;
import ambit2.rendering.CompoundImageTools;
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

	protected ICompoundImageTools imageTools;
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
        imageTools.setBackground(Color.white);//new Color(253,253,253));
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
    		try {
    		setImage(table, isSelected, imageTools.getImage((IAtomContainer)value));
    		} catch (Exception x) {}
    		return this;
    	} else if (value instanceof Image) { 
    		scaleImage(table,isSelected,(Image)value);
    		return this;
    	} else return null;
            
	}
    protected void scaleImage(final JTable table,final boolean isSelected, Image image) {
		setFont(null);
		Dimension d = imageTools.getImageSize();
		BufferedImage scaledImage = new BufferedImage(d.width,d.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = scaledImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, d.width,d.height, null);
		graphics2D.dispose();		
		icon.setImage(scaledImage);
    }    
    protected void setImage(final JTable table,final boolean isSelected, Image value) {
		setFont(null);
		icon.setImage((Image) value);
    }

	public void propertyChange(PropertyChangeEvent evt) {
		if (IBrowserMode.PROPERTY_ZOOM.equals(evt.getPropertyName())) {
			Dimension size = ((Dimension)evt.getNewValue());
			imageTools.setImageSize(size);
			
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
