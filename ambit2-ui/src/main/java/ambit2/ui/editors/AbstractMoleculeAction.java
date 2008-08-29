package ambit2.ui.editors;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.openscience.cdk.interfaces.IMolecule;

/**
 * An abstract action to perform smth on a molecule
 * @author Nina Jeliazkova
 *
 */
public abstract class AbstractMoleculeAction extends AbstractAction {
	
	protected  IMolecule molecule; 
	public AbstractMoleculeAction(IMolecule molecule) {
		super("Edit molecule");
		this.molecule = molecule;
	}

	public AbstractMoleculeAction(IMolecule molecule,String arg0) {
		super(arg0);
		this.molecule = molecule;
	}

	public AbstractMoleculeAction(IMolecule molecule,String arg0, Icon arg1) {
		super(arg0,arg1);
		this.molecule = molecule;
	}

	public IMolecule getMolecule() {
		return molecule;
	}

	public void setMolecule(IMolecule molecule) {
		this.molecule = molecule;
	}

}
