package ambit2.ui;

import java.awt.Component;

import javax.swing.Action;

import org.openscience.cdk.interfaces.IMolecule;

public interface IMoleculeEditAction extends Action {
	public IMolecule getMolecule();

	public void setMolecule(IMolecule molecule);
	
	public void setParentComponent(Component parentComponent);

	public void setModal(boolean modal);
}
