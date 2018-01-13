package ambit2.reactions.rules.scores;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class AtomComplexity 
{
	protected int pathLenght = 2;
	protected boolean includeImplicitHAtoms = false;
	//protected boolean useBondTypes = false;
	
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
		List<IAtom[]> paths = ChemComplexityUtils.getAtomPaths(atom, mol, pathLenght);
		Map<String,Integer> pathFreq = new HashMap<String,Integer>();
		for (int i = 0; i < paths.size(); i++)
		{
			String pathString = getPathString(paths.get(i));
			ChemComplexityUtils.registerGroup(pathString, pathFreq);
		}
		
		int N = ChemComplexityUtils.totalGroupNum(pathFreq);
		
		if (N == 0)
			return 0;
		//John R. Proudfoot paper 2017
		double ac = ChemComplexityUtils.shannonEntropy(pathFreq, N);
		ac += ChemComplexityUtils.log2(N);
		return ac;
	}
	
	public String getPathString(IAtom path[])
	{
		//TODO
		return null;
	}
	
	
	
}
