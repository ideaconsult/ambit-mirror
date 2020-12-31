package ambit2.sln.dictionary;

import ambit2.sln.SLNAtom;

public class AtomDictionaryObject extends GenericDictionaryObject
{
	
	public AtomDictionaryObject(String sln, SLNAtom atom) {
		this.sln = sln;
		this.atom = atom;
		makeContainerFromAtom();
	}
	
	
	@Override
	public Type getObjectType() {
		return Type.ATOM;
	}	
		
}
