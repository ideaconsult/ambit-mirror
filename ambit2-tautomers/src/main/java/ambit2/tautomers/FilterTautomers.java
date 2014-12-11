package ambit2.tautomers;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;

import net.sf.jniinchi.INCHI_OPTION;


public class FilterTautomers 
{	
	public boolean FlagApplyWarningFilter = true;
	public boolean FlagApplyExcludeFilter = true;
	public boolean FlagApplyDuplicationFilter = true;	
	public boolean FlagTreatAromaticBondsAsEquivalentFilter = false;
	public boolean FlagApplyDuplicationCheckIsomorphism = true;
	public boolean FlagApplySimpleAromaticityRankCorrection = true;
	
	//This duplication check is suitable for any case 
	//since equal InChI keys are given to the resonance Kekeule stuctures
	//By default this check is switched off
	public boolean FlagApplyDuplicationCheckInChI = false;
	public boolean FlagINCHI_OPTION_FixedH = true;
	public boolean FlagFilterIncorrectValencySumStructures = true;
	
	
	public TautomerManager tman;
	public List<IAtomContainer> removedTautomers = new ArrayList<IAtomContainer>();
	public List<List<Integer>> warnFilters = new ArrayList<List<Integer>>();  // List< "<FilterNumber> <Number_of_positions> <Pos1> <Pos2> ..."  x n >
	public List<List<Integer>> excludeFilters = new ArrayList<List<Integer>>();
	public List<List<Integer>> warnFiltersOriginalPos = new ArrayList<List<Integer>>();
	public List<List<Integer>> excludeFiltersOriginalPos = new ArrayList<List<Integer>>();
	
	IsomorphismTester isoTester = new IsomorphismTester();
	
