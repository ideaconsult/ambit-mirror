package ambit2.groupcontribution.descriptors;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.utils.MoleculeUtils;

public class LDAtomHeavyNeighbours implements ILocalDescriptor{

	private boolean FlagUsed = true;
	
	@Override
	public int calcForAtom(IAtom atom, IAtomContainer mol) {
		return atom.getFormalNeighbourCount() - MoleculeUtils.getHCount(atom, mol);
	}

	@Override
	public String getDesignation(int value) {
		return "HeN" + value;
	}

	@Override
	public String getShortName() {
		return "HeN";
	}

	@Override
	public String getName() {
		return "AtomHeavyNeighbours";
	}

	@Override
	public String getInfo() {
		return "Atom Heavy Neighbours";
	}

	@Override
	public Type getType() {
		return Type.PREDEFINED;
	}

	@Override
	public boolean isUsed() {
		return FlagUsed;
	}

	@Override
	public void setIsUsed(boolean used) {
		FlagUsed = used;
	}

}
