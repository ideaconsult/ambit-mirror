package ambit2.structure2name.components;

import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;

public class MoleculeAnalysis 
{
	private IAtomContainer molecule = null;
	private IRingSet ringSet = null;
	
	
	public IAtomContainer getMolecule() {
		return molecule;
	}

	public void setMolecule(IAtomContainer molecule) {
		this.molecule = molecule;
	}
	
	public void calcRingSet()
	{
		setRingSet(Cycles.sssr(molecule).toRingSet());
	}

	public IRingSet getRingSet() {
		return ringSet;
	}

	public void setRingSet(IRingSet ringSet) {
		this.ringSet = ringSet;
	}
}
