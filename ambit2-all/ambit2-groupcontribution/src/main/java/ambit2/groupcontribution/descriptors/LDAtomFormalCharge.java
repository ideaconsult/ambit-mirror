package ambit2.groupcontribution.descriptors;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class LDAtomFormalCharge implements ILocalDescriptor {

	private boolean FlagUsed = true;
	
	@Override
	public int calcForAtom(IAtom atom, IAtomContainer mol) {
		return atom.getFormalCharge();
	}

	@Override
	public String getDesignation(int value) {
		return "FC" + value;
	}

	@Override
	public String getShortName() {
		return "FC";
	}

	@Override
	public String getName() {
		return "FormalCharge";
	}

	@Override
	public String getInfo() {
		return "Formal Charge";
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
