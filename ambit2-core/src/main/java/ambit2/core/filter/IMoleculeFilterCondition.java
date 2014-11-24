package ambit2.core.filter;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface IMoleculeFilterCondition 
{
	public boolean useMolecule(IAtomContainer container, int recordNum);
}
