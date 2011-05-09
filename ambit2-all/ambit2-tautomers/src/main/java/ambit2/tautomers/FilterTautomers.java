package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsParser;


public class FilterTautomers 
{
	public TautomerManager tman;
	public IAtomContainer originalMolecule;
	public Vector<IAtomContainer> removedTautomers = new Vector<IAtomContainer>();
	public Vector<Vector<Integer>> warnFilters = new Vector<Vector<Integer>>();
	public Vector<Vector<Integer>> excludeFilters = new Vector<Vector<Integer>>();
	public Vector<Vector<Integer>> warnFiltersOriginalPos = new Vector<Vector<Integer>>();
	public Vector<Vector<Integer>> excludeFiltersOriginalPos = new Vector<Vector<Integer>>();
	
	IsomorphismTester isoTester = new IsomorphismTester();
	
	public boolean FlagExcludeWarningTautomers = true;
	
	
	public FilterTautomers(TautomerManager m)
	{
		tman = m;
	}
		
	
	public Vector<IAtomContainer> filter(Vector<IAtomContainer> tautomers)
	{	
		getOriginalPositions();
		
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
	
	
	public void getOriginalPositions()
	{
		for (int i = 0; i < tman.knowledgeBase.warningFilters.size(); i++)
		{
			Filter f = tman.knowledgeBase.warningFilters.get(i);
			RuleStateFlags flags = f.fragmentFlags;
			SmartsParser.prepareTargetForSMARTSSearch(
					flags.mNeedNeighbourData, 
					flags.mNeedValenceData, 
					flags.mNeedRingData, 
					flags.mNeedRingData2, 
					flags.mNeedExplicitHData , 
					flags.mNeedParentMoleculeData, tman.molecule);
			
			isoTester.setQuery(f.fragmentQuery);			
			warnFiltersOriginalPos.add(isoTester.getIsomorphismPositions(tman.molecule));
		}
		
		for (int i = 0; i < tman.knowledgeBase.excludeFilters.size(); i++)
		{
			Filter f = tman.knowledgeBase.excludeFilters.get(i);
			RuleStateFlags flags = f.fragmentFlags;
			SmartsParser.prepareTargetForSMARTSSearch(
					flags.mNeedNeighbourData, 
					flags.mNeedValenceData, 
					flags.mNeedRingData, 
					flags.mNeedRingData2, 
					flags.mNeedExplicitHData , 
					flags.mNeedParentMoleculeData, tman.molecule);
			
			isoTester.setQuery(f.fragmentQuery);			
			excludeFiltersOriginalPos.add(isoTester.getIsomorphismPositions(tman.molecule));
		}
		
	}
	
	public Vector<Integer> getWarnFilters(IAtomContainer tautomer)
	{	
		Vector<Integer> v = new Vector<Integer>(); 
		for (int i = 0; i < tman.knowledgeBase.warningFilters.size(); i++)
		{			
			Filter f = tman.knowledgeBase.warningFilters.get(i);
			RuleStateFlags flags = f.fragmentFlags;
			SmartsParser.prepareTargetForSMARTSSearch(
					flags.mNeedNeighbourData, 
					flags.mNeedValenceData, 
					flags.mNeedRingData, 
					flags.mNeedRingData2, 
					flags.mNeedExplicitHData , 
					flags.mNeedParentMoleculeData, tman.molecule);
			
			isoTester.setQuery(f.fragmentQuery);			
			Vector<Integer> pos =  isoTester.getIsomorphismPositions(tautomer);
			Vector<Integer> orgPos = warnFiltersOriginalPos.get(i);
			
			for (int k = 0; k < pos.size(); k++)
			{
				Integer IOk = pos.get(k);
				boolean FlagOrgPos = false;
				for (int j = 0; j < orgPos.size(); j++)
				{
					if (IOk.intValue() == orgPos.get(j).intValue())
					{	
						FlagOrgPos = true;
						break;
					}	
				}
				
				if (!FlagOrgPos)
					v.add(IOk);
			}
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
