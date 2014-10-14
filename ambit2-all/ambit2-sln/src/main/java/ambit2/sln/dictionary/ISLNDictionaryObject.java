package ambit2.sln.dictionary;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

public interface ISLNDictionaryObject 
{
	public enum Type {
		ATOM, BOND, FRAGMENT, STRUCTURE, QUERY, REACTION
	}
	
	public Type getObjectType();
	
	public boolean match(IAtom atom);
	
	public boolean match(IBond bond);
}
