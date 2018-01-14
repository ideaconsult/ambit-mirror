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
		List<IAtom[]> paths = ChemComplexityUtils.getAtomPaths(atom, mol, pathLenght, includeImplicitHAtoms);
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
	
	public String getAtomPathsAsString(IAtom atom, IAtomContainer mol)
	{
		List<IAtom[]> paths = ChemComplexityUtils.getAtomPaths(atom, mol, pathLenght, includeImplicitHAtoms);
		StringBuffer sb = new StringBuffer();
		for (IAtom[] path : paths)
			sb.append(getPathString(path) + "\n");
		return sb.toString();
	}
	
	public String getPathString(IAtom path[])
	{
		StringBuffer sb =  new StringBuffer();
		int xd[];
		for (int i = 0; i < path.length-1; i++)
		{	
			sb.append(path[i].getSymbol());
			xd = ChemComplexityUtils.getAtomXDInfo(path[i]);
			sb.append("X"+xd[0]);
			sb.append("D"+xd[1]);
			sb.append("~");
		}
		
		//Last atom in the atom path
		IAtom at = path[path.length-1];
		if (at == null)
		{
			//at is a implicit hydrogen
			sb.append("HX1D1");
		}
		else
		{	
			sb.append(at.getSymbol());
			xd = ChemComplexityUtils.getAtomXDInfo(at);
			sb.append("X"+xd[0]);
			sb.append("D"+xd[1]);
		}
		
		return sb.toString();
	}
	
}
