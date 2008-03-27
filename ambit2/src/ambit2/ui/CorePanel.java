/**
 * Created on 2005-1-18
 *
 */
package ambit2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.Format;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.border.EtchedBorder;


/**
 * A parent panel for any panels inAMBIT applications 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public abstract class CorePanel extends JPanel{
	protected Object layout; 
	protected Color foreClr = AmbitColors.DarkClr;
	protected Color backClr = AmbitColors.BrightClr;
	protected String caption;
	
	/**
	 * 
	 */
	public CorePanel(String title) {
		super();
		caption = title;
		initLayout();
		addWidgets();
	}
	
	public CorePanel(String title,  Color  bClr, Color fClr) {
		super();
		caption = title;
		backClr = bClr;
		foreClr = fClr;
		initLayout();
		addWidgets();
	}
	

	protected void initLayout() {
		layout = new GridBagLayout() ;
		setLayout((GridBagLayout) layout);
		setBackground(backClr);
		setBorder(BorderFactory.createMatteBorder(5,5,5,5,backClr));
		
   }
	
	protected JLabel createTitledLabel(String title, String value, String tooltip, Color fClr, boolean edge) {
		JLabel l = new JLabel(value);		
		l.setOpaque(true);
		l.setBackground(backClr);
		l.setForeground(fClr);
		l.setPreferredSize(new Dimension(120, 20));
		l.setToolTipText(tooltip);
		if (edge)
			l.setBorder(BorderFactory.createTitledBorder(title));		
		return l;
	}

	protected void decorateTextField(JFormattedTextField tfield, Color fClr,Color bClr, boolean editable) {
		tfield.setOpaque(true);
		tfield.setBackground(bClr);
		tfield.setForeground(fClr);
		tfield.setPreferredSize(new Dimension(80, 20));		
		tfield.setMinimumSize(new Dimension(20, 20));
		tfield.setEditable(editable);
		tfield.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		tfield.setHorizontalAlignment(JTextField.LEFT);
	}	
	
	protected AmbitTextField createTextField(String value, Color fClr, Color bClr, boolean editable) {
		AmbitTextField l = new AmbitTextField(value);
		decorateTextField(l,fClr,bClr,editable);
		return l;
	}
	protected AmbitTextField createNumericField(Number value, Format f, Color fClr,Color bClr, boolean editable) {
		AmbitTextField l = new AmbitTextField(f);
		l.setValue(value);
		decorateTextField(l,fClr,bClr,editable);
		return l;
	}	
	
	
	protected JLabel createEtchedLabel(String value, Color fClr, boolean edge) {
		JLabel l = new JLabel(value);		
		l.setOpaque(true);
		l.setBackground(backClr);
		l.setForeground(fClr);
		if (edge)
			l.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED,Color.gray,Color.lightGray));
		l.setPreferredSize(new Dimension(64, 20));		
		l.setMinimumSize(new Dimension(32, 12));		
		return l;
	}	

	protected JLabel createColorLabel(String value, Color fClr, boolean edge) {
		JLabel l = new JLabel(value);		
		l.setOpaque(true);
		l.setBackground(fClr);
		l.setForeground(foreClr);
		if (edge) 
		l.setBorder( 
		        BorderFactory.createCompoundBorder(
						BorderFactory.createEtchedBorder(EtchedBorder.RAISED,fClr,Color.gray),
						BorderFactory.createEmptyBorder(2,2,2,2)									
				));
		return l;
	}
	protected JSpinner createSpinner(TreeMap labels, TreeMap edits,
			String key,
			String caption,
			String value,
			String tooltip,
			SpinnerModel model) {
	JLabel label  = createColorLabel(caption, backClr, false);
	
	JSpinner spinner = new JSpinner(model);
	
	spinner.setBackground(backClr);
	spinner.setForeground(foreClr);
	spinner.setToolTipText(tooltip);
	label.setLabelFor(spinner); 
	labels.put(key,label);
	edits.put(key,spinner);	
	return spinner;
}	
	protected JComboBox createCombo(TreeMap labels, TreeMap edits,
			String key,
			String caption,
			String value,
			String tooltip,
			String[] patterns,
			boolean editable) {
	JLabel label  = createColorLabel(caption, backClr, false);
	JComboBox combo = new JComboBox(patterns);
	combo.setSelectedItem(value);
	combo.setBackground(backClr);
	combo.setForeground(foreClr);
	combo.setEditable(editable);
	combo.setToolTipText(tooltip);
	label.setLabelFor(combo); 
	labels.put(key,label);
	edits.put(key,combo);	
	return combo;
}
	protected JPasswordField createPasswordWidget(TreeMap labels, TreeMap edits,
			String key,
			String caption,
			String value,
			String tooltip,
			boolean editable) {
	JLabel label  = createColorLabel(caption, backClr, false);		
	JPasswordField edit = new JPasswordField(value);
	edit.setBackground(backClr);
	edit.setForeground(foreClr);
	edit.setToolTipText(tooltip);
	edit.setEditable(editable);
	label.setLabelFor(edit); 
	labels.put(key,label);
	edits.put(key,edit);	
	return edit;
	}
	protected AmbitTextField createWidget(TreeMap labels, TreeMap edits,
			String key,
			String caption,
			String value,
			String tooltip,
			boolean editable) {
	JLabel label  = createColorLabel(caption, backClr, false);
	label.setMinimumSize(new Dimension(64,20));	
	AmbitTextField edit = createTextField(value,foreClr,backClr,editable);
	edit.setMinimumSize(new Dimension(64,20));
	edit.setToolTipText(tooltip);
	label.setLabelFor(edit); 
	labels.put(key,label);
	edits.put(key,edit);	
	return edit;
	}
	protected void placeWidgets(TreeMap labels, TreeMap edits,
			GridBagConstraints c) {
		placeWidgets(labels,edits,c,2);
	}
	protected void placeWidgets(TreeMap labels, TreeMap edits,
			GridBagConstraints c, int columns) {
       
		Set keys = labels.keySet();
	    Iterator it = keys.iterator();
	    Object key;
	    Component element;
	    int i = 1;
    	while (it.hasNext()) {
	        key = it.next();
	        element = (Component) labels.get(key);

        	c.weightx = 0;
        	c.weighty = 0;	        
    		c.fill = GridBagConstraints.HORIZONTAL;	  
        	c.gridwidth = GridBagConstraints.RELATIVE;
			c.anchor = GridBagConstraints.NORTH;
        	((GridBagLayout) layout).setConstraints(element,c);
        	add(element); i++;
        
	        element = (Component) edits.get(key);
        	c.weightx = 0.5;
        	if ((i % columns) == 0)
        		c.gridwidth = GridBagConstraints.REMAINDER;
        	else
        		c.gridwidth = GridBagConstraints.RELATIVE;        	
        	((GridBagLayout) layout).setConstraints(element,c);
			add(element); i++;       	        	 
    	}

	}
	abstract protected void addWidgets();

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//g.drawImage(myBackground, this.getWidth() - 256, this.getHeight() - 256, this);
	}
	
	
}