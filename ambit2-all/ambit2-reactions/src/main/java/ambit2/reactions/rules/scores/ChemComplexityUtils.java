package ambit2.reactions.rules.scores;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class ChemComplexityUtils 
{
	public static double log_2 = Math.log(2);
	
	public static double log2(double x)
	{
		return Math.log(x)/log_2;
	}
	
	public static int totalGroupNum (Map<String,Integer> groupFrequencies)
	{
		int N = 0;
		for (Entry<String,Integer> entry : groupFrequencies.entrySet())
			N += entry.getValue(); 
		return N;
	}
	
	public static double shannonEntropy(Map<String,Integer> groupFrequencies)
	{
		int N = totalGroupNum(groupFrequencies);
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
			I = I - p * log2(p);
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
			I = I - p * log2(p);
		}			
		return I;
	}
	
	public static List<IAtom[]> getAtomPaths2(IAtom atom, IAtomContainer mol)
	{
		//TODO
		return null;
	}
	
	public static List<IAtom[]> getAtomPaths(IAtom atom, IAtomContainer mol, int pathLenght)
	{
		//TODO
		return null;
	}
	
	public static void registerGroup(String group, Map<String,Integer> groupFrequencies)
	{
		Integer freq = groupFrequencies.get(group);
		if (freq == null)
			groupFrequencies.put(group, 1);
		else
			groupFrequencies.put(group, (freq+1));
	}
}
