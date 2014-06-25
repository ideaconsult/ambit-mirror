package ambit2.groupcontribution.descriptors;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class LDAtomHybridization implements ILocalDescriptor
{
	private boolean FlagUsed = true;
	
	@Override
	public int calcForAtom(IAtom atom, IAtomContainer mol) {
		// TODO Auto-generated method stub
		return parseHybridization(atom.getHybridization().toString());
	}

	@Override
	public String getDesignation(int value) {
		// TODO Auto-generated method stub
		return "Hyb" + value;
	}

	@Override
	public String getShortName() {
		// TODO Auto-generated method stub
		return "Hyb";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "AtomHybridization";
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return "AtomHybridization";
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return Type.PREDEFINED;
	}
	
	public int parseHybridization(String s){
		int hybridizationType = 0;
		//System.out.println(s);
		if(s.equalsIgnoreCase("SP3")){
			//System.out.println("I am here!");	
			hybridizationType = 3;
		}
		else if(s.equalsIgnoreCase("SP2")){
			hybridizationType = 2;
		}
		else if(s.equalsIgnoreCase("SP1")){
			hybridizationType = 1;
		}
		else{
			hybridizationType = -1;
		}
		return hybridizationType;
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
