package ambit2.sln;

import org.openscience.cdk.interfaces.IAtom;

public class SLNDictionaryObject 
{
	public enum Type {
		ATOM, BOND, FRAGMENT, STRUCTURE, QUERY, REACTION
	}
	
	private Type objectType = Type.ATOM;
	
	public boolean match(IAtom atom)
	{
		if (objectType != Type.ATOM)
			return false;
		//TODO	
		return true;
	}
	
}
