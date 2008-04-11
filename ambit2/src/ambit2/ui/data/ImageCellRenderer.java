/*
 * Created on 2006-3-5
 *
 */
package ambit2.ui.data;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-5
 */
public class ImageCellRenderer extends DefaultTableCellRenderer implements
        TableCellRenderer {
	protected CompoundImageTools imageTools;
    private final ImageIcon icon = new ImageIcon();
    protected MatteBorder selectedBorder = null;
    /**
     * 
     */
    public ImageCellRenderer(Dimension dimension) {
        super();
        setHorizontalAlignment(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);
        setIcon(icon);
        imageTools = new CompoundImageTools(dimension);
        
    }
    
    public ImageCellRenderer() {
        this(new Dimension(100,100));
        
    }
    public Component getTableCellRendererComponent
    (final JTable table, final Object value, final boolean isSelected,
     final boolean hasFocus, final int row, final int column)
    {
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
		if (isSelected)
		{
            if (selectedBorder ==null)
                selectedBorder = BorderFactory.createMatteBorder(4,4,4,4, table.getSelectionBackground());
            setBorder(selectedBorder);
		}
		else
		{
            setBorder(null);
		//setBackground(null);
		}			    	
    }
}
