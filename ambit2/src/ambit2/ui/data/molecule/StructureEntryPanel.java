/*
 * Created on 2005-9-3
 *
 */
package ambit2.ui.data.molecule;

import javax.swing.JPanel;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.data.molecule.DataContainer;


/**
 * This is an abstract class to be used as structure entry (by SMILES and other means)
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-3
 */
public abstract class StructureEntryPanel extends JPanel {
	protected DataContainer dataContainer = null;
	
	/**
	 * 
	 */
	public StructureEntryPanel() {
		super();
	}


	/**
	 * @return Returns the atomContainer.
	 */
	public synchronized IAtomContainer getMolecule() {
		return dataContainer.getMolecule();
	}
	/**
	 * @param atomContainer The atomContainer to set.
	 */
	
	public synchronized void setMolecule(IMolecule atomContainer) {
    	//if (!dataContainer.loadedFromFile()) 
    		dataContainer.setMolecule(atomContainer);
    }		
	
	/**
	 * @return Returns the toxData.
	 */
	public synchronized DataContainer getDataContainer() {
		return dataContainer;
	}
	/**
	 * @param toxData The toxData to set.
	 */
	public synchronized void setDataContainer(DataContainer toxData) {
		this.dataContainer = toxData;
	}
}
