package ambit2.reactions.rules.scores;

import java.util.Map;
import java.util.Map.Entry;

public class ChemComplexityUtils 
{
	public static double log_2 = Math.log(2);
	
	public static double shannonEntropy(Map<String,Integer> groupFrequencies)
	{
		int N = 0;
		for (Entry<String,Integer> entry : groupFrequencies.entrySet())
			N += entry.getValue(); 
		return shannonEntropy(groupFrequencies, N);
	}
	
	public static double shannonEntropy(Map<String,Integer> groupFrequencies, int totalNum)
	{
		double I = 0;
		if (totalNum == 0)
			return I;
		for (Entry<String,Integer> entry : groupFrequencies.entrySet())
		{	
			double p = entry.getValue().doubleValue() / totalNum;
			I = I - p * Math.log(p)/log_2;
		}
		return I;
	}
	
	public static double shannonEntropy(int groupFrequencies[])
	{
		int N = 0;
		for (int i = 0; i < groupFrequencies.length; i++)
			N += groupFrequencies[i];
		return shannonEntropy(groupFrequencies, N);
	}
	
	public static double shannonEntropy(int groupFrequencies[], int totalNum)
	{
		double I = 0;
		if (totalNum == 0)
			return I;
		for (int k = 0; k < groupFrequencies.length; k++)
		{	
			double p = ((double)groupFrequencies[k]) / totalNum;
			I = I - p * Math.log(p)/log_2;
		}			
		return I;
	}
}
