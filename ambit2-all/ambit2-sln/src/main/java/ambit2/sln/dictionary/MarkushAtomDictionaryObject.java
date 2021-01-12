package ambit2.sln.dictionary;

import java.util.ArrayList;
import java.util.List;



public class MarkushAtomDictionaryObject extends GenericDictionaryObject 
{
	
	public List<MarkushExpressionToken>  tokens = null; //When tokens = null this is a simple Markush atom
	public List<MacroAtomDictionaryObject>  macroAtoms = new ArrayList<MacroAtomDictionaryObject>();
	
	public MarkushAtomDictionaryObject(String name, String sln) {
		this.name = name;
		this.sln = sln;
	}
	
	public MarkushAtomDictionaryObject(String name, String sln, List<MacroAtomDictionaryObject>  macroAtoms) {
		this.name = name;
		this.sln = sln;
		this.macroAtoms = macroAtoms;
	}
	
	
	@Override
	public Type getObjectType() {
		return Type.MARKUSH_ATOM;
	}
	
}
