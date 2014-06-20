package ambit2.groupcontribution.descriptors;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;


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
}
