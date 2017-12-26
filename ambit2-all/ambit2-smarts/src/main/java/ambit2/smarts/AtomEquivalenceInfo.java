package ambit2.smarts;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class AtomEquivalenceInfo 
{
	public static final int SHIFT_ATOM_ELEMENT = 10000000;
	public static final int SHIFT_BOND_TYPE    = 1000000;
	public static final int SHIFT_POS_CHARGE   = 100000;
	public static final int SHIFT_NEG_CHARGE   = 10000;
	public static final int SHIFT_ISOTOPE      = 1000;
	public static final int SHIFT_TOP_LAYER   = 1;
	
	
	//atom layers information is added incrementally
	public List<Integer> atomLayersCode = new ArrayList<Integer>();
	boolean isTerminalAtom = false;
	int curLayerNum = 0;
	int nProcessedBonds = 0;
	public List<IAtom> curLayerAtoms = new ArrayList<IAtom>();

	public void initialize(IAtom at)
	{
		atomLayersCode.add(getAtomCode(at, null));
		curLayerAtoms.add(at);
	}
	
	public void initialize(IAtom at, int numLayers, IAtomContainer mol)
	{
		atomLayersCode.add(getAtomCode(at, null));
		curLayerAtoms.add(at);
		for (int i = 0; i < numLayers; i++)
			nextLayer(mol);
	}
	
	public void firstLayer(IAtomContainer mol)
	{
		IAtom at = curLayerAtoms.get(0);
		TopLayer tl = (TopLayer)at.getProperty(TopLayer.TLProp);
		
		List<IAtom> newLayerAtoms = new ArrayList<IAtom>();
		List<Integer> codes = new ArrayList<Integer>();
		
		if (tl.atoms.size() == 1)
		{
			IAtom a = tl.atoms.get(0);
			IBond b = tl.bonds.get(0);
			atomLayersCode.add(getAtomCode(a,b) + (curLayerNum+1));
			newLayerAtoms.add(a);
		}
		else
		{	
			for (int k = 0; k < tl.atoms.size(); k++)
			{
				IAtom a = tl.atoms.get(k);
				IBond b = tl.bonds.get(k);
				codes.add(getAtomCode(a,b) + (curLayerNum+1));
			}

			int sort[] = getSortedCodesIndices(codes);
			for (int k = 0; k < sort.length; k++)
			{
				int index = sort[k];
				atomLayersCode.add(codes.get(index));
				newLayerAtoms.add(tl.atoms.get(index));
			}
		}
		
		curLayerNum++;
		curLayerAtoms.clear();
		curLayerAtoms.addAll(newLayerAtoms);
	}	
	
	public void nextLayer(IAtomContainer mol)
	{
		if (nProcessedBonds >= mol.getBondCount())
			return;
		
		//Special treatment for the first layer
		if (curLayerNum == 0)
		{
			firstLayer(mol);
			return;
		}	
		
		List<IAtom> newLayerAtoms = new ArrayList<IAtom>();
		List<IBond> curLayerInnerBonds = new ArrayList<IBond>();
		
		for (int i = 0; i < curLayerAtoms.size(); i++)
		{
			IAtom at = curLayerAtoms.get(i);
			TopLayer tl = (TopLayer)at.getProperty(TopLayer.TLProp);
			if (tl.atoms.size() == 1)
					continue; // it is a terminal atom from the current layer
			
			for (int k = 0; k < tl.atoms.size(); k++)
			{
				//TODO
			}
			//TODO
		}
		
		curLayerNum++;
		curLayerAtoms.clear();
		curLayerAtoms.addAll(newLayerAtoms);
	}
	
	public static Integer getAtomCode(IAtom atom, IBond bond)
	{
		int n = atom.getAtomicNumber() * SHIFT_ATOM_ELEMENT;
		if (bond != null)
			n = n + bond.getOrder().numeric() * SHIFT_BOND_TYPE; 
		
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
	
	
	public static int[] getSortedCodesIndices(List<Integer> codes)
	{
		int n = codes.size();
		int indices[] = new int[n];
		
		switch (n)
		{
		case 0:
			return null;
		case 1:
			indices[0] = 0;
			break;
		case 2:
			if (codes.get(0) > codes.get(1))
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
					if (codes.get(indices[k]) > codes.get(indices[k+1]))
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
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < atomLayersCode.size(); i++)
			sb.append(" " + atomLayersCode.get(i));
		return sb.toString();
	}
	
}
