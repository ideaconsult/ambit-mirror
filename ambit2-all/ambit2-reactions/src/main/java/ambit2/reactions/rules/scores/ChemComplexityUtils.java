package ambit2.reactions.rules.scores;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.smarts.TopLayer;

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
	
	public static List<IAtom[]> getAtomPaths1(IAtom atom, IAtomContainer mol, boolean includeImplicitHAtoms)
	{
		//TODO
		return null;
	}
	
	public static List<IAtom[]> getAtomPaths2(IAtom atom, IAtomContainer mol, boolean includeImplicitHAtoms)
	{
		List<IAtom[]> paths = new ArrayList<IAtom[]>();
		List<IAtom> neighAtoms = ((TopLayer)atom.getProperty(TopLayer.TLProp)).atoms;
		
		//Separate paths of the type "atom~H" (length=1) for each implicit H
		if (includeImplicitHAtoms)
		{	
			int atom_hci = getImplicitHCount (atom);
			for (int i = 0; i < atom_hci; i++)
			{
				//Register path: atom~iplicitH
				IAtom path[] = new IAtom[2];
				path[0] = atom;
				path[1] = null;
				paths.add(path);
			}
		}
		
		for (IAtom at1: neighAtoms)
		{
			if (at1.getSymbol().equals("H"))
			{	
				//Explicit H atom is the termination atom of a path of length 1
				//Register path: atom~at1
				IAtom path[] = new IAtom[2];
				path[0] = atom;
				path[1] = at1;
				paths.add(path);
				continue; 
			}	
			
			List<IAtom> neighAt1 = ((TopLayer)at1.getProperty(TopLayer.TLProp)).atoms;
			int at1_hci = getImplicitHCount (at1);
			
			if (neighAt1.size() == 1)
			{
				//only one neighbor of at1 and this is the input atom
				if (!includeImplicitHAtoms || at1_hci == 0)
				{
					//Register path: atom~at1
					IAtom path[] = new IAtom[2];
					path[0] = atom;
					path[1] = at1;
					paths.add(path);
				}
			}
			else
				for (IAtom at2: neighAt1)
				{
					if (at2 == atom)
						continue;
					//Register path: atom~at1~at2
					IAtom path[] = new IAtom[3];
					path[0] = atom;
					path[1] = at1;
					path[2] = at2;
					paths.add(path);
				}
			
			if (includeImplicitHAtoms)
			{	
				for (int i = 0; i < at1_hci; i++)
				{	
					IAtom path[] = new IAtom[3];
					path[0] = atom;
					path[1] = at1;
					path[2] = null;
					paths.add(path);
				}
			}
		}
		return paths;
	}
	
	static int getImplicitHCount (IAtom atom)
	{
		Integer hci = atom.getImplicitHydrogenCount();
		if (hci != null)	
			return hci;
		return 0;
	}
	
	public static List<IAtom[]> getAtomPaths(IAtom atom, IAtomContainer mol, 
				int pathLenght, boolean includeImplicitHAtoms)
	{
		switch (pathLenght)
		{
		case 0:
			//TODO
			break;
		case 1:
			return getAtomPaths1(atom, mol, includeImplicitHAtoms);
		case 2:
			return getAtomPaths2(atom, mol, includeImplicitHAtoms);
		default:
			//pathLenght >= 3
			//TODO
			break;
		}
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
	
	/**
	 * 
	 * @param atom
	 * @return array int[2] with smarts X and D primitives values for the atom
	 */
	public static int[] getAtomXDInfo(IAtom atom)
	{
		int xd[] = new int[2];
		xd[0] = 0;
		xd[1] = 0;
		List<IAtom> neighAtoms = ((TopLayer)atom.getProperty(TopLayer.TLProp)).atoms;
		for (IAtom a: neighAtoms)
		{
			xd[0]++;
			if (!a.getSymbol().equals("H"))
				xd[1]++;
		}
		Integer hci = atom.getImplicitHydrogenCount();
		if (hci != null)
			xd[0] += hci.intValue();
		return xd;
	}
}
