package ambit2.groupcontribution.nmr.nmr_1h;

import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class SpinSplitManager 
{
	IAtomContainer molecule = null;
	//List<HAtomEnvironmentInstance> hAtEnvInstances = null;
	Map<IAtom, HShift> atomHShifts = null;
	
	public void setup(IAtomContainer molecule, Map<IAtom, HShift> atomHShifts) {
		this.molecule = molecule;
		this.atomHShifts = atomHShifts; 
	}
	
	public void caclulateSplits() 
	{
		
	}
	
	
	
	
	
}
