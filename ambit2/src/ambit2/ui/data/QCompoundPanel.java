/**
 * Created on 2005-2-2
 *
 */
package ambit2.ui.data;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.config.AmbitCONSTANTS;
import ambit2.ui.data.molecule.Panel2D;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.data.AmbitObject;
import ambit2.data.molecule.Compound;
import ambit2.exceptions.AmbitException;
import ambit2.ui.CorePanel;



/**
 * A panel to display a {@link Compound} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class QCompoundPanel extends CorePanel implements IAmbitEditor, PropertyChangeListener {
	protected TreeMap labels;
	protected TreeMap edits;
	protected Panel2D picturePanel;
	protected boolean editable = true;
	protected JFormattedTextField smilesEdit;
	protected JFormattedTextField casEdit;
	protected JFormattedTextField formulaEdit;
	protected JFormattedTextField nameEdit;	
	//
	private Compound molecule = null;
	private String oldSmiles = "";
	/**
	 * @param title
	 */
	public QCompoundPanel(String title) {
		super(title);
	}

	/**
	 * @param title
	 * @param bClr
	 * @param fClr
	 */
	public QCompoundPanel(String title, Color bClr, Color fClr) {
		super(title, bClr, fClr);

	}

	/* (non-Javadoc)
	 * @see ambit2.ui.CorePanel#addWidgets()
	 */
	protected void addWidgets() {
		setMinimumSize(new Dimension(200,200));		
		labels = new TreeMap();
		edits = new TreeMap();
		JLabel labelTitle = new JLabel("<html><b>Molecule</b></html>");
		labelTitle.setOpaque(true);
		labelTitle.setForeground(backClr);
		labelTitle.setBackground(foreClr);
        labelTitle.setMaximumSize(new Dimension(Short.MAX_VALUE,24));
        labelTitle.setMinimumSize(new Dimension(100,24));
        editable = true;        
		//TODO Load strings fro resource	
		casEdit = createWidget(labels,edits,AmbitCONSTANTS.CASRN,"<html><b>CAS</b><html>","","CAS registry number(format 9999-99-9) e.g. 97-53-0",!editable);
		casEdit.addPropertyChangeListener("value",this);
		formulaEdit = createWidget(labels,edits,AmbitCONSTANTS.FORMULA,"<html><b>Formula</b><html>","","Chemical formula",!editable);
		formulaEdit.addPropertyChangeListener("value",this);		
		nameEdit = createWidget(labels,edits,AmbitCONSTANTS.NAMES,"<html><b>Name</b><html>","","Chemical name (case insensitive) e.g.Eugenol",!editable);
		nameEdit.addPropertyChangeListener("value",this);
		
		smilesEdit = createWidget(labels,edits,AmbitCONSTANTS.SMILES,"<html><b>SMILES</b><html>","","SMILES e.g. COc1cc(CC=C)ccc1O ",!editable);
		smilesEdit.addPropertyChangeListener("value",this);		
		
		//createWidget(labels,edits,AmbitCONSTANTS.DATASET,"<html><b>Origin</b><html>","","The dataset this compound was imported from",!editable);		
		
//		smilesEdit = (JFormattedTextField) (edits.get(AmbitCONSTANTS.SMILES));
        smilesEdit.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
        smilesEdit.setHorizontalAlignment(JTextField.LEFT);
		
        /*
		smilesEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				display();
			}	
		});
		*/		
        picturePanel = new Panel2D();
        picturePanel.setOpaque(true);
		picturePanel.setBackground(backClr);
		picturePanel.setForeground(foreClr);
        picturePanel.setBorder(BorderFactory.createTitledBorder("Structure diagram"));
		picturePanel.setPreferredSize(new Dimension(200,200));        
		picturePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		picturePanel.setMinimumSize(new Dimension(100,100));		
				
		
		GridBagConstraints c = new GridBagConstraints();
    	c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
       	c.anchor = GridBagConstraints.NORTH;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(1,1,1,1);		
        ((GridBagLayout) layout).setConstraints(labelTitle,c);
        add(labelTitle);
        
        placeWidgets(labels,edits,c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.SOUTH;
    	c.fill = GridBagConstraints.BOTH;        
        c.weightx = 0;    
		c.weighty = 0;
        ((GridBagLayout) layout).setConstraints(picturePanel,c);
        add(picturePanel);    	
	}

	private void display() {
		String smiles = molecule.getSMILES();
		if (smiles.equals(oldSmiles)) return;
		oldSmiles = smiles;
		try {
		if (smiles.equals("") ||  smiles.equals("NA")) {
			picturePanel.setAtomContainer(null,false);
		} else {
			//try {
				molecule.updateMolecule();
				picturePanel.setAtomContainer(molecule.getMolecule(),true);
                /*
			} catch (InvalidSmilesException e) {
				picturePanel.setAtomContainer(null,false);			
				JOptionPane.showMessageDialog(getParent(),
					    smiles,						
					    "Invalid SMILES",
					    JOptionPane.ERROR_MESSAGE);
			}
            */
		}
		} catch (Exception ex) {
			picturePanel.setAtomContainer(null,false);
			repaint();
			ex.printStackTrace();

		}	
	}

	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
		Set keys = edits.keySet();
	    Iterator it = keys.iterator();
	    Object key;
	    JFormattedTextField element;
    	while (it.hasNext()) {
	        key = it.next();
	        ((JFormattedTextField) edits.get(key)).setEditable(editable);
	        
		}		
	}
	public IMolecule getMol() {
		if (molecule == null) molecule = new Compound(); 

		Set keys = edits.keySet();
	    Iterator it = keys.iterator();
	    Object key;
	    JFormattedTextField element;
    	while (it.hasNext()) {
	        key = it.next();
	        element = (JFormattedTextField) edits.get(key);
	        String s = element.getText();
	        molecule.getMolecule().setProperty(key,s);
		}		
		return molecule.getMolecule();
	}
	
	public void setMol(IMolecule mol) throws AmbitException {
		this.molecule.setMolecule(mol,null);
		
		Set keys = edits.keySet();
	    Iterator it = keys.iterator();
	    Object key;
	    JFormattedTextField element;
    	while (it.hasNext()) {
	        key = it.next();
	        element = (JFormattedTextField) edits.get(key);
			if (mol == null) element.setText("NA");
			else { 	        
	        	Object txt = mol.getProperty(key);
	        	if (txt == null) element.setText("");
	        	else element.setText((String)txt);
	        }
		}		
		display();
	}
	public void setCompound(Compound mol) {
		this.molecule = mol;
		setEditable(!mol.hasID());
		Set keys = edits.keySet();
	    Iterator it = keys.iterator();
	    Object key;
	    JFormattedTextField element;
    	while (it.hasNext()) {
	        key = it.next();
	        element = (JFormattedTextField) edits.get(key);
			if (mol == null) element.setText("NA");
			else { 	        
				if (key.equals(AmbitCONSTANTS.CASRN)) 
					element.setText(mol.getCAS_RN());
				else if (key.equals(AmbitCONSTANTS.SMILES)) 
					element.setText(mol.getSMILES());
				else if (key.equals(AmbitCONSTANTS.NAMES)) 
					element.setText(mol.getName());
				else if (key.equals(AmbitCONSTANTS.FORMULA)) 
					element.setText(mol.getFormula());
				else element.setText("");
	        }
		}		
		display();
	}
	public Compound getCompound() {
		if (molecule == null) molecule = new Compound(); 

		Set keys = edits.keySet();
	    Iterator it = keys.iterator();
	    Object key;
	    JFormattedTextField element;
    	while (it.hasNext()) {
	        key = it.next();
	        element = (JFormattedTextField) edits.get(key);
			if (key.equals(AmbitCONSTANTS.CASRN)) 
				molecule.setCAS_RN(element.getText());
			else if (key.equals(AmbitCONSTANTS.SMILES)) 
				molecule.setSMILES(element.getText());
			else if (key.equals(AmbitCONSTANTS.NAMES)) 
				molecule.setName(element.getText());
			else if (key.equals(AmbitCONSTANTS.FORMULA)) 
				molecule.setFormula(element.getText());
			else element.setText("");
		}		
		return molecule;
	}
    /* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
	    Object source = evt.getSource();
	    if (molecule == null) molecule = new Compound();
	    try {
	    	if (source == casEdit) {
	    		molecule.setCAS_RN(casEdit.getText());
	    	} else if (source == nameEdit) {
	    		molecule.setName(nameEdit.getText());
	    	} else if (source == formulaEdit) {
	    		molecule.setFormula(formulaEdit.getText());
	    	} else if (source == smilesEdit) {
	    		molecule.createFromSMILES(smilesEdit.getText());
	    		display();
	    	}

	    } catch (NullPointerException ex) {
	    	ex.printStackTrace();
	    }


	}	
	public boolean view(Component parent, boolean editable, String title) throws AmbitException {
		return
	    JOptionPane.showConfirmDialog(parent,this,"",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;
	}
	public JComponent getJComponent() {
	    return this;
	}
	public AmbitObject getAO() {
		return getCompound();
	}
	public void setAO(AmbitObject ao) {
		if (ao instanceof Compound) setCompound((Compound)ao);
		
	}
	/* (non-Javadoc)
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Compound) setCompound((Compound)value);
        return this;
    }
}
