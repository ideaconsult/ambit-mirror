package ambit2.groupcontribution.descriptors;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

//import ambit2.groupcontribution.descriptors.ILocalDescriptor.Type;

public class LDAtomFormalCharge implements ILocalDescriptor {

	@Override
	public int calcForAtom(IAtom atom, IAtomContainer mol) {
		// TODO Auto-generated method stub
		return atom.getFormalCharge();
	}

	@Override
	public String getDesignation(int value) {
		// TODO Auto-generated method stub
		return "FC" + value;
	}

	@Override
	public String getShortName() {
		// TODO Auto-generated method stub
		return "FC";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "FormalCharge";
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "Formal Charge";
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return Type.PREDEFINED;
	}

}
