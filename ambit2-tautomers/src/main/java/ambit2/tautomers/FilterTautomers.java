package ambit2.tautomers;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;


public class FilterTautomers 
{	
	public boolean FlagApplyWarningFilter = true;
	public boolean FlagApplyExcludeFilter = true;
	public boolean FlagApplyDuplicationFilter = true;	
	public boolean FlagApplyDuplicationCheckIsomorphism = true;
	public boolean FlagApplySimpleAromaticityRankCorrection = true;
	
	//This duplication check is suitable for any case 
	//since equal InChI keys are given to the resonance Kekeule stuctures
	//By default this check is switched off
	public boolean FlagApplyDuplicationCheckInChI = false;  
	public boolean FlagFilterIncorrectValencySumStructures = true;
	
	
	public TautomerManager tman;
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
		
	
	public Vector<IAtomContainer> filter(Vector<IAtomContainer> tautomers) throws Exception 
	{	
		getOriginalPositions();
		//System.out.println("generated " + tautomers.size() + " strctures");
		removedTautomers.clear();
		Vector<IAtomContainer> filteredTautomers = new Vector<IAtomContainer>();		
		Vector<IAtomContainer> uniqueTautomers;
		
		
		//Remove duplications based on double bond positions
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
			Vector<IAtomContainer> tempTautomers = filterIncorrectValencySumStructs(uniqueTautomers);
			uniqueTautomers = tempTautomers;
		}
		
		
		//Pre-processing is n
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
				throw e;
				//System.out.println(e.toString());
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
	
	Vector<IAtomContainer> filterIncorrectValencySumStructs(Vector<IAtomContainer> tautomers)
	{	
		int vsum = getValencySum(tman.originalMolecule);
		Vector<IAtomContainer> filtrated = new Vector<IAtomContainer>();
		
		for (int i = 0; i < tautomers.size(); i++)
		{
			int v = getValencySum(tautomers.get(i));
			if (v == vsum)
				filtrated.add(tautomers.get(i));
		}
		
		return (filtrated);
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
	
	Vector<IAtomContainer> duplicationFilterBasedOnIsomorphism(Vector<IAtomContainer> tautomers) throws Exception
	{
		
		Vector<IAtomContainer> filtered = new Vector<IAtomContainer> ();
		
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
	
	boolean checkIsomorphismEquivalence(IAtomContainer target, Vector<IAtomContainer> structs)
	{
		if (structs.isEmpty())
			return (false);
		
		QueryAtomContainer query = SmartsHelper.getQueryAtomContainer(target, false);
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
		AtomContainerManipulator.convertImplicitToExplicitHydrogens(clone);
		return(clone);
	}
	
	public void getOriginalPositions() throws Exception
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
	
	public Vector<Integer> getWarnFilters(IAtomContainer tautomer) throws Exception
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
	
	
	public Vector<Integer> getExcludeFilters(IAtomContainer tautomer) throws Exception
	{	
		//System.out.print("tautomer target: " +  SmartsHelper.moleculeToSMILES(tautomer));
		
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
