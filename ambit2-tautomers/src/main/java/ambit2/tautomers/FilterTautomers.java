package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;

public class FilterTautomers 
{
	public TautomerManager tman;
	public IAtomContainer originalMolecule;
	public Vector<IAtomContainer> removedTautomers = new Vector<IAtomContainer>();
	public Vector<Vector<Integer>> warnFilters = new Vector<Vector<Integer>>();
	public Vector<Vector<Integer>> excludeFilters = new Vector<Vector<Integer>>();
	
	public boolean FlagExcludeWarningTautomers = true;
	
	
	public FilterTautomers(TautomerManager m)
	{
		tman = m;
	}
	
	
	public Vector<IAtomContainer> filter(Vector<IAtomContainer> tautomers)
	{	
		removedTautomers.clear();
		Vector<IAtomContainer> filteredTautomers = new Vector<IAtomContainer>();
		
		for (int i = 0; i < tautomers.size(); i++)
		{			
			Vector<Integer> vWarnF = getWarnFilters(tautomers.get(i));
			Vector<Integer> vExcludF = getExcludeFilters(tautomers.get(i));
			
			if (vExcludF != null)
			{
				removedTautomers.add(tautomers.get(i));
				warnFilters.add(vWarnF);
				excludeFilters.add(vWarnF);
				continue;
			}
			
			if ((vWarnF != null))
			{
				if (FlagExcludeWarningTautomers)
				{
					removedTautomers.add(tautomers.get(i));
					warnFilters.add(vWarnF);
					excludeFilters.add(vWarnF);
					continue;
				}
			}
			
			filteredTautomers.add(tautomers.get(i));
		}
		
		return filteredTautomers;
	}
	
	
	public Vector<Integer> getWarnFilters(IAtomContainer tautomer)
	{	
		Vector<Integer> v = new Vector<Integer>(); 
		for (int i = 0; i < tman.knowledgeBase.warningFilters.size(); i++)
		{
			//TODO
		}
		
		if (v.isEmpty())
			return null;
		else
			return(v);
	}
	
	
	public Vector<Integer> getExcludeFilters(IAtomContainer tautomer)
	{	
		//TODO
		return null;
	}
	
	
	
}
