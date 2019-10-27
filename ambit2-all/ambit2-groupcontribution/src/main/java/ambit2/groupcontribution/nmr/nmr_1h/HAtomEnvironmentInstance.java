package ambit2.groupcontribution.nmr.nmr_1h;

import org.openscience.cdk.interfaces.IAtom;

import ambit2.groupcontribution.nmr.SubstituentInstance;

public class HAtomEnvironmentInstance 
{
	public IAtom atoms[] = null;
	public HAtomEnvironment hEnvironment = null; 
	public SubstituentInstance substituentInstances[] = null; 
	
	public HAtomEnvironmentInstance() {
	}
	
	public HAtomEnvironmentInstance(IAtom atoms[], HAtomEnvironment hEnvironment) {
		this.atoms = atoms;
		this.hEnvironment = hEnvironment;
	}
}
