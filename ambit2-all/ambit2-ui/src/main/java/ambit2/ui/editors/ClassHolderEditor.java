package ambit2.ui.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import ambit2.base.data.ClassHolder;
import ambit2.ui.Utils;

public class ClassHolderEditor {
    public static Color BorderColor = new Color(216, 240, 250);

    public static Color SelectedColor = new Color(244, 250, 253);

    /**
	 * 
	 */

    public static ListCellRenderer createListCellRenderer() {
	return createListCellRenderer(48);
    }

    public static ListCellRenderer createListCellRenderer(int size) {
	return new ImageCellRenderer(size);
    }
}

class ImageCellRenderer extends DefaultListCellRenderer {
    protected int size = 48;
    protected Border shadow = BorderFactory.createLineBorder(ClassHolderEditor.BorderColor);
    protected Color selectedClr = ClassHolderEditor.SelectedColor;
    protected JLabel label;
    /**
	 * 
	 */
    private static final long serialVersionUID = -5986970740680430732L;

    public ImageCellRenderer(int size) {
	super();
	this.size = size;
	this.label = new JLabel();
    }

    public ImageCellRenderer() {
	this(48);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	    boolean cellHasFocus) {
	label.setText(value.toString());
	/*
	 * (JLabel) super.getListCellRendererComponent(list, value, index,
	 * isSelected, cellHasFocus);
	 */
	ClassHolder item = (ClassHolder) value;
	label.setText("<html><b>" + value.toString() + "</b><p>" + item.getDescription() + "</html>");
	try {
	    ImageIcon icon = item.getImage();
	    if (icon == null) {
		icon = Utils.createImageIcon(item.getIcon());
	    }
	    if (icon != null) {
		icon.setImage(icon.getImage().getScaledInstance(size, size, 1));
		label.setIcon(icon);
	    }
	    label.setOpaque(true);

	    if (isSelected) {
		label.setBackground(selectedClr);
		label.setBorder(shadow);
	    } else {
		label.setBorder(null);
		label.setBackground(Color.white);
	    }

	} catch (Exception x) {
	    x.printStackTrace();
	    label.setIcon(null);
	}
	label.setPreferredSize(new Dimension(size * 8, size));
	return label;
    }

}

class ImageListRenderer extends JPanel implements ListCellRenderer {

    private static final long serialVersionUID = 1L;

    private JLabel icon = new JLabel();

    private JLabel title = new JLabel();

    private JTextArea description = new JTextArea();

    private Color evenRowColor = Color.white;

    private Color oddRowColor = new Color(235, 235, 235);

    public ImageListRenderer() {
	super(new BorderLayout());
	add(icon, BorderLayout.WEST);
	add(title, BorderLayout.NORTH);
	add(description, BorderLayout.CENTER);
    }

    public Component getListCellRendererComponent(JList list, Object value,

    int index, boolean isSelected, boolean cellHasFocus) {

	if (isSelected)

	{

	    setBackground(list.getSelectionBackground());

	    setForeground(list.getSelectionForeground());

	    title.setBackground(list.getSelectionBackground());

	    title.setForeground(list.getSelectionForeground());

	    description.setBackground(list.getSelectionBackground());

	    description.setForeground(list.getSelectionForeground());

	}

	else

	{

	    Color c = ((index & 0x1) == 1) ? evenRowColor : oddRowColor;

	    setBackground(c);

	    setForeground(list.getForeground());

	    title.setBackground(c);

	    title.setForeground(list.getForeground());

	    description.setBackground(c);

	    description.setForeground(list.getForeground());

	}

	ClassHolder item = (ClassHolder) value;

	try {
	    String iconFile = item.getIcon();
	    if (iconFile == null)
		iconFile = "images/experiment.png";
	    icon.setIcon(Utils.createImageIcon(iconFile));

	} catch (Exception x) {
	    x.printStackTrace();
	}

	title.setText(item.getTitle());

	description.setText(item.getDescription());

	return this;

    }

}