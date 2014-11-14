package ambit2.groupcontribution.descriptors;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;


public interface ILocalDescriptor 
{
	enum Type {
		PREDEFINED, SMARTS
	}
	
	public int calcForAtom(IAtom atom, IAtomContainer mol);
	public String getDesignation(int value);
	public String getShortName();
	public String getName();
	public String getInfo();
	public Type getType();
	public boolean isUsed();
	public void setIsUsed(boolean used);
}
