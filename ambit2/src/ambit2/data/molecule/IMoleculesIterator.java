package ambit2.data.molecule;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Bidirectional iterator for a set of molecules.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public interface IMoleculesIterator extends IAtomContainersList {
	
	public Object setSelectedIndex(int index);
	public int getSelectedIndex();
	public void setAtomContainers(IAtomContainer[] molecules);
	//public IMoleculeSet getMoleculeForEdit() throws Exception;
	public IAtomContainer getAtomContainer(int index);
	public void removeAtomContainer(int index);
	public void removeAllAtomContainers();
	public void addAtomContainer(IAtomContainer a);
	public int getAtomContainerCount();
}
