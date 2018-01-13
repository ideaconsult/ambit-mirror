package ambit2.reactions.rules.scores;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class AtomComplexity 
{
	protected int pathLenght = 2;
	protected boolean includeImplicitHAtoms = false;
	
	public int getPathLenght() {
		return pathLenght;
	}

	public void setPathLenght(int pathLenght) {
		this.pathLenght = pathLenght;
	}

	public boolean isIncludeImplicitHAtoms() {
		return includeImplicitHAtoms;
	}
	
	public void setIncludeImplicitHAtoms(boolean includeImplicitHAtoms) {
		this.includeImplicitHAtoms = includeImplicitHAtoms;
	}

	public double calcAtomComplexity(IAtom atom, IAtomContainer mol)
	{
		//TODO
		return 0.0;
	}
	
	
}
