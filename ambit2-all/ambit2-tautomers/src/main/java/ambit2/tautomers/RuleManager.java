package ambit2.tautomers;

import java.util.Stack;
import java.util.Vector;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;

import ambit2.smarts.SmartsHelper;


public class RuleManager 
{
	TautomerManager tman;
	Vector<IRuleInstance> extendedRuleInstances;
	Vector<IRuleInstance> ruleInstances;
	Vector<Rule> generatedRules;
	Vector<IRuleInstance> unprocessedInstances = new Vector<IRuleInstance>();
	
	
	//Vector<TautomerIncrementStep> incSteps = new Vector<TautomerIncrementStep>(); 
	Stack<TautomerIncrementStep> stackIncSteps = new Stack<TautomerIncrementStep>();
		
	
	public RuleManager(TautomerManager man)
	{
		tman = man;
		extendedRuleInstances = tman.extendedRuleInstances;
		ruleInstances = tman.ruleInstances;
		generatedRules = tman.generatedRules;
		
		
	}
	
	public int handleOverlappingRuleInstances()
	{
		unprocessedInstances.clear();
		unprocessedInstances.addAll(extendedRuleInstances);
				
		while (unprocessedInstances.size() > 1)
		{
			IRuleInstance r = unprocessedInstances.lastElement();
			unprocessedInstances.remove(unprocessedInstances.size()-1);
			
			int k = 0;
			while (k < unprocessedInstances.size())
			{
				IRuleInstance r1 = unprocessedInstances.get(k);				
				int nOverlappedAtoms = overlapInstances(r, r1);
				
				if (nOverlappedAtoms > 0)
				{
					//TODO - handle the case where one instance is entirely overlapped by the other one
					//Currently not needed hence not critical. 
					//It depends from the rule definitions added in the future.
					
					r = addRuleInstanceToCombination(r,r1);
					unprocessedInstances.remove(r1);
					k=0;
					continue;
				}
				k++;
			}
			
			//handle rule combination
			if (r instanceof CombinedRuleInstance)
			{	
				CombinedRuleInstance cri = (CombinedRuleInstance)r;
				prepareOverlappedAtomsBonds(cri);
				cri.generateCombinedRuleStates();
			}	
			
			ruleInstances.add(r);			
		}
		
		//Adding the last one 
		if (unprocessedInstances.size() == 1)
			ruleInstances.add(unprocessedInstances.get(0));
		
		return(0);
	}
	
	
	int overlapInstances(IRuleInstance r1, IRuleInstance r2)
	{
		int n = 0;
		if (r1 instanceof RuleInstance)
		{
			if (r2 instanceof RuleInstance)			
				n = getNumOfOverlappedAtoms((RuleInstance)r1, (RuleInstance)r2);			
			else
				n = getNumOfOverlappedAtoms((RuleInstance)r1, (CombinedRuleInstance)r2);
		}
		else
		{
			if (r2 instanceof RuleInstance)			
				n = getNumOfOverlappedAtoms((RuleInstance)r2, (CombinedRuleInstance)r1);			
			else
			{	
				//This case should not happen!!!
			}	
		}		
		
		return(n);
	}
	
	int getNumOfOverlappedAtoms(RuleInstance r1, RuleInstance r2)
	{
		//TODO - to check explicitH atoms if needed
		int n = 0;
		for (int i = 0; i < r1.atoms.size(); i++)
			if (r2.atoms.contains(r1.atoms.get(i)))
				n++;
		return (n);
	}
	
	int getNumOfOverlappedAtoms(RuleInstance r1, CombinedRuleInstance r2)
	{
		int n = 0;
		for (int i = 0; i < r1.atoms.size(); i++)
		{	
			for (int k = 0; k < r2.instances.size(); k++)
				if (r2.instances.get(k).atoms.contains(r1.atoms.get(i)))
					n++;
		}	
		return (n);
	}
	
	
	
