package ambit2.sln.dictionary;

import java.util.ArrayList;
import java.util.List;

import ambit2.sln.SLNHelper;



public class MarkushAtomDictionaryObject extends GenericDictionaryObject 
{
	
	public List<MarkushExpressionToken>  tokens = null; //When tokens = null this is a simple Markush atom
	public List<ISLNDictionaryObject>  macroAtoms = new ArrayList<ISLNDictionaryObject>();
	
	public MarkushAtomDictionaryObject(String name, String sln) {
		this.name = name;
		this.sln = sln;
	}
	
	public MarkushAtomDictionaryObject(String name, String sln, List<ISLNDictionaryObject>  macroAtoms) {
		this.name = name;
		this.sln = sln;
		this.macroAtoms = macroAtoms;
	}
	
	
	@Override
	public Type getObjectType() {
		return Type.MARKUSH_ATOM;
	}
	
	public String getContentString (SLNHelper slnHelper)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i <  macroAtoms.size(); i++)
		{
			//This is an ATOM or MACTRO_ATOM object
			ISLNDictionaryObject obj = macroAtoms.get(i);
			if (i > 0)
				sb.append("|");
			sb.append(slnHelper.toSLN(obj.getSLNContainer()));
		}
		return sb.toString();
	}
	
	
}
