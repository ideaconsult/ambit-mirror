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
	Vector<IAtomContainer> resultTautomers = new Vector<IAtomContainer>();	
	Vector<String> errors = new Vector<String>(); 
	public FilterTautomers tautomerFilter = new FilterTautomers(this);
	
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
	
	
	public Vector<IAtomContainer> generateTautomers()
	{
		searchAllRulePositions();
		handleOverlapedInstances();
		
		resultTautomers.clear();
		if (ruleInstances.isEmpty())
		{	
			resultTautomers.add(molecule);
			return(resultTautomers);
		}	
		
		generateRuleInstanceCombinations();
		
		return(resultTautomers);
	}
	
	public Vector<IAtomContainer> generateTautomersIncrementaly()
	{
		//Another approach for generation of tautomers
		//based on incremental steps of analysis with first-depth approach
		//In each Incremental steps one rule instance is added
		//and the other rule instances are revised and accordingly 
		//appropriate rule-instance sets are supported (derived from extendedRuleInstance)
		
		resultTautomers.clear();	
		searchAllRulePositions();
		
		if (extendedRuleInstances.isEmpty())
		{	
			resultTautomers.add(molecule);
			return(resultTautomers);
		}
		
		RuleManager rman = new RuleManager(this);
		rman.firstIncrementalStep();
		rman.iterateIncrementalSteps();	
		
		resultTautomers = tautomerFilter.filter(resultTautomers);
		
		return(resultTautomers);
	}
	
	
	
	void searchAllRulePositions()
	{
		generatedRules.clear();
		extendedRuleInstances.clear();
		ruleInstances.clear();
		
		for (int i = 0; i < knowledgeBase.rules.size(); i++)
		{	
			Vector<IRuleInstance> instances = knowledgeBase.rules.get(i).applyRule(molecule); 
			extendedRuleInstances.addAll(instances);
		}	
	}
	
	void handleOverlapedInstances()
	{			
		ruleInstances.addAll(extendedRuleInstances);
		
		//RuleManager rman = new RuleManager(this);
		//rman.handleOverlappingRuleInstances();
	}
	
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
