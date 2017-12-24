package ambit2.smarts;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class AtomEquivalenceInfo 
{
	public static final int SHIFT_ATOM_ELEMENT = 100000000;
	public static final int SHIFT_POS_CHARGE   = 1000000;
	public static final int SHIFT_NEG_CHARGE   = 100000;
	public static final int SHIFT_ISOTOPE      = 1000;
	public static final int SHIFT_BOND_TYPE    = 100;
	public static final int SHIFT_PREV_LAYER   = 1;
	
	
	//atom layers information is added incrementally
	public List<Integer> atomLayersCode = new ArrayList<Integer>();
	boolean isTerminalAtom = false;
	int curLayerNum = 0;
	int nProcessedAtoms = 0;
	public List<IAtom> curLayerAtoms = new ArrayList<IAtom>();

	public void initialize(IAtom at)
	{
		atomLayersCode.add(getAtomCode(at));
		curLayerAtoms.add(at);
		nProcessedAtoms = 1;
	}
	
	public void initialize(IAtom at, int numLayers, IAtomContainer mol)
	{
		atomLayersCode.add(getAtomCode(at));
		curLayerAtoms.add(at);
		for (int i = 0; i < numLayers; i++)
			nextLayer(mol);
	}
	
	public void nextLayer(IAtomContainer mol)
	{
		if (nProcessedAtoms >= mol.getAtomCount())
			return;
		
		List<IAtom> newLayerAtoms = new ArrayList<IAtom>();
		for (int i = 0; i < curLayerAtoms.size(); i++)
		{
			IAtom at = curLayerAtoms.get(i);
			TopLayer topLayer = (TopLayer)at.getProperty(TopLayer.TLProp);
			
			//TODO
		}
		
		curLayerAtoms.clear();
		curLayerAtoms.addAll(newLayerAtoms);
	}
	
	public static Integer getAtomCode(IAtom atom)
	{
		Integer n = atom.getAtomicNumber()*SHIFT_ATOM_ELEMENT;
		//TODO handle charges and isotopes
		return n;
	}
	
	public static int[] getSortedCodesIndices(int codes[])
	{
		int n = codes.length;
		int indices[] = new int[n];
		
		switch (n)
		{
		case 0:
			return null;
		case 1:
			indices[0] = 0;
			break;
		case 2:
			if (codes[0] > codes[1])
			{
				indices[0] = 1;
				indices[1] = 0;
			}
			else
			{
				indices[0] = 0;
				indices[1] = 1;
			}
			break;
			
		default: //n >= 3 
			//initialize indices
			for (int i = 0; i < n; i++)
				indices[i] = i;
			//bubble algorithm
			int tmp;
			for (int i = (n-2); i >= 0; i--)
				for (int k = 0; k <= i; k++)
				{
					if (codes[indices[k]] > codes[indices[k+1]])
					{
						tmp = indices[k];
						indices[k] = indices[k+1];
						indices[k+1] = tmp;
					}
				}
			break;
		}
		
		return indices;
	}
	
	
}
