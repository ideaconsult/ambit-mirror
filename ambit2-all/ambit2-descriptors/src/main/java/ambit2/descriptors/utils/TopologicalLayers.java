package ambit2.descriptors.utils;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;


public class TopologicalLayers 
{
	public static List<IAtom>[] getAtomTopologicalLayers(IAtomContainer mol, int maxTopLayer)
	{
		//TODO
		return null;
	}
	
	public static List<IAtom>[] getAtomTopologicalLayers(IAtomContainer mol)
	{
		return getAtomTopologicalLayers(mol,-1);
	}
}
