package ambit2.groupcontribution.nmr.nmr_1h;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

public class SpinSplitManager 
{
	IAtomContainer molecule = null;
	List<HAtomEnvironmentInstance> hAtEnvInstances = null;
	
	public void setup(IAtomContainer molecule, List<HAtomEnvironmentInstance> hAtEnvInstances) {
		this.molecule = molecule;
		this.hAtEnvInstances = hAtEnvInstances; 
	}
	
	public void caclulateSplits() {
		//TODO
	}
	
}
