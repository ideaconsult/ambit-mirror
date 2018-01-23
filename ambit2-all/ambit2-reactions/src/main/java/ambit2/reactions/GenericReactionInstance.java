package ambit2.reactions;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class GenericReactionInstance 
{
	public GenericReaction reaction = null;
	public double score = 0.0;
	public List<IAtom> instanceAtoms = null;
	//public List<IAtom> instanceAtomIndices = null;
	public IAtomContainer target = null;
	public IAtomContainer products = null;
	
	public GenericReactionInstance()
	{	
	}
	
	public GenericReactionInstance(GenericReaction reaction, IAtomContainer target, List<IAtom> instanceAtoms)
	{
		this.reaction = reaction;
		this.target = target;
		this.instanceAtoms = instanceAtoms;
	}
	
	public GenericReactionInstance(GenericReaction reaction, IAtomContainer target, 
			List<IAtom> instanceAtoms, IAtomContainer products)
	{
		this.reaction = reaction;
		this.target = target;
		this.instanceAtoms = instanceAtoms;
		this.products = products;
	}
	
	public void generateProducts()
	{
		//TODO
	}
	
}
