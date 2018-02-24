package ambit2.reactions.rules.scores;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;

public class RingComplexity 
{
	public static double ringComplexity(IAtomContainer mol)
	{
		IRingSet ringSet = Cycles.sssr(mol).toRingSet();
		return ringComplexity(ringSet); 
	}
	
	public static double ringComplexity(IRingSet ringSet)
	{
		//Ring complexity is the ratio of sum of all SSSR ring sizes
		//and the number of all ring atoms
		//Typically ringComplexity will vary between 1.0 and 1.5
		//If there are no ring condensation, bridged structures 
		//or spiro atoms the ratio is 1.0
		//Also if no rings are present in the structure complexity is
		//set to 1.0
		
		if (ringSet.isEmpty())
			return 1.0;
		
		int ringSizeSum = 0;
		List<IAtom> ringAtoms = new ArrayList<IAtom>();
		
		for (IAtomContainer ring : ringSet.atomContainers())
		{
			ringSizeSum += ring.getAtomCount();
			for (IAtom at : ring.atoms())
			{	
				if (!ringAtoms.contains(at))
					ringAtoms.add(at);
			}
		}
		
		if (ringAtoms.size() > 0)
			return ((double)ringSizeSum) / ringAtoms.size();
		else
			return 1.0; //in case of incorrect ringSet
	}
	
	public static int cyclomaticNumber(IAtomContainer mol)
	{	
		return (mol.getBondCount() - mol.getAtomCount() + 1);
	}
}
