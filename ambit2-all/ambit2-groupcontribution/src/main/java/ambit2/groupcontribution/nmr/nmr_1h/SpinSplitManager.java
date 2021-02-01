package ambit2.groupcontribution.nmr.nmr_1h;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.smarts.AtomEquivalenceInfo;
import ambit2.smarts.TopLayer;

public class SpinSplitManager 
{
	IAtomContainer molecule = null;
	//List<HAtomEnvironmentInstance> hAtEnvInstances = null;
	//Map<IAtom, HShift> atomHShifts = null;
	List<HShift> hShifts = null;
	int atomClasses[] = null;	
	Map<String, Integer> codeClasses = new HashMap<String, Integer>();
	//String eqAtCodes[] = null;
	
	int numLayers = 2;
	
	public void setup(IAtomContainer molecule, List<HShift> hShifts) {
		this.molecule = molecule;
		this.hShifts = hShifts; 
	}
	
	public void caclulateSplits() 
	{
		calcAtomEquivalenceClasses();
		
		for (int i = 0; i < hShifts.size(); i++)
		{
			HShift hShift = hShifts.get(i);
			IAtom at = molecule.getAtom(hShift.atomIndex);
			SpinSplitting spinSplit = generateSpinSplitting(at);
			hShift.spinSplit = spinSplit;
		}
	}
	
	void calcAtomEquivalenceClasses()
	{
		atomClasses = new int[molecule.getAtomCount()]; 
		codeClasses.clear();
		//eqAtCodes = new String[molecule.getAtomCount()];
		int curNumOfClasses = 0;
		
		for (int i = 0; i < molecule.getAtomCount(); i++)
		{
			IAtom at = molecule.getAtom(i);
			AtomEquivalenceInfo atEqInfo = new AtomEquivalenceInfo();
			atEqInfo.initialize(at, numLayers, molecule);
			String eqAtCode = atEqInfo.toString();
			//eqAtCodes[i] = eqAtCode;
			
			//Get code class number or register new class number
			Integer classNum = codeClasses.get(eqAtCode);
			if (classNum == null)
			{
				classNum = curNumOfClasses;
				curNumOfClasses++;
				codeClasses.put(eqAtCode, classNum);
			}
			
			atomClasses[i] = classNum;
		}
		
	}
	
	SpinSplitting generateSpinSplitting(IAtom at)
	{
		//Get simple spin splitting without JJ coupling
		TopLayer tl = (TopLayer)at.getProperty(TopLayer.TLProp);		
		
		List<Integer> classes = new ArrayList<Integer>();
		List<Integer> classHAtNumber = new  ArrayList<Integer>();
		
		for (int i = 0; i < tl.atoms.size(); i++)
		{
			IAtom neighAt = tl.atoms.get(i);
			int atIndex = molecule.indexOf(neighAt);
			int classNum = atomClasses[atIndex];			
			//This code works for molecules with implicit H atoms
			int numOfHAtoms = neighAt.getImplicitHydrogenCount();
			if (numOfHAtoms == 0)
				continue;
			int k = classes.indexOf(classNum);
			
			if (k == -1)
			{
				classes.add(classNum);
				classHAtNumber.add(numOfHAtoms);
			}
			else
			{
				int new_nH = numOfHAtoms + classHAtNumber.get(k);
				classHAtNumber.set(k, new_nH);
			}
			
		}
		
		SpinSplitting spinSplit = new SpinSplitting();
		if (!classes.isEmpty())
		{
			spinSplit.numSplitLevels = classes.size();
			spinSplit.splits = new int[classes.size()];
			for (int i = 0; i < classHAtNumber.size(); i++)			
				spinSplit.splits[i] = classHAtNumber.get(i) + 1;
		}
		
		return spinSplit;
	}
	
	public String getEquivalenceAtomsInfo() 
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < molecule.getAtomCount(); i++)
		{
			IAtom at = molecule.getAtom(i);
			sb.append("#" + (i+1) + "  " + at.getSymbol() + "  " + atomClasses[i]);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	
}
