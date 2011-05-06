package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;

public class FilterTautomers 
{
	public IAtomContainer originalMolecule;
	public Vector<IAtomContainer> removedTautomers = new Vector<IAtomContainer>();
	public Vector<Vector<Integer>> warnFilters = new Vector<Vector<Integer>>();
	public Vector<Vector<Integer>> excludeFilters = new Vector<Vector<Integer>>();
	
	public boolean FlagExcludeWarningTautomers = true;
	
	
	public Vector<IAtomContainer> filter(Vector<IAtomContainer> tautomers)
	{	
		removedTautomers.clear();
		Vector<IAtomContainer> filteredTautomers = new Vector<IAtomContainer>();
		
		for (int i = 0; i < tautomers.size(); i++)
		{	
			
			boolean checkOK = check(tautomers.get(i));
			if (checkOK)
				filteredTautomers.add(tautomers.get(i));
			else
				removedTautomers.add(tautomers.get(i));
		}
		
		
		return filteredTautomers;
	}
	
	public boolean check(IAtomContainer tautomer)
	{
		//TODO
		return(true);
	}
	
	public Vector<Integer> getWarnFilters(IAtomContainer tautomer)
	{	
		//TODO
		return null;
	}
	
	
	public Vector<Integer> getExcludeFilters(IAtomContainer tautomer)
	{	
		//TODO
		return null;
	}
	
	
	
}
