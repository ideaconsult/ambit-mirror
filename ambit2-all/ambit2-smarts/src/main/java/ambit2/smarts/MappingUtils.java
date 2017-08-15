package ambit2.smarts;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;


public class MappingUtils 
{
	public static List<List<IAtom>> getNonIdenticalMappings(List<List<IAtom>> allMaps)
	{
		List<List<IAtom>> result = new ArrayList<List<IAtom>>();
		if (allMaps.isEmpty())
			return(result);
		
		int nMaps = allMaps.size();
		int nAtoms = allMaps.get(0).size();
		List<IAtom> map;
		List<IAtom> map1;
		boolean FlagOK;
		boolean FlagOneFifferent;
		
		for (int i = 0; i < nMaps; i++)
		{
			map = allMaps.get(i);
			FlagOK = true;
			for (int j = 0; j < result.size(); j++)
			{
				//comparing map against j-th element of result 
				map1 = result.get(j);
				FlagOneFifferent = false;
				for (int k = 0; k < nAtoms; k++)
				{
					//map1 MUST NOT conatin at least one atom from map
					if (!map1.contains(map.get(k)))
					{
						FlagOneFifferent = true;
						break;
					}
				}
				
				if (!FlagOneFifferent)
				{	
					FlagOK = false;
					break;
				}
			}
			
			if (FlagOK)
				result.add(map);
		}
		
		return(result);
	}
	
	public static List<List<IAtom>> getNonOverlappingMappings(List<List<IAtom>> allMaps)
	{
		List<List<IAtom>> result = new ArrayList<List<IAtom>>();
		
		if (allMaps.isEmpty())
			return(result);
		
		int nMaps = allMaps.size();
		int nAtoms = allMaps.get(0).size();
		List<IAtom> map;
		List<IAtom> map1;
		boolean FlagOK;
		
		for (int i = 0; i < nMaps; i++)
		{
			map = allMaps.get(i);
			FlagOK = true;
			for (int j = 0; j < result.size(); j++)
			{
				//comparing map against j-th element of result 
				map1 = result.get(j);				
				for (int k = 0; k < nAtoms; k++)
				{
					//map1 must not have any intersection
					if (map1.contains(map.get(k)))
					{
						FlagOK = false;
						break;
					}
				}
				
				if (!FlagOK)
					break;
			}
			
			if (FlagOK)
				result.add(map);
		}
		
		return(result);
	}
	
	/*
	 * returns a set of groups (arrays with indexes) of overlapped mappings
	 */
	public static List<List<Integer>> getOverlappedMappingClusters(List<List<IAtom>> maps)
	{
		List<List<Integer>>  v = new ArrayList<List<Integer>>();
		if (maps.size() == 0)
			return (v);
		
		//The first cluster is created
		List<Integer> vInt;
		vInt = new ArrayList<Integer>();
		vInt.add(new Integer(0));
		v.add(vInt);
		
		
		if (maps.size() == 1)	
			return(v);
			
		//The case with more than 1  mapping
		for (int i = 1; i < maps.size(); i++)
		{
			List<IAtom> map = maps.get(i);
			
			boolean FlagOverlap = false;
			for (int k = 0; k < v.size(); k++)
			{
				if (overlapsWithCluster(map,v.get(k), maps))
				{
					v.get(k).add(new Integer(i));
					FlagOverlap = true;
					break;
				}
			}
			
			if (!FlagOverlap)
			{
				//New cluster is created
				vInt = new ArrayList<Integer>();
				vInt.add(new Integer(i));
				v.add(vInt);
			}
		}
		
		return v;
	}
	
	static boolean overlapsWithCluster(List<IAtom> map, List<Integer> cluster, List<List<IAtom>> maps)
	{
		for (int i = 0; i < cluster.size(); i++)
		{
			int mapNum = cluster.get(i).intValue();
			List<IAtom> clMap = maps.get(mapNum);
			for (int k = 0; k < map.size(); k++)
				if (clMap.contains(map.get(k)))
					return true;
		}
		
		return false;
	}
}
