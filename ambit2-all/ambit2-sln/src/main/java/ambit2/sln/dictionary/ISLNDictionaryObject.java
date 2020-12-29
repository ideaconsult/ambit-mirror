package ambit2.sln.dictionary;


import ambit2.sln.SLNContainer;

public interface ISLNDictionaryObject 
{
	public enum Type {
		ATOM, BOND, FRAGMENT, STRUCTURE, QUERY, REACTION
	}
	
	public Type getObjectType();
	public String getSLNString();
	public SLNContainer generateSLNContainer();
	
	//public boolean match(IAtom atom);	
	//public boolean match(IBond bond);
}
