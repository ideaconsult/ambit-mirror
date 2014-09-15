package ambit2.smarts;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;


public class EquivalenceTester 
{
	public IAtomContainer target;
	public int atomClasses[];
	public int nClasses;
	
	public void setTarget(IAtomContainer t)
	{
		target = t;
		atomClasses = new int [target.getAtomCount()];
		nClasses = 0;
		for (int i = 0; i < atomClasses.length; i++)
			atomClasses[i] = 0;
	}
	
	public void quickFindEquivalentTerminalHAtoms()
	{
		for(int i = 0; i < target.getAtomCount(); i++)
		{
			IAtom at = target.getAtom(i);
			if (at.getSymbol().equals("H"))
				continue;
			
			List<IAtom> list = target.getConnectedAtomsList(at);
			List<IAtom> hlist = new ArrayList<IAtom>();
			
			for (int k = 0; k < list.size(); k++)
			{
				IAtom a = list.get(k);
				if (a.getSymbol().equals("H"))
					hlist.add(a);
			}
			
			if (!hlist.isEmpty())
			{
				nClasses++;
				for (int k = 0; k < hlist.size(); k++)
				{
					IAtom a = hlist.get(k);
					int index = target.getAtomNumber(a);
					atomClasses[index] = nClasses;
				}
			}	
		}
	}
	
	
	public boolean equivalentMaps(List<IAtom> map1, List<IAtom> map2)
	{
		if (map1.size() != map2.size())
			return false;
		
		for (int i = 0; i < map1.size(); i++)
		{
			if (map1.get(i) != map2.get(i))
				if (!equivalentAtoms(map1.get(i),map2.get(i)))
					return false;
		}
		
		return true;
	}
	
	
	public List<List<IAtom>> filterEquivalentMappings(List<List<IAtom>> maps)
	{	
		if (maps.size() == 0)
			return (maps);
		
		List<List<IAtom>> res = new ArrayList<List<IAtom>>();
		res.add(maps.get(0));
		
		for (int i = 1; i < maps.size(); i++)
		{
			boolean FlagEquivalent = false;
			for (int k = 0; k < res.size(); k++)
				if (equivalentMaps(maps.get(i), res.get(k)))
				{
					FlagEquivalent = true;
					break;
				}
				
			if (!FlagEquivalent)
				res.add(maps.get(i));
		}
		
		return (res);
	}
	public boolean equivalentAtoms(IAtom a1, IAtom a2)
	{
		int a1_ind = target.getAtomNumber(a1);
		int a2_ind = target.getAtomNumber(a2);
		
		if ((atomClasses[a1_ind] > 0) && (atomClasses[a2_ind] > 0))
			if (atomClasses[a1_ind] == atomClasses[a2_ind])
				return true;
		
		return false;
	}
	
	
}
