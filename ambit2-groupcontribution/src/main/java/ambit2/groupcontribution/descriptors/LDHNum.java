package ambit2.groupcontribution.descriptors;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.utils.MoleculeUtils;

public class LDHNum implements ILocalDescriptor
{	
	private boolean FlagUsed = true;
	
	@Override
	public int calcForAtom(IAtom atom, IAtomContainer mol) {
		return MoleculeUtils.getHCount(atom, mol);
	}

	@Override
	public String getDesignation(int value) {		
		return "H" + value;
	}

	@Override
	public String getShortName() {
		return "H";
	}

	@Override
	public String getName() {		
		return "HNum";
	}

	@Override
	public String getInfo() {		
		return "Number of hydrogen neighbours";
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
