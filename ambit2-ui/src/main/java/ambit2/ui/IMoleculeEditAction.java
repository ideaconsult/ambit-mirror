package ambit2.ui;

import java.awt.Component;

import javax.swing.Action;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface IMoleculeEditAction extends Action {
	public IAtomContainer getMolecule();

	public void setMolecule(IAtomContainer molecule);
	
	public void setParentComponent(Component parentComponent);

	public void setModal(boolean modal);
}