	Vector<IAtom> getOverlappedAtoms(RuleInstance r1, RuleInstance r2)
	{
		Vector<IAtom> oa = new Vector<IAtom>();		
		for (int i = 0; i < r1.atoms.size(); i++)
			if (r2.atoms.contains(r1.atoms.get(i)))
				oa.add(r1.atoms.get(i));
				
		return (oa);
	}
	
	Vector<IBond> getOverlappedBonds(RuleInstance r1, RuleInstance r2)
	{
		Vector<IBond> ob = new Vector<IBond>();		
		for (int i = 0; i < r1.bonds.size(); i++)
			if (r2.bonds.contains(r1.bonds.get(i)))
				ob.add(r1.bonds.get(i));
				
		return (ob);
	}
	
	void prepareOverlappedAtomsBonds(CombinedRuleInstance cri)
	{
		int n = cri.instances.size();
		for (int i = 0; i < n; i++)
		{
			RuleInstance r = cri.instances.get(i);
			for (int j = 0; j < n; j++)
			if ( i != j)
			{
				Vector<IAtom> va = getOverlappedAtoms(r, cri.instances.get(j));
				for (int k = 0; k < va.size(); k++)
					if (!r.overlappedAtoms.contains(va.get(k))) //This check is needed since an could be part of two overlapping
						r.overlappedAtoms.add(va.get(k));
				
				Vector<IBond> vb = getOverlappedBonds(r, cri.instances.get(j));
				for (int k = 0; k < vb.size(); k++)
					if (!r.overlappedBonds.contains(vb.get(k))) 
						r.overlappedBonds.add(vb.get(k));
			}	
		}
	}
	
	
	IRuleInstance addRuleInstanceToCombination(IRuleInstance baseRule, IRuleInstance addRule)
	{
		if (baseRule instanceof RuleInstance)
		{
			CombinedRuleInstance cri = new CombinedRuleInstance();
			cri.instances.add((RuleInstance)baseRule);
			cri.instances.add((RuleInstance)addRule);
			cri.molecule = ((RuleInstance)baseRule).molecule;
			return(cri);
		}
		else
		{
			//baseRule is a CombinedRuleInstance
			CombinedRuleInstance cri = (CombinedRuleInstance)baseRule;
			cri.instances.add((RuleInstance)addRule);
			return(baseRule);
		}
	}
	
	
	
	
	
	/*
	IRuleInstance combineRuleInstances(IRuleInstance ir1, IRuleInstance ir2)
	{
		RuleInstance r1 = (RuleInstance)ir1;
		RuleInstance r2 = (RuleInstance)ir2;
		Vector<int[]> combStates = new Vector<int[]>(); 
		
		//Checking of all possible combined states in order to generate a new combined rule. 
		for (int i = 0; i < r1.getNumberOfStates(); i++)
			for (int k = 0; k < r2.getNumberOfStates(); k++)
			{
				r1.gotoState(i);
			}			
		
		// TODO   see also for the generation of combined states 
		//and eventually if needed new CombinedRule		
		return(null);
	}
	*/
	
	
	
	
	
	//------------Incremental Approach--------------------------
	
	
	
