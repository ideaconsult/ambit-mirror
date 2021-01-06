package ambit2.sln.dictionary;

import ambit2.sln.SLNAtom;
import ambit2.sln.SLNContainer;

public class AtomDictionaryObject extends GenericDictionaryObject
{
	
	public AtomDictionaryObject(String name, String sln, SLNAtom atom) {
		this.name = name;
		this.sln = sln;
		this.atom = atom;
		makeContainerFromAtom(atom);
	}
	
	public AtomDictionaryObject(String name, String sln, SLNContainer container) {
		this.name = name;
		this.sln = sln;
		this.container = container;
		this.atom = (SLNAtom)container.getAtom(0);
		
	}
	
	
	@Override
	public Type getObjectType() {
		return Type.ATOM;
	}

	
		
}
