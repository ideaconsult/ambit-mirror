package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;


public class FilterTautomers 
{	
	public boolean FlagApplyWarningFilter = true;
	public boolean FlagApplyExcludeFilter = true;
	public boolean FlagApplyDuplicationFilter = true;	
	public boolean FlagApplyDuplicationCheckIsomorphism = false;
	
	//This duplication check is not so good since equal InChI keys are given to the resonance Kekeule stuctures
	public boolean FlagApplyDuplicationCheckInChI = false;  
	
	
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
		//System.out.println("generated " + tautomers.size() + " strctures");
		removedTautomers.clear();
		Vector<IAtomContainer> filteredTautomers = new Vector<IAtomContainer>();		
		Vector<IAtomContainer> uniqueTautomers;
		
		
		//Remove duplications based on double bond positions
		if (FlagApplyDuplicationFilter)
			uniqueTautomers = dubplicationFilter(tautomers);
		else
			uniqueTautomers = tautomers;
		
		
		//Filtration according to the filter rules
		for (int i = 0; i < uniqueTautomers.size(); i++)
		{			
			//System.out.println("Tautomer #" + i + "  " + SmartsHelper.moleculeToSMILES(uniqueTautomers.get(i)));
			
			Vector<Integer> vWarnF;
			if (FlagApplyWarningFilter)
				vWarnF = getWarnFilters(uniqueTautomers.get(i));
			else
				vWarnF = null;
			
			Vector<Integer> vExcludF;
			if (FlagApplyExcludeFilter)
				vExcludF = getExcludeFilters(uniqueTautomers.get(i));
			else
				vExcludF = null;
			
			
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
		
		
		//Pre-processing
		for (int i = 0; i < filteredTautomers.size(); i++)
		{	
			try{
				//System.out.println("preprocess " + i);
				preProcessStructures(filteredTautomers.get(i));
			}
			catch(Exception e){
				System.out.println(e.toString());
			}
		}
		
		
		if (FlagApplyDuplicationCheckIsomorphism)
		{
			Vector<IAtomContainer> filteredTautomers2 = duplicationFilterBasedOnIsomorphism(filteredTautomers);
			filteredTautomers = filteredTautomers2;
		}
		
		
		//Remove duplications based on InChI representation
		//Generally this option is switched-off (false) since InChI based filtering removes
		//leaves only one resonance aromatic structure.   
		if (FlagApplyDuplicationCheckInChI)
		{	
			try{
				Vector<IAtomContainer> filteredTautomers2 = duplicationFilterBasedOnInChI(filteredTautomers);
				filteredTautomers = filteredTautomers2;
			}
			catch(Exception e)
			{	
				System.out.println(e.toString());
			}
		}
		
		
		return filteredTautomers;
	}
	
	
	Vector<IAtomContainer> dubplicationFilter(Vector<IAtomContainer> tautomers)
	{	
		Vector<IAtomContainer> uniqueTautomers0 = new Vector<IAtomContainer>();
		Vector<String> tCodes = new Vector<String> ();
		
		for (int i = 0; i < tautomers.size(); i++)
		{
			String tcode = TautomerManager.getTautomerCodeString(tautomers.get(i));
			//System.out.println("#" + i + "  tcode = " + tcode);
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
				uniqueTautomers0.add(tautomers.get(i));
			}
		}
		
		return uniqueTautomers0;
	}
	
	
	Vector<IAtomContainer> duplicationFilterBasedOnInChI(Vector<IAtomContainer> tautomers) throws Exception
	{
		InChIGeneratorFactory igf = InChIGeneratorFactory.getInstance();
		Vector<IAtomContainer> uniqueTautomers = new Vector<IAtomContainer>();
		Vector<String> tKeys = new Vector<String> ();
		
		for (int i = 0; i < tautomers.size(); i++)
		{	
			IAtomContainer clone = getCloneForInChIGeneration(tautomers.get(i)); //the clone contains eexplicit H atoms
			
			InChIGenerator ig = igf.getInChIGenerator(clone);
			String inchi = ig.getInchi();
			String inchiKey = ig.getInchiKey();
			System.out.println("#" + i + "  " + inchi + "   "+ inchiKey + "  " + SmartsHelper.moleculeToSMILES(tautomers.get(i)));
			
			
			boolean FlagDuplication = false;

			for (int k = 0; k < tKeys.size(); k++)
			{
				if (inchiKey.equals(tKeys.get(k)))
				{
					FlagDuplication = true;
					break;
				}
			}

			if (!FlagDuplication)
			{
				tKeys.add(inchiKey);
				uniqueTautomers.add(tautomers.get(i));
			}
		}
		
		return uniqueTautomers;
	}
	
	Vector<IAtomContainer> duplicationFilterBasedOnIsomorphism(Vector<IAtomContainer> tautomers)
	{
		
		Vector<IAtomContainer> filtered = new Vector<IAtomContainer> ();
		
		for (int i = 0; i < tautomers.size(); i++)
		{
			if (!checkIsomorphismEquivalence(tautomers.get(i), filtered))
					filtered.add(tautomers.get(i));
		}
		
		return (filtered);
	}
	
	boolean checkIsomorphismEquivalence(IAtomContainer target, Vector<IAtomContainer> structs)
	{
		if (structs.isEmpty())
			return (false);
		
		//TODO
		
		return false;
	}
	
	
	IAtomContainer getCloneForInChIGeneration(IAtomContainer ac) throws Exception
	{	
		IAtomContainer clone = (IAtomContainer)ac.clone();
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(clone);
		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		adder.addImplicitHydrogens(clone);
		AtomContainerManipulator.convertImplicitToExplicitHydrogens(clone);
		return(clone);
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
	
	
	void clearAromaticityFlags(IAtomContainer ac)
	{
		for (int i = 0; i < ac.getAtomCount(); i++)
		{
			ac.getAtom(i).setFlag(CDKConstants.ISAROMATIC, false);
		}
		
		for (int i = 0; i < ac.getBondCount(); i++)
		{
			ac.getBond(i).setFlag(CDKConstants.ISAROMATIC, false);
		}
		
	}
	
	void preProcessStructures(IAtomContainer ac) throws Exception
	{
		if (ac == null)
			return;
		clearAromaticityFlags(ac);

		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		adder.addImplicitHydrogens(ac);
		//AtomContainerManipulator.convertImplicitToExplicitHydrogens(ac);

		CDKHueckelAromaticityDetector.detectAromaticity(ac);		
	}
	
	
	
	
}
