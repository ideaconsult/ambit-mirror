package ambit2.groupcontribution.nmr.nmr_1h;

public class SpinSplitting 
{
	public int numSplitLevels = 0;
	public int splits[] = null;
	public String explanationInfo = null;
	
	public String toString() 
	{	
		String s = "multiplicity ";
		for (int i = 0; i < numSplitLevels; i++)			
			s = s + ( ((i==0)?"":"x")  + splits[i]);
			
		return s;
	}
}