	void firstIncrementalStep()
	{
		stackIncSteps.clear();
		
		//Creation of the 0-th increment step:
		TautomerIncrementStep incStep0 = new TautomerIncrementStep(); 
		incStep0.struct = tman.molecule;
		for (int i = 0; i < extendedRuleInstances.size(); i++)
			incStep0.unUsedRuleInstances.add((RuleInstance)extendedRuleInstances.get(i));
				
		stackIncSteps.push(incStep0);
	}
	
	
	void iterateIncrementalSteps()
	{	
		//first depth search approach
		int nMax = 20;
		int n = 0;
		while (!stackIncSteps.isEmpty())
		{	
			//System.out.println("stack_size = " + stackIncSteps.size());
			TautomerIncrementStep tStep = stackIncSteps.pop();
			
			//System.out.println(tStep.debugInfo());
			//System.out.println("tStep.unusedRI  = " + tStep.unUsedRuleInstances.size());
			//System.out.print("  pop stack: " + SmartsHelper.moleculeToSMILES(tStep.struct)); 
			expandIncremenStep(tStep);
			
			n++;
			if (n > nMax)
				break;
		}
	}
	
	
	void expandIncremenStep(TautomerIncrementStep incStep)
	{		
		//Condition for reaching the bottom of the generation tree
		//e.g. at this point real tautomers are obtained
		if (incStep.unUsedRuleInstances.isEmpty())
		{
			try{
				IAtomContainer newTautomer = (IAtomContainer)incStep.struct.clone();
				tman.resultTautomers.add(newTautomer);
				//System.out.println("   new tautomer " + SmartsHelper.moleculeToSMILES(newTautomer) 
				//		+ "    " + incStep.getTautomerCombination());
			}
			catch(Exception e)
			{
				tman.errors.add("Error clonning molecule to get tatutomer!");
			}
			
			return;
		}
		
		
		//Register in the stack the descendants of incStep 
		TautomerIncrementStep newIncSteps[] = generateNextIncrementSteps(incStep);
		for (int i = 0; i < newIncSteps.length; i++)
		{
			stackIncSteps.push(newIncSteps[i]);
			//System.out.print("  push stack: " + SmartsHelper.moleculeToSMILES(newIncSteps[i].struct)); 
		}	
	}	
		
	
	TautomerIncrementStep[] generateNextIncrementSteps(TautomerIncrementStep curIncStep)
	{	
		//incStep objects fields are not preserved since it will no longer be used in the 
		//depth-first search algorithm
		RuleInstance ri = curIncStep.unUsedRuleInstances.lastElement();
		curIncStep.unUsedRuleInstances.remove(ri);
		
		//System.out.println("Used rule: " + ri.rule.OriginalRuleString);
		
		int n = ri.getNumberOfStates();
		TautomerIncrementStep incSteps[] = new TautomerIncrementStep[n]; 
		for (int i = 0; i < n; i++)
		{	
			incSteps[i] = new TautomerIncrementStep();
			incSteps[i].usedRuleInstances.addAll(curIncStep.usedRuleInstances);
			incSteps[i].unUsedRuleInstances.addAll(curIncStep.unUsedRuleInstances);
			setNewIncrementStep(curIncStep.struct,ri,i,incSteps[i]);
			
			//reviseUnusedRuleInstances(incSteps[i]) is included inside function setNewIncrementStep
		}
		
		return incSteps;
	}
	
	
	void setNewIncrementStep(IAtomContainer prevStruct, RuleInstance ri, int state, TautomerIncrementStep incStep)
	{
		//(1)generate new structure  (modified clone of prevStruct)
		//(2)Generate and store the modified RuleInstance in usedRuleInstances
		//(3)Revise Used and Unused Rule Instances which intersect with the current active RuleInstance 
	
		Molecule mol = new Molecule();
		RuleInstance newRI = new RuleInstance(ri); 
		
		//helper arrays
		IAtom newRIAtoms[] = new IAtom[ri.atoms.size()];
		IBond newRIBonds[] = new IBond[ri.bonds.size()];
		
		
		//TODO		
		//Handle explicitH !!!
		
		//Transfer/clone atoms
		for (int i = 0; i < prevStruct.getAtomCount(); i++)
		{
			IAtom a = prevStruct.getAtom(i);
			if (ri.atoms.contains(a))
			{
				IAtom a1 = cloneAtom(a);
				mol.addAtom(a1);
				int index = ri.atoms.indexOf(a);
				newRIAtoms[index] = a1;
			}
			else
				mol.addAtom(a);
		}
		
		
		//Transfer/clone bonds
		for (int i = 0; i < prevStruct.getBondCount(); i++)
		{
			IBond b = prevStruct.getBond(i);
			if (ri.bonds.contains(b))
			{
				//This bond is part of the current RuleInstance being used for generation of next incremental steps				
				IBond b1 = new Bond();
				IAtom a01[] = new IAtom[2];
				int ind0 = ri.atoms.indexOf(b.getAtom(0));
				int ind1 = ri.atoms.indexOf(b.getAtom(1));
				a01[0] = newRIAtoms[ind0];
				a01[1] = newRIAtoms[ind1];
				b1.setAtoms(a01);
				b1.setOrder(b.getOrder());
				mol.addBond(b1);
				int index = ri.bonds.indexOf(b);
				newRIBonds[index] = b1;
			}
			else
			{	
				if (ri.atoms.contains(b.getAtom(0)))
				{
					//atom 0 is part of the ri, atom 1 is not
					IBond b1 = new Bond();
					IAtom a01[] = new IAtom[2];
					int ind0 = ri.atoms.indexOf(b.getAtom(0));
					a01[0] = newRIAtoms[ind0];
					a01[1] = b.getAtom(1);
					b1.setAtoms(a01);
					b1.setOrder(b.getOrder());
					mol.addBond(b1);
				}
				else
				{
					if (ri.atoms.contains(b.getAtom(1)))
					{
						//atom 1 is part of the ri, atom 0 is not
						IBond b1 = new Bond();
						IAtom a01[] = new IAtom[2];
						int ind1 = ri.atoms.indexOf(b.getAtom(1));
						a01[0] = b.getAtom(0);
						a01[1] = newRIAtoms[ind1];
						b1.setAtoms(a01);
						b1.setOrder(b.getOrder());
						mol.addBond(b1);
					}
					else
						mol.addBond(b);
				}
					
			}	
		}
		
		for (int i = 0; i < newRIAtoms.length; i++)
			newRI.atoms.add(newRIAtoms[i]);
		for (int i = 0; i < newRIBonds.length; i++)
			newRI.bonds.add(newRIBonds[i]);
						
		//(1) and (2)
		incStep.struct = mol;
		incStep.usedRuleInstances.add(newRI);		
		//Set the rule instance state
		//System.out.println("curState = " + newRI.curState + "   -->  state = " + state);
		//System.out.print("   " + SmartsHelper.moleculeToSMILES(incStep.struct));
		newRI.gotoState(state);
		//System.out.print("   " + SmartsHelper.moleculeToSMILES(incStep.struct));
				
		 
		
		//(3) Revision of the Used and Unused Rule Instances
		//This is very important since in guarantees that the left instances are still valid and correct
		//e.g. double bonds and protons are OK
		//Also new possibilities are searched.
		
		
		//(3.1) All used instances that overlap with the newly generated instance (newRI) 
		//are updated. They must be described in the terms of the new atoms. 
		//Old atoms references are replaces with references to the new atoms (and bonds correspondingly).
		for (int i = 0; i < incStep.usedRuleInstances.size(); i++)
		{			
			RuleInstance usedRI =  incStep.usedRuleInstances.get(i);
			int nOvAt = getNumOfOverlappedAtoms(ri,usedRI);
			if (nOvAt > 0)
			{
				//TODO - handle excplicitH
				
				//Registering the old atoms which overlap with newRI
				//into the helper 
				IAtom newUsedRIAtoms[] = new IAtom[usedRI.atoms.size()];
				IBond newUsedRIBonds[] = new IBond[usedRI.bonds.size()];				
				for (int k = 0; k < usedRI.atoms.size(); k++)
				{
					IAtom a = usedRI.atoms.get(k);
					int index = ri.atoms.indexOf(a);
					if (index == -1)
						newUsedRIAtoms[k] = a;  //atom is not part of the old ri hence it does not overlap with newRI
					else
						newUsedRIAtoms[k] = newRIAtoms[index]; //new replacement atom is taken
				}
				
				for (int k = 0; k < usedRI.bonds.size(); k++)
				{
					IBond b = usedRI.bonds.get(k);
					int index = ri.bonds.indexOf(b);
					if (index == -1)
						newUsedRIBonds[k] = b; //bond is not part of the old ri hence it does not overlap with newRI
					else
						newUsedRIBonds[k] = newRIBonds[index];
				}
				
				
				//Final replacement of the old atoms/bonds with new ones
				usedRI.atoms.clear();
				usedRI.bonds.clear();
				for (int k = 0; k < newUsedRIAtoms.length; k++)
					usedRI.atoms.add(newUsedRIAtoms[k]);
				for (int k = 0; k < newUsedRIBonds.length; k++)
					usedRI.bonds.add(newUsedRIBonds[k]);
				
				System.out.println("revised used instance: " + usedRI.debugInfo(incStep.struct));
				
			}
			
			
		}
		
		
		//System.out.println(incStep.debugInfo());
		
		
		//(3.2) All unused instances that overlap with the current rule instance ri 
		//are removed. Operation is performed in terms of the atoms from prevStruct. 		
		
		//System.out.println("check 3.1");
		Vector<RuleInstance> checkedInstances = new Vector<RuleInstance>();
		for (int i = 0; i < incStep.unUsedRuleInstances.size(); i++)
		{
			RuleInstance unusedRI = incStep.unUsedRuleInstances.get(i);
			int nOvAt = getNumOfOverlappedAtoms(ri,unusedRI);
			if (nOvAt == 0)
				checkedInstances.add(unusedRI);
			//else
			//	System.out.println("**excluded unused: " + unusedRI.debugInfo(prevStruct));
		}
		incStep.unUsedRuleInstances = checkedInstances;
		
		//(3.3)Also new instances are searched in several topological layers 
		//around the atoms from the current instance. This operation is performed in terms of the new atoms
		Vector<RuleInstance> newInstances = new Vector<RuleInstance>();
		IAtomContainer fragment = generateFragmentShell(incStep.struct, newRI, 2);		
		for (int i = 0; i < tman.knowledgeBase.rules.size(); i++)
		{	
			//System.out.println("$$ "  + i);
			Vector<IRuleInstance> instances = tman.knowledgeBase.rules.get(i).applyRule(fragment); 
			for (int k = 0; k < instances.size(); k++)
			{	
				RuleInstance genRI = (RuleInstance)instances.get(k);
				//Check whether genRI duplicates newRI
				int nOvAt = getNumOfOverlappedAtoms(newRI, genRI);
				if (nOvAt < newRI.atoms.size())
					newInstances.add(genRI);
			}	
		}
		
		//(3.4)Filter new instances so that they do not duplicate 
		//used and unused rule instances. 
		//Otherwise infinite recursion loops are obtained.  
		Vector<RuleInstance> filteredNewInstances = new Vector<RuleInstance>();
		for (int i = 0; i < newInstances.size(); i++)
		{
			RuleInstance testedRI = newInstances.get(i);
			boolean FlagOK = true;
			
			for (int k = 0; k < incStep.usedRuleInstances.size(); k++)
			{
				RuleInstance r = incStep.usedRuleInstances.get(k);
				int nOvAt = getNumOfOverlappedAtoms(testedRI,r);
				if (nOvAt == testedRI.atoms.size())
				{
					FlagOK = false;
					break;
				}
			}
			
			if (!FlagOK)
				continue;
			
			for (int k = 0; k < incStep.unUsedRuleInstances.size(); k++)
			{
				RuleInstance r = incStep.unUsedRuleInstances.get(k);
				int nOvAt = getNumOfOverlappedAtoms(testedRI,r);
				if (nOvAt == testedRI.atoms.size())
				{
					FlagOK = false;
					break;
				}
			}
			
			if (FlagOK)
				filteredNewInstances.add(testedRI);
		}
		
		incStep.unUsedRuleInstances.addAll(filteredNewInstances);
		
	}
	
