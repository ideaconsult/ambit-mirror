package ambit2.sln.dictionary;

import ambit2.sln.SLNContainer;

public class MacroAtomDictionaryObject extends GenericDictionaryObject 
{

	public MacroAtomDictionaryObject(String name, String sln, SLNContainer container) {
		this.name = name;
		this.sln = sln;
		this.container = container;
		this.valences = container.getAttributes().valences;
	}
	
	@Override
	public Type getObjectType() {
		return Type.MACRO_ATOM;
	}

}
