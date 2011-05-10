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
	public Vector<Vector<Integer>> warnFilters = new Vector<Vector<Integer>>();  // Vector< "<FilterNumber> <Number_of_positions> <Pos1> <Pos2> ..."  x n >
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
		
		//Remove duplications
		Vector<IAtomContainer> uniqueTautomers = new Vector<IAtomContainer>();
		Vector<String> tCodes = new Vector<String> ();
		
		for (int i = 0; i < tautomers.size(); i++)
		{
			String tcode = TautomerManager.getTautomerCodeString(tautomers.get(i));
			
			boolean FlagDuplication = false;
			for (int k = 0; k < tCodes.size(); k++)
			{
				if (tcode.equals(tCodes.get(k)))
				{
					FlagDuplication = true;
					break;
				}
			}
			
			if (!FlagDuplication)
			{
				tCodes.add(tcode);
				uniqueTautomers.add(tautomers.get(i));
			}
		}
		
		
		//Filtration according to the filter rules
		for (int i = 0; i < uniqueTautomers.size(); i++)
		{			
			Vector<Integer> vWarnF = getWarnFilters(uniqueTautomers.get(i));
			Vector<Integer> vExcludF = getExcludeFilters(uniqueTautomers.get(i));
			
			if (vExcludF != null)
			{
				removedTautomers.add(uniqueTautomers.get(i));
				warnFilters.add(vWarnF);
				excludeFilters.add(vWarnF);
				continue;
			}
			
			if ((vWarnF != null))
			{
				if (FlagExcludeWarningTautomers)
				{
					removedTautomers.add(uniqueTautomers.get(i));
					warnFilters.add(vWarnF);
					excludeFilters.add(vWarnF);
					continue;
				}
			}
			
			filteredTautomers.add(uniqueTautomers.get(i));
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
					flags.mNeedParentMoleculeData, tautomer);
			
			isoTester.setQuery(f.fragmentQuery);			
			Vector<Integer> pos =  isoTester.getIsomorphismPositions(tautomer);
			Vector<Integer> orgPos = warnFiltersOriginalPos.get(i);
			Vector<Integer> notOriginalPos = new Vector<Integer>();
			
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
					notOriginalPos.add(IOk);
			}
			
			//Format:  <FilterNumber> <Number_of_positions> <Pos1> <Pos2> ...
			if (!notOriginalPos.isEmpty())
			{
				v.add(new Integer(i));
				v.add(new Integer(notOriginalPos.size()));
				for (int k = 0; k < notOriginalPos.size(); k++)
					v.add(notOriginalPos.get(k));
			}
		}
		
		if (v.isEmpty())
			return null;
		else
			return(v);
	}
	
	
	public Vector<Integer> getExcludeFilters(IAtomContainer tautomer)
	{	
		
		Vector<Integer> v = new Vector<Integer>(); 
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
					flags.mNeedParentMoleculeData, tautomer);
			
			isoTester.setQuery(f.fragmentQuery);			
			Vector<Integer> pos =  isoTester.getIsomorphismPositions(tautomer);
			Vector<Integer> orgPos = excludeFiltersOriginalPos.get(i);
			Vector<Integer> notOriginalPos = new Vector<Integer>();
			
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
					notOriginalPos.add(IOk);
			}
			
			//Format:  <FilterNumber> <Number_of_positions> <Pos1> <Pos2> ...
			if (!notOriginalPos.isEmpty())
			{
				v.add(new Integer(i));
				v.add(new Integer(notOriginalPos.size()));
				for (int k = 0; k < notOriginalPos.size(); k++)
					v.add(notOriginalPos.get(k));
			}
		}
		
		if (v.isEmpty())
			return null;
		else
			return(v);
	}
	
	
	
}
