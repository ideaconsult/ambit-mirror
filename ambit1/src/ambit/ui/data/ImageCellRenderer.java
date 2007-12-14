/*
 * Created on 2006-3-5
 *
 */
package ambit.ui.data;

import java.awt.Component;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-5
 */
public class ImageCellRenderer extends DefaultTableCellRenderer implements
        TableCellRenderer {
    private final ImageIcon icon = new ImageIcon();
    protected MatteBorder selectedBorder = null;
    /**
     * 
     */
    public ImageCellRenderer() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);
        setIcon(icon);
        
    }
    public Component getTableCellRendererComponent
    (final JTable table, final Object value, final boolean isSelected,
     final boolean hasFocus, final int row, final int column)
    {
    	if (!(value instanceof Image)) return null;
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
            return this;
	}
}
