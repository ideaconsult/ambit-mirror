package ambit2.reactions.mapping;

import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class ReactionAtomMapping 
{
	enum MappingStrategy {
		FAST, FUZZY, INTERACTIVE, EXHAUSTIVE
	}
	
	private MappingStrategy mapStrategy = MappingStrategy.FAST;
	
	
	
	
	
	public Map<IAtom,IAtom> getReactionMapping(IAtomContainer container1, IAtomContainer container2)
	{	
		//1.Find all components (fragments)
		//2.Determine most probable relationships between the components
		//3.Generate all possible combinations of component relationships
		//4.For each relationship map all corresponding components
		
		//TODO
		return null;
	}
	
	
	private Map<IAtom,IAtom> mapComponents(IAtomContainer comp1, IAtomContainer comp2)
	{
		//TODO
		return null;
	}
	
	private double fragmentPreMappingProbability(IAtomContainer frag1, IAtomContainer frag2) 
	{
		//TODO
		return 1.0;
	}


	public MappingStrategy getMapStrategy() {
		return mapStrategy;
	}


	public void setMapStrategy(MappingStrategy mapStrategy) {
		this.mapStrategy = mapStrategy;
	}
	
}
