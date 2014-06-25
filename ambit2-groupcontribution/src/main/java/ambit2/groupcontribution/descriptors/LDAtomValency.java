package ambit2.groupcontribution.descriptors;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class LDAtomValency implements ILocalDescriptor 
{

	private boolean FlagUsed = true;
	
	@Override
	public int calcForAtom(IAtom atom, IAtomContainer mol) {
		return atom.getValency();
	}

	@Override
	public String getDesignation(int value) {
		return "Val" + value;
	}

	@Override
	public String getShortName() {
		return "Val";
	}

	@Override
	public String getName() {
		return "Valency";
	}

	@Override
	public String getInfo() {
		return "Valency";
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
