package ambit2.jchempaint.editor;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * An abstract action to perform smth on a molecule
 * @author Nina Jeliazkova
 *
 */
public abstract class AbstractMoleculeAction extends AbstractAction {
	
	protected  IAtomContainer molecule; 
	public AbstractMoleculeAction(IAtomContainer molecule) {
		super("Edit molecule");
		this.molecule = molecule;
	}

	public AbstractMoleculeAction(IAtomContainer molecule,String arg0) {
		super(arg0);
		this.molecule = molecule;
	}

	public AbstractMoleculeAction(IAtomContainer molecule,String arg0, Icon arg1) {
		super(arg0,arg1);
		this.molecule = molecule;
	}

	public IAtomContainer getMolecule() {
		return molecule;
	}

	public void setMolecule(IAtomContainer molecule) {
		this.molecule = molecule;
	}

}
