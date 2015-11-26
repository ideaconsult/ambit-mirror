package ambit2.rules.actions;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface IAtomContainerAction extends IAction
{
	public IAtomContainer getResultAtomContainer();
}
