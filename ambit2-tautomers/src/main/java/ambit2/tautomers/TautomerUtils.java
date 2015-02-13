package ambit2.tautomers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.smarts.SmartsHelper;

public class TautomerUtils 
{
	private final static Logger logger = Logger.getLogger(TautomerUtils.class.getName());
	
	public static class TautomerPair
	{
		public IAtomContainer mol0 = null;
		public IAtomContainer mol1 = null;
		public int riAtomIndices[] = null;
		public int ruleKBIndex = 0;
	}
	
	
	public static double getFastTautomerCountEstimation(TautomerManager tman, IAtomContainer mol) throws Exception
	{	
		tman.setStructure(mol); 
		tman.searchAllRulePositions();
		return Math.pow(2, tman.extendedRuleInstances.size());
	}
	
	public static double getTautomerCountEstimation(TautomerManager tman, IAtomContainer mol) throws Exception
	{	
		throw (new Exception("Not implemented yet"));
		
		/*
		tman.setStructure(mol); 
		tman.searchAllRulePositions();
		//TODO
		return Math.pow(2, tman.extendedRuleInstances.size());
		*/
	}
	
	public static ArrayList<TautomerPair> generatePairForEachRuleInstance(TautomerManager tman, IAtomContainer mol) throws Exception
	{	
		tman.setStructure(mol); 
		tman.searchAllRulePositions();
		
		ArrayList<TautomerPair> tpairs = new ArrayList<TautomerPair>();
		
		for (int i = 0; i < tman.extendedRuleInstances0.size(); i++)
		{	
			TautomerPair tp = new TautomerPair();
			IRuleInstance ri = tman.extendedRuleInstances0.get(i);
			tp.ruleKBIndex = tman.getKnowledgeBase().rules.indexOf(ri.getRule());
			
			if (ri.getCurrentState() == 0)
			{	
				try{
					tp.mol0 = (IAtomContainer)tman.molecule.clone();
				}catch(Exception x){};
			}	
			else
			{	
				try{
					tp.mol1 = (IAtomContainer)tman.molecule.clone();
				} catch(Exception x){};
			}	
			
			List<IAtom> atoms = ri.getAtoms();
			if (atoms != null)
			{
				tp.riAtomIndices = new int[atoms.size()];
					for (int k = 0; k < atoms.size(); k++)
						tp.riAtomIndices[k] = tman.molecule.getAtomNumber(atoms.get(k));
			}	
			
			
			//logger.info("RI#" + (i+1) + "  Rule DB index " + tp.ruleIndex + "  " + ((RuleInstance)ri).debugInfo(tman.molecule) 	+ "    state " + ri.getCurrentState() + "   " + SmartsHelper.moleculeToSMILES(tman.molecule, false));
			
			ri.nextState();
			
			if (ri.getCurrentState() == 0)
			{	
				try{
					tp.mol0 = (IAtomContainer)tman.molecule.clone();
				}catch(Exception x){};
			}	
			else
			{	
				try{
					tp.mol1 = (IAtomContainer)tman.molecule.clone();
				} catch(Exception x){};
			}	
			
			if (atoms != null)
			{
				tp.riAtomIndices = new int[atoms.size()];
					for (int k = 0; k < atoms.size(); k++)
						tp.riAtomIndices[k] = tman.molecule.getAtomNumber(atoms.get(k));
			}	
			
			ri.nextState();  //retunrs to the original state

			tpairs.add(tp);	
		}
		
		
		return tpairs;
	}
	
}
