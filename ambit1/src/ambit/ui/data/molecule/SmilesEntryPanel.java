/*
 * Created on 2005-9-3
 *
 */
package ambit.ui.data.molecule;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.MFAnalyser;

import ambit.data.molecule.SmilesParserWithTimeout;
import ambit.ui.UITools;
import ambit.ui.actions.AmbitAction;



/**
 * A {@link javax.swing.JPanel} to enter a SMILES. Now it supports a history of entered SMILES that can be 
 * navigated back and forward and by a drop down studyList.
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-3
 */
public class SmilesEntryPanel extends StructureEntryPanel implements ItemListener, ActionListener {
	//JFormattedTextField smilesEdit = null;
	AmbitAction drawSmilesAction = null;
	JComboBox smilesBox = null;
	protected JPopupMenu popup;
	protected MFAnalyser mf ;
	protected String caption;
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -8184455436899528629L;

	/**
	 * 
	 */
	public SmilesEntryPanel(AmbitAction drawSmilesAction) {
	    this("SMILES",drawSmilesAction);	
	}
	public SmilesEntryPanel(String caption, AmbitAction drawSmilesAction) {
		super();
		this.caption = caption;
		addWidgets(drawSmilesAction);
	}

	private void initLayout() {
		setLayout(new BorderLayout());
	}


	private void addWidgets( AmbitAction drawSmilesAction) {
		this.drawSmilesAction = drawSmilesAction;
		mf = new MFAnalyser("",DefaultChemObjectBuilder.getInstance().newMolecule());
		//TODO history of entered SMILES to be persistent accross instances
		//TODO select na cialoto edit pole pri click (kakto w IE
		//TODO Go da prawi i estimate

		initLayout();
		//setBackground(Color.black);


        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top,BoxLayout.LINE_AXIS));
        
        Dimension d = new Dimension(48,24);
        JButton backButton = new JButton(new AbstractAction("",UITools.createImageIcon("ambit/ui/images/arrowleft_green_16.png")) {
			private static final long serialVersionUID = 7046410922163328877L;
			public void actionPerformed(ActionEvent arg0) {
        		int index = smilesBox.getSelectedIndex();
        		index = index -1;
        		if (index < 0) index = 0;
        		if (smilesBox.getItemAt(index) != null)
        			smilesBox.setSelectedIndex(index);
        	}
        });
        backButton.setDefaultCapable(false);
        backButton.setPreferredSize(d);
        JButton forwardButton = new JButton(new AbstractAction("",UITools.createImageIcon("ambit/ui/images/arrowright_green_16.png")) {
			private static final long serialVersionUID = 8892599118153155748L;
			public void actionPerformed(ActionEvent arg0) {
        		int index = smilesBox.getSelectedIndex();
        		index = index +1 ;
        		if (index >= smilesBox.getItemCount()) index = smilesBox.getItemCount()-1;
        		if (smilesBox.getItemAt(index) != null)
        			smilesBox.setSelectedIndex(index);        		
        	}
        });     
        forwardButton.setDefaultCapable(false);
        forwardButton.setPreferredSize(d);
        forwardButton.setToolTipText("Forward to the next "+caption);
        backButton.setToolTipText("Back to the previous "+caption);
        top.add(backButton);top.add(forwardButton);

        JLabel labelSmi = new JLabel("<html>  Enter <b>"+caption+"</b>:</html>");
        labelSmi.setOpaque(true);
        //labelSmi.setBackground(Color.black);
        //labelSmi.setForeground(Color.white);
        labelSmi.setPreferredSize(new Dimension(80,24));
        labelSmi.setAlignmentX(CENTER_ALIGNMENT);

        top.add(labelSmi);
        labelSmi.setHorizontalAlignment(SwingConstants.RIGHT);
        
        smilesBox = new JComboBox();
        smilesBox.setEditable(true);
        smilesBox.setFocusable(true);
        //smilesBox.setHorizontalAlignment(JTextField.LEFT);
        smilesBox.setToolTipText("Enter "+caption);
        smilesBox.setPreferredSize(new Dimension(Integer.MAX_VALUE,24));
        
        //smilesEdit.addActionListener(this);
        //smilesBox.addPropertyChangeListener("value",this);
        smilesBox.addItemListener(this);
        popup = new JPopupMenu("Edit");
        JMenuItem mi = new JMenuItem("Copy");
        mi.addActionListener(this);
        popup.add(mi);
        mi = new JMenuItem("Paste");
        mi.addActionListener(this);
        popup.add(mi);
        smilesBox.addMouseListener(new MouseAdapter() {
        	//TODO towa ne raboti
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				if (e.getButton()==MouseEvent.BUTTON3) {
					popup.show(e.getComponent(),e.getX(),e.getY());
				}
			}

        });
        labelSmi.setLabelFor(smilesBox);
        
        //add(labelSmi,BorderLayout.WEST);
        add(top,BorderLayout.WEST);
        add(smilesBox,BorderLayout.CENTER);        
        
        JButton go = new JButton(new AbstractAction("<html><b> Draw </b></html>") {
			private static final long serialVersionUID = 7605151410870720861L;
			public void actionPerformed(ActionEvent e) {
				if (smilesBox.getSelectedItem() != null) {
					String s = smilesBox.getSelectedItem().toString();
					createMoleculeFromSMILES(s);
					
				}
			}
        }
        		);
        
        add(go,BorderLayout.EAST);
        go.setDefaultCapable(true);
        

	}
	protected boolean createMoleculeFromSMILES(String smiles) {
		smiles = smiles.replace('\n',' ').trim();
		SmilesParserWithTimeout sp = new SmilesParserWithTimeout();
    	IMolecule a = null;
    	try {
    		a = (IMolecule) sp.parseSmiles(smiles,5000);
    	} catch (Exception x) {
    		a = null;
    		x.printStackTrace();
    	}
    	if (a != null) { 
	    	a.setProperty(CDKConstants.COMMENT,"Created from SMILES");
	    	a.setProperty("SMILES",smiles);
	    	a.setProperty("FORMULA",mf.analyseAtomContainer(a));
	    	
	        setMolecule(a);
	        
	        smilesBox.addItem(smiles);
	        if (drawSmilesAction != null)
	        drawSmilesAction.actionPerformed(null);
	        return true;
    	} else 
			JOptionPane.showMessageDialog(getParent(),
				    "You have entered an invalid SMILES, please try again.",						
				    "Error while parsing SMILES",
				    JOptionPane.ERROR_MESSAGE);
    	return false;
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent evt) {
	    Object source = evt.getSource();
	    try {
	    	if ((source == smilesBox) && isVisible()) {
	    		if (evt.getStateChange() == ItemEvent.SELECTED) {
	    			String smiles = evt.getItem().toString();
	    	    	if (createMoleculeFromSMILES(smiles))
						smilesBox.repaint();
	    	    	
	    	    }
	    	    repaint();	    	    
	    	}
	    	
	    } catch (NullPointerException ex) {
	    	ex.printStackTrace();
	    }


	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
