package ambit2.sln.dictionary;

import ambit2.sln.SLNParser;

public class PredefinedSLNDictionary 
{
	public static SLNDictionary getDictionary(SLNParser parser)
	{
		SLNDictionary dict = new SLNDictionary();
		setPredefinedAtoms(dict);
		return dict;
	}
	
	static void setPredefinedAtoms(SLNDictionary dict)
	{
		//TODO
	}
}
