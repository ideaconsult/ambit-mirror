package ambit2.sln.dictionary;

import java.util.List;

public class MarkushHelper 
{
	public static List<Integer> getMarkushAtomDelimiterPositions(String dictObjectString)
	{
		//Simple recognition algorithm: searching  symbol '|' outside atom expression:
		//e.g. [...] ... | ... [...]
		
		boolean withinAtomExpression = false;
		int numOpenBrackets = 0;
		int pos = 0;
		int n = dictObjectString.length();
		while (pos < n )
		{
			//TODO
			pos++;
		}
		
		return null;
	}
}
