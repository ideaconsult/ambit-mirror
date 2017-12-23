package ambit2.smarts;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class AtomEquivalenceInfo 
{
	//atom layers information is added incrementally
	public List<Integer> atomLayersCode = new ArrayList<Integer>();
	boolean isTerminalAtom = false;
	int curLayerNum = 0;
	public List<IAtom> curLayerAtoms = new ArrayList<IAtom>();

	public void initialize(IAtom at)
	{
		atomLayersCode.add(getAtomCode(at));
		curLayerAtoms.add(at);
	}
	
	public void initialize(IAtom at, int numLayers, IAtomContainer mol)
	{
		atomLayersCode.add(getAtomCode(at));
		curLayerAtoms.add(at);
		for (int n = 1; n <= numLayers; n++)
			nextLayer(mol);
	}
	
	public void nextLayer(IAtomContainer mol)
	{
		//TODO
	}
	
	public static Integer getAtomCode(IAtom atom)
	{
		Integer n = atom.getAtomicNumber();
		//TODO handle charges and isotopes
		return n;
	}
}