	IAtomContainer generateFragmentShell(IAtomContainer mol, RuleInstance ri, int nLayers)
	{
		Molecule fragment = new Molecule();
		
		//Initial fragment
		for (int i = 0; i < ri.atoms.size(); i++)
			fragment.addAtom(ri.atoms.get(i));
		
		for (int i = 0; i < ri.bonds.size(); i++)
			fragment.addBond(ri.bonds.get(i));
		
		//System.out.print("initial fragment: " + SmartsHelper.moleculeToSMILES(fragment)); 
		
		//adding several layers around initial fragment
		Vector<IAtom> terminalAtoms = null;
		Vector<IAtom> layerAtoms = null;
		for (int i = 0; i < nLayers; i++)
		{	
			layerAtoms = addLayerToFragment(mol, fragment, terminalAtoms);
			terminalAtoms = layerAtoms;
		}
		
		//System.out.print("layered fragment: " + SmartsHelper.moleculeToSMILES(fragment));
		return fragment;
	}
	
	Vector<IAtom> addLayerToFragment(IAtomContainer mol, Molecule fragment, Vector<IAtom> terminalAtoms)
	{
		
		Vector<IAtom> termAtoms;		
		Vector<IAtom> layerAtoms = new Vector<IAtom>();
		
		if (terminalAtoms == null) //all atoms are treated as terminal ones
		{
			termAtoms = new Vector<IAtom>();
			for (int i = 0; i < fragment.getAtomCount(); i++)
				termAtoms.add(fragment.getAtom(i));
		}
		else
			termAtoms = terminalAtoms;
		
		if(termAtoms.isEmpty())
			return(layerAtoms); //No additional layer can be added
		
		//Adding the atoms and bonds from the first layer around the current fragment		
		for (int i = 0; i < termAtoms.size(); i++)
		{
			IAtom a = termAtoms.get(i);
			List<IAtom> list = mol.getConnectedAtomsList(a);
			for (int k = 0; k < list.size(); k++)
			{
				IAtom newAt = list.get(k);
				if (!termAtoms.contains(newAt))
				{
					//It is possible that current atom is already added
					//being connected to another terminal atom. However the bond should be checked in this case;
					if (!fragment.contains(newAt))
					{	
						layerAtoms.add(newAt);
						fragment.addAtom(newAt);
					}
					//Also it is possible that fragment already contains the new Atom 
					//but it does not contain Bond(a,newAt) 
					IBond newBo = mol.getBond(a, newAt);
					if (!fragment.contains(newBo))
						fragment.addBond(newBo);
				}
			}
		}
		
		
		//Checking for bonds between the atoms from the first layer		
		for (int i = 0; i < layerAtoms.size(); i++)
		{
			IAtom a = layerAtoms.get(i);
			List<IAtom> list = mol.getConnectedAtomsList(a);
			for (int k = 0; k < list.size(); k++)
			{
				IAtom a1 = list.get(k);
				if (layerAtoms.contains(a1))
				{	
					//The bond could be already added to this fragment. Check is needed.
					IBond newLayerBo = mol.getBond(a, a1);
					if (!fragment.contains(newLayerBo))
						fragment.addBond(newLayerBo);
				}	
			}
		}
		
		return(layerAtoms);
	}
	
	
	IAtom cloneAtom(IAtom a)
	{
		try
		{
			IAtom a1 = (IAtom)a.clone();
			return (a1);
		}	
		catch(Exception e)
		{
			tman.errors.add("Error cloning atom " + a.getSymbol());
		}
		
		return(null);
	}
	
	/*
	IBond cloneBond(IBond b)
	{
		try
		{
			IBond b1 = (IBond)b.clone();
			return (b1);
		}	
		catch(Exception e)
		{
			tman.errors.add("Error cloning bond ");
		}
		
		return(null);
	}
	*/
	
	
	/*
	void reviseUnusedRuleInstances(TautomerIncrementStep incStep)
	{
		//The unused rule instances the overlap with the used rule instances are revised
		//Strategy for economy rule application (searching) is used  
		//Topological distances from the used rule instances are taken inti account ...
		  
	}
	*/
		
	
}
