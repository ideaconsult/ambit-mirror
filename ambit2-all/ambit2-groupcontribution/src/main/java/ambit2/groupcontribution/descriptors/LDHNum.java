package ambit2.groupcontribution.descriptors;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;


public class LDHNum implements ILocalDescriptor
{	
	private boolean FlagUsed = true;
	
	@Override
	public int calcForAtom(IAtom atom, IAtomContainer mol) {		
		return atom.getImplicitHydrogenCount();
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