	public boolean FlagExcludeWarningTautomers = true;
	
	
	public FilterTautomers(TautomerManager m)
	{
		tman = m;
	}
		
	
	public List<IAtomContainer> filter(List<IAtomContainer> tautomers) throws Exception 
	{	
		getOriginalPositions();
		//System.out.println("generated " + tautomers.size() + " strctures");
		removedTautomers.clear();
		List<IAtomContainer> filteredTautomers = new ArrayList<IAtomContainer>();		
		List<IAtomContainer> uniqueTautomers;
		
		
		//Remove duplications based on double bond positions as expressed in the tautomer string code 
		if (FlagApplyDuplicationFilter)
		{	
			if (tman.FlagCheckDuplicationOnRegistering)
				uniqueTautomers = tautomers; //duplication check is already done on registration
			else
				uniqueTautomers = dubplicationFilter(tautomers);
		}	
		else
			uniqueTautomers = tautomers;
		
		
		
		if (FlagFilterIncorrectValencySumStructures)
		{
			List<IAtomContainer> tempTautomers = filterIncorrectValencySumStructs(uniqueTautomers);
			uniqueTautomers = tempTautomers;
		}
		
		
		//Pre-processing is done here 
		for (int i = 0; i < uniqueTautomers.size(); i++)
		{	
			try{
				//System.out.println("preprocess " + i);
				preProcessStructures(uniqueTautomers.get(i));
			}
			catch(Exception e){
				throw e;
				//System.out.println(e.toString()); Please do not hide exceptions!
			}
		}
		
		
		//Filtration according to the filter rules
		for (int i = 0; i < uniqueTautomers.size(); i++)
		{			
			//System.out.println("Tautomer #" + i + "  " + SmartsHelper.moleculeToSMILES(uniqueTautomers.get(i)));
			
			List<Integer> vWarnF;
			if (FlagApplyWarningFilter)
				vWarnF = getWarnFilters(uniqueTautomers.get(i));
			else
				vWarnF = null;
			
			List<Integer> vExcludF;
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
		
		
		if (FlagTreatAromaticBondsAsEquivalentFilter)
		{
			//Filtration based on tautomer string code where aromatic bonds are managed
			//
			//This filtration should not be applied before the allene atom filters 
			//since some side effect are observed 
			
			
			//TODO
		}
		
		
		if (FlagApplyDuplicationCheckIsomorphism)
		{
			List<IAtomContainer> filteredTautomers2 = duplicationFilterBasedOnIsomorphism(filteredTautomers);
			filteredTautomers = filteredTautomers2;
		}
		
		
		//Remove duplications based on InChI representation
		//Generally this option is switched-off (false) since InChI based filtering removes
		//leaves only one resonance aromatic structure.   
		if (FlagApplyDuplicationCheckInChI)
		{	
			try{
				List<IAtomContainer> filteredTautomers2 = duplicationFilterBasedOnInChI(filteredTautomers);
				filteredTautomers = filteredTautomers2;
			}
			catch(Exception e)
			{	
				throw e;
				//System.out.println(e.toString());
			}
		}		
		
		return filteredTautomers;
	}
	
	
	List<IAtomContainer> dubplicationFilter(List<IAtomContainer> tautomers)
	{	
		List<IAtomContainer> uniqueTautomers0 = new ArrayList<IAtomContainer>();
		List<String> tCodes = new ArrayList<String> ();
		
		for (int i = 0; i < tautomers.size(); i++)
		{
			String tcode = TautomerManager.getTautomerCodeString(tautomers.get(i), false);
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
	
	List<IAtomContainer> filterIncorrectValencySumStructs(List<IAtomContainer> tautomers)
	{	
		int vsum = getValencySum(tman.originalMolecule);
		List<IAtomContainer> filtrated = new ArrayList<IAtomContainer>();
		
		for (int i = 0; i < tautomers.size(); i++)
		{
			int v = getValencySum(tautomers.get(i));
			if (v == vsum)
				filtrated.add(tautomers.get(i));
		}
		
		return (filtrated);
	}	
	
	
	List<IAtomContainer> duplicationFilterBasedOnInChI(List<IAtomContainer> tautomers) throws Exception
	{
		InChIGeneratorFactory igf = InChIGeneratorFactory.getInstance();
		List<IAtomContainer> uniqueTautomers = new ArrayList<IAtomContainer>();
		List<String> tKeys = new ArrayList<String> ();
		
		List<INCHI_OPTION> options = new ArrayList<INCHI_OPTION>();
		
		if (FlagINCHI_OPTION_FixedH)
			options.add(INCHI_OPTION.FixedH);
		options.add(INCHI_OPTION.SAbs);
		options.add(INCHI_OPTION.SAsXYZ);
		options.add(INCHI_OPTION.SPXYZ);
		options.add(INCHI_OPTION.FixSp3Bug);
		
		for (int i = 0; i < tautomers.size(); i++)
		{	
			IAtomContainer clone = getCloneForInChIGeneration(tautomers.get(i)); //the clone contains explicit H atoms
			
			InChIGenerator ig = igf.getInChIGenerator(clone, options);
			String inchi = ig.getInchi();
			String inchiKey = ig.getInchiKey();
			//System.out.println("#" + i + "  " + inchi + "   "+ inchiKey + "  " + SmartsHelper.moleculeToSMILES(tautomers.get(i),false));
			
			
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
	
	List<IAtomContainer> duplicationFilterBasedOnIsomorphism(List<IAtomContainer> tautomers) throws Exception
	{
		
		List<IAtomContainer> filtered = new ArrayList<IAtomContainer> ();
		
		//preparing each tautomer molecule for sss seaching
		for (int i = 0; i < tautomers.size(); i++)
		{
			SmartsParser.prepareTargetForSMARTSSearch(false, false, false, 
				false, false, false, tautomers.get(i));
		}
		
		
		for (int i = 0; i < tautomers.size(); i++)
		{
			if (!checkIsomorphismEquivalence(tautomers.get(i), filtered))
					filtered.add(tautomers.get(i));
		}
		
		return (filtered);
	}
	
	boolean checkIsomorphismEquivalence(IAtomContainer target, List<IAtomContainer> structs)
	{
		if (structs.isEmpty())
			return (false);
		
		IQueryAtomContainer query = SmartsHelper.getQueryAtomContainer(target, false);
		isoTester.setQuery(query);
		
		for (int i = 0; i < structs.size(); i++)
		{
			if (isoTester.hasIsomorphism(structs.get(i)))
				return (true);
		}
		
		return false;
	}
	
	
	IAtomContainer getCloneForInChIGeneration(IAtomContainer ac) throws Exception
	{	
		IAtomContainer clone = (IAtomContainer)ac.clone();
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(clone);
		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		adder.addImplicitHydrogens(clone);
		HydrogenAdderProcessor.convertImplicitToExplicitHydrogens(clone);
		return(clone);
	}
	
	public void getOriginalPositions() throws Exception
	{
		warnFiltersOriginalPos.clear();
		
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
		
		
		excludeFiltersOriginalPos.clear();
		
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
	
	public List<Integer> getWarnFilters(IAtomContainer tautomer) throws Exception
	{	
		List<Integer> v = new ArrayList<Integer>(); 
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
			List<Integer> pos =  isoTester.getIsomorphismPositions(tautomer);
			List<Integer> orgPos = warnFiltersOriginalPos.get(i);
			List<Integer> notOriginalPos = new ArrayList<Integer>();
			
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
	
	
	public List<Integer> getExcludeFilters(IAtomContainer tautomer) throws Exception
	{	
		//System.out.print("tautomer target: " +  SmartsHelper.moleculeToSMILES(tautomer));
		
		List<Integer> v = new ArrayList<Integer>(); 
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
			List<Integer> pos =  isoTester.getIsomorphismPositions(tautomer);
			
			List<Integer> orgPos = excludeFiltersOriginalPos.get(i);
			List<Integer> notOriginalPos = new ArrayList<Integer>();
			//System.out.println("filter: " + f.fragmentSmarts + "  " + pos.size());			
					
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
		//AtomContainerManipulator.clearAtomConfigurations(ac);
		for (IAtom atom : ac.atoms()) {
            atom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);

        }
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		adder.addImplicitHydrogens(ac);
		//AtomContainerManipulator.convertImplicitToExplicitHydrogens(ac);

		CDKHueckelAromaticityDetector.detectAromaticity(ac);
				
		if (FlagApplySimpleAromaticityRankCorrection)
		{	
			//Fix rank according to the aromaticity info
			double aromRank = RuleManager.getAdditionalAromaticityRank(ac);		
			if (aromRank != 0.0)
			{
				Double rank = (Double)ac.getProperty("TAUTOMER_RANK");
				if (rank == null)
					return;
				double newRank = rank.doubleValue();
				newRank += aromRank;
				ac.setProperty("TAUTOMER_RANK", new Double(newRank));
			}
		}
		
	}
	
	public static int getValencySum(IAtomContainer ac)
	{
		int sum = 0;
		for (int i = 0; i < ac.getBondCount(); i++)
			sum += SmartsHelper.bondOrderToIntValue(ac.getBond(i));
		
		return sum;
	}
	
	
	
	
	
}
