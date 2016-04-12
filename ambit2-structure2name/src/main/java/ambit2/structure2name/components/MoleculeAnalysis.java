package ambit2.structure2name.components;

import java.util.List;

import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;

import ambit2.smarts.SmartsParser;

public class MoleculeAnalysis 
{
	private IAtomContainer molecule = null;
	private IRingSet ringSet = null;
	private List<int[]> atomRingIndices = null;
	
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
	
	
	public List<int[]> getAtomRingIndices() {
		return atomRingIndices;
	}

	public void setAtomRingIndices(List<int[]> atomRingIndices) {
		this.atomRingIndices = atomRingIndices;
	}
	
	private void handleRingData()
	{
		setAtomRingIndices(SmartsParser.getRindData2(molecule, ringSet));
	}
	
	public void analyse()
	{
		if (ringSet == null)
			calcRingSet();
		
		handleRingData();
	}
	
}
