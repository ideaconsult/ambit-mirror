package ambit2.tautomers;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.Vector;
import ambit2.smarts.SmartsHelper;


public class TautomerManager 
{
	KnowledgeBase knowledgeBase; 
	IAtomContainer originalMolecule;
	IAtomContainer molecule;
	Vector<IRuleInstance> extendedRuleInstances = new Vector<IRuleInstance>(); 
	Vector<IRuleInstance> ruleInstances = new Vector<IRuleInstance>();
	Vector<Rule> generatedRules = new Vector<Rule>(); 
	Vector<IAtomContainer> resultTautomers;	
	Vector<String> errors = new Vector<String>(); 
	public FilterTautomers tautomerFilter = new FilterTautomers(this);
		
	public boolean FlagRecurseBackResultTautomers = false;
	public boolean FlagUseRingChainRules = false;
	public boolean FlagUseClorineRules = false;
	
	//Some debug info flags
	public boolean FlagPrintTargetMoleculeInfo = false;
	public boolean FlagPrintExtendedRuleInstances = false;
	public boolean FlagPrintIcrementalStepDebugInfo = false;
				
	
	public TautomerManager()
	{
		knowledgeBase = new KnowledgeBase();
		if (knowledgeBase.errors.size() > 0)
		{	
			System.out.println(knowledgeBase.getAllErrors());
		}
		
		
	}
	
	
	public void setStructure(IAtomContainer str)
	{	
		molecule = str;
		originalMolecule = str;
		try{
			molecule = (IAtomContainer)originalMolecule.clone();
		}
		catch(Exception e)
		{}
	}
	
	
	//Approach 00: this is the initial (pure combinatorial)  approach 
	public Vector<IAtomContainer> generateTautomers()
	{
		searchAllRulePositions();
		handleOverlapedInstances();
		
		resultTautomers = new Vector<IAtomContainer>();
		if (ruleInstances.isEmpty())
		{	
			resultTautomers.add(molecule);
			return(resultTautomers);
		}	
		
		generateRuleInstanceCombinations();
		
		return(resultTautomers);
	}
	
	//Approach 01 (basic one) based on first depth search algorithm
	public Vector<IAtomContainer> generateTautomersIncrementaly()
	{
		//Another approach for generation of tautomers
		//based on incremental steps of analysis with first-depth approach
		//In each Incremental steps one rule instance is added
		//and the other rule instances are revised and accordingly 
		//appropriate rule-instance sets are supported (derived from extendedRuleInstance)
		
		resultTautomers = new Vector<IAtomContainer>();	
		searchAllRulePositions();
		
		if (extendedRuleInstances.isEmpty())
		{	
			resultTautomers.add(molecule);
			return(resultTautomers);
		}
		
		RuleManager rman = new RuleManager(this);
		rman.firstIncrementalStep();
		rman.iterateIncrementalSteps();	
		
		if (FlagRecurseBackResultTautomers)
		{
			//Apply special filter before recursing back the result tautomers
			Vector<IAtomContainer> filtered = preRecursionFiltration(resultTautomers);			
			Vector<IAtomContainer> res = generateTautomersFromMultipleTargets(filtered);
			resultTautomers = res;
		}
		else
			resultTautomers = tautomerFilter.filter(resultTautomers);
		
		return(resultTautomers);
	}
	
	
	Vector<IAtomContainer> preRecursionFiltration(Vector<IAtomContainer> tautomers)
	{
		//Save original filtration flags
		boolean F_ApplyDuplicationCheckInChI = tautomerFilter.FlagApplyDuplicationCheckInChI;
		boolean F_ApplyDuplicationCheckIsomorphism = tautomerFilter.FlagApplyDuplicationCheckIsomorphism;
		boolean F_ApplyDuplicationFilter = tautomerFilter.FlagApplyDuplicationFilter;
		boolean F_ApplyExcludeFilter = tautomerFilter.FlagApplyExcludeFilter;
		boolean F_ApplyWarningFilter = tautomerFilter.FlagApplyWarningFilter;
		boolean F_ExcludeWarningTautomers = tautomerFilter.FlagExcludeWarningTautomers;
		boolean F_FilterIncorrectValencySumStructures = tautomerFilter.FlagFilterIncorrectValencySumStructures;
		
		//Setting the flags for filtration
		tautomerFilter.FlagApplyDuplicationCheckInChI = false;
		tautomerFilter.FlagApplyDuplicationCheckIsomorphism = true;
		tautomerFilter.FlagApplyDuplicationFilter = true;
		tautomerFilter.FlagApplyExcludeFilter = true;
		tautomerFilter.FlagApplyWarningFilter = true;
		tautomerFilter.FlagExcludeWarningTautomers = true;
		tautomerFilter.FlagFilterIncorrectValencySumStructures = true;
		
		//Performing filtration
		Vector<IAtomContainer> res = tautomerFilter.filter(tautomers);
		
		//Restore original filtration flags
		tautomerFilter.FlagApplyDuplicationCheckInChI = F_ApplyDuplicationCheckInChI;
		tautomerFilter.FlagApplyDuplicationCheckIsomorphism = F_ApplyDuplicationCheckIsomorphism; 
		tautomerFilter.FlagApplyDuplicationFilter = F_ApplyDuplicationFilter;
		tautomerFilter.FlagApplyExcludeFilter = F_ApplyExcludeFilter;
		tautomerFilter.FlagApplyWarningFilter = F_ApplyWarningFilter; 
		tautomerFilter.FlagExcludeWarningTautomers = F_ExcludeWarningTautomers;
		tautomerFilter.FlagFilterIncorrectValencySumStructures = F_FilterIncorrectValencySumStructures;
				
		return res;
	}
	
