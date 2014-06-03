package ambit2.reactions.generator.score;

import org.openscience.cdk.interfaces.IAtomContainer;


public interface IScoreComponent 
{	
	public double getWeight();
	
	public void setWeight(double weight);
	
	public double getScore(IAtomContainer mol);
}
