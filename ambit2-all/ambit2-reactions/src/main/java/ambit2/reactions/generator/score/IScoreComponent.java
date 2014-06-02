package ambit2.reactions.generator.score;

import org.openscience.cdk.interfaces.IAtomContainer;


public interface IScoreComponent 
{
	public enum Type{
		
	}
	
	public double getScore(IAtomContainer mol);
}
