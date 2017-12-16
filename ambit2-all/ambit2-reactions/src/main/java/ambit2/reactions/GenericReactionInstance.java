package ambit2.reactions;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class GenericReactionInstance 
{
	public GenericReaction reaction = null;
	public double score = 0.0;
	public List<IAtom> instanceAtoms = null;
	public IAtomContainer target = null;
	public IAtomContainer products = null;
	
	public void generateProducts()
	{
		//TODO
	}
	
}