	Vector<IAtomContainer> generateTautomersFromMultipleTargets(Vector<IAtomContainer> targets)
	{
		IAtomContainer tmpOrgMolecule = originalMolecule;
		IAtomContainer tmpMolecule = molecule;
		
		Vector<IAtomContainer> summarizedResult = new Vector<IAtomContainer>();
		
		for (int i = 0; i < targets.size(); i++) 
		{
			setStructure(targets.get(i));
			resultTautomers = new Vector<IAtomContainer>();	
			searchAllRulePositions();
			
			if (extendedRuleInstances.isEmpty())
			{	
				summarizedResult.add(molecule);
				continue;
			}
			
			RuleManager rman = new RuleManager(this);
			rman.firstIncrementalStep();
			rman.iterateIncrementalSteps();	
			
			summarizedResult.addAll(resultTautomers);
		}
		
		//Restore the original molecule variables
		originalMolecule = tmpOrgMolecule;
		molecule = tmpMolecule; 
		
		return tautomerFilter.filter(summarizedResult);
	}
	
	
	void searchAllRulePositions()
	{
		generatedRules.clear();
		extendedRuleInstances.clear();
		ruleInstances.clear();
		
		for (int i = 0; i < knowledgeBase.rules.size(); i++)
			if (knowledgeBase.rules.get(i).isRuleActive)
			{	
				Vector<IRuleInstance> instances = knowledgeBase.rules.get(i).applyRule(molecule); 
				extendedRuleInstances.addAll(instances);
			}	
	}
	
	void activateRingChainRules(boolean FlagActivate)
	{
		FlagUseRingChainRules = FlagActivate;
		
		for (int i = 0; i < knowledgeBase.rules.size(); i++)
		{	
			Rule rule = knowledgeBase.rules.get(i);
			if (rule.type == TautomerConst.RT_RingChain)
				rule.isRuleActive = FlagActivate;
		}	
	}
	
	void activateClorineRules(boolean FlagActivate)
	{
		FlagUseClorineRules = FlagActivate;		
		for (int i = 0; i < knowledgeBase.rules.size(); i++)
		{	
			Rule rule = knowledgeBase.rules.get(i);
			if (rule.type == TautomerConst.RT_MobileGroup)
				if (rule.mobileGroup.equals("Cl"))
					rule.isRuleActive = FlagActivate;
		}	
	}
	
	
	//This function is applied for approach 00 
	void handleOverlapedInstances()
	{			
		ruleInstances.addAll(extendedRuleInstances);
		
		//RuleManager rman = new RuleManager(this);
		//rman.handleOverlappingRuleInstances();
	}
	
	//This function is applied for approach 00 
	void generateRuleInstanceCombinations()
	{
		for (int i = 0; i < ruleInstances.size(); i++)
			ruleInstances.get(i).firstState();
						
		int n;
		int instNumber; 
		
		do 	{
			registerTautomer();
			
			n = ruleInstances.get(0).nextState();
			instNumber = 0;
			
			while(n == 0)
			{
				instNumber++;
				if (instNumber == ruleInstances.size())
					break;
				n = ruleInstances.get(instNumber).nextState();
			}
		} while (instNumber < ruleInstances.size()); 
	}
	
	//This function is applied for approach 00 
	void registerTautomer()
	{	
		try{
			IAtomContainer newTautomer = (IAtomContainer)molecule.clone();
			resultTautomers.add(newTautomer);
			
			//TODO search for hidden tautomers 
			//i.e. overlapping of some rules occur only for specific states of both rules
		}
		catch(Exception e)
		{}
		
		
		System.out.print("  tautomer: " + getTautomerCombination()  
				+  "    " + SmartsHelper.moleculeToSMILES(molecule));
		
		//Print H Atoms info
		//for (int i = 0; i < molecule.getAtomCount(); i++)
		//	System.out.print(" " + molecule.getAtom(i).getImplicitHydrogenCount());
		//System.out.println();		
		//System.out.println();
		
	}
	
	
	public static String getTautomerCodeString(IAtomContainer tautomer)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("bo");
		
		//All bonds are described in canonical numbering (i < j) 
		for (int i = 0; i < tautomer.getAtomCount(); i++)
		{
			for (int j = i+1; j < tautomer.getAtomCount(); j++)
			{
				IBond bo = tautomer.getBond(tautomer.getAtom(i), tautomer.getAtom(j));
				if (bo != null)
					sb.append(bo.getOrder().ordinal());
					
			}
		}
		
		return(sb.toString());
	}
	
	
	String getTautomerCombination()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = ruleInstances.size()-1; i >= 0; i--)
			sb.append(""+ruleInstances.get(i).getCurrentState() + " ");
		return (sb.toString());
	}
	
	
	public void printDebugInfo()
	{
		if (FlagPrintTargetMoleculeInfo)
		{
			System.out.println("Debug info - Target Atom atributes:");
			String s = SmartsHelper.getAtomsAttributes(molecule);
			System.out.println(s + "\n");
			System.out.println("Debug info - Target Bond atributes:");
			String s2 = SmartsHelper.getBondAttributes(molecule);
			System.out.println(s2 + "\n");
		}
		
		
		if (FlagPrintExtendedRuleInstances)
		{	
			System.out.println("Debug info - extendedRuleInstances:");
			for (int i = 0; i < extendedRuleInstances.size(); i++)
				System.out.println(((RuleInstance)extendedRuleInstances.get(i)).debugInfo(molecule));
			if(extendedRuleInstances.isEmpty())
				System.out.println("  NONE");
		}	
			
	}
	
}
