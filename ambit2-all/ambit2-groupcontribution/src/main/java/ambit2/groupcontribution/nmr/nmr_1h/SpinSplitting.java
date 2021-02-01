package ambit2.groupcontribution.nmr.nmr_1h;

public class SpinSplitting 
{
	public int numSplitLevels = 0;
	public int splits[] = null;
	public String explanationInfo = null;
	
	public String toString() 
	{	
		String s = "split:";
		for (int i = 0; i < numSplitLevels; i++)
			s = s + (" " + splits[i]);
		return s;
	}
}
