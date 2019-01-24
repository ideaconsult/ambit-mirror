package ambit2.structure2name.components;


import java.util.List;

import org.openscience.cdk.interfaces.IAtom;


public abstract class AbstractComponent implements IIUPACComponent 
{
	protected List<IAtom> atoms = null;
	protected long rank = 0;
	protected CompState state = CompState.INITIAL; 
	
	public CompState getState() {
		return state;
	}
	
	public void setState(CompState state) {
		this.state = state;
	}
	
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
