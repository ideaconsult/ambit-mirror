package ambit2.structure2name.components;


import java.util.List;

import org.openscience.cdk.interfaces.IAtom;

public abstract class AbstractComponent implements IIUPACComponent 
{
	List<IAtom> atoms = null;
	
	long rank = 0;
	
	public long getRank() {
		return rank;
	}
	
	public void setRank(long rank) {
		this.rank = rank;
	}
	
	public List<IAtom> getAtoms(){
		return atoms;
	}
	
	public void setAtoms (List<IAtom> atoms) {
		this.atoms = atoms;
	}
}
