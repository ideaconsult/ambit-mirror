package ambit.ui.actions;

import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.interfaces.IMolecule;

/**
 * An abstract action to perform smth on a molecule
 * @author Nina Jeliazkova
 *
 */
public abstract class AbstractMoleculeAction extends AmbitAction {
	
	protected  IMolecule molecule; 
	public AbstractMoleculeAction(IMolecule molecule,Object userData, JFrame mainFrame) {
		super(userData,mainFrame,"Edit molecule");
		this.molecule = molecule;
	}

	public AbstractMoleculeAction(IMolecule molecule,Object userData, JFrame mainFrame,String arg0) {
		super(userData,mainFrame,arg0);
		this.molecule = molecule;
	}

	public AbstractMoleculeAction(IMolecule molecule,Object userData, JFrame mainFrame,String arg0, Icon arg1) {
		super(userData,mainFrame,arg0, arg1);
		this.molecule = molecule;
	}

	public IMolecule getMolecule() {
		return molecule;
	}

	public void setMolecule(IMolecule molecule) {
		this.molecule = molecule;
	}

}
