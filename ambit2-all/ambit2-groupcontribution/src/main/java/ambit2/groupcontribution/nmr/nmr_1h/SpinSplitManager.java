package ambit2.groupcontribution.nmr.nmr_1h;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.smarts.AtomEquivalenceInfo;

public class SpinSplitManager 
{
	IAtomContainer molecule = null;
	//List<HAtomEnvironmentInstance> hAtEnvInstances = null;
	Map<IAtom, HShift> atomHShifts = null;
	Map<IAtom,Integer> atEqClasses = new HashMap<IAtom,Integer>();	
	Map<String, Integer> codeClasses = new HashMap<String, Integer>();
	//String eqAtCodes[] = null;
	
	int numLayers = 2;
	
	public void setup(IAtomContainer molecule, Map<IAtom, HShift> atomHShifts) {
		this.molecule = molecule;
		this.atomHShifts = atomHShifts; 
	}
	
	public void caclulateSplits() 
	{
		calcAtomEquivalenceClasses();
		
		Set<IAtom> keyAtoms = atomHShifts.keySet();
		for (IAtom at: keyAtoms)
		{
			HShift hShift = atomHShifts.get(at);
			
			SpinSplitting spinSplit = generateSpinSplitting(at);
			hShift.spinSplit = spinSplit;
		}
	}
	
	void calcAtomEquivalenceClasses()
	{
		atEqClasses.clear(); 
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
		}
		
		
	}
	
	SpinSplitting generateSpinSplitting(IAtom at)
	{
		//TODO
		return null;
	}
	
	
}
