package ambit2.reactions.generator.score.components;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.generator.score.IScoreComponent;

public class KnimeWorkflowScore implements IScoreComponent 
{
	private double weight = 1.0;
	
	public double getWeight()
	{
		return weight;
	};
	
	public void setWeight(double weight)
	{
		this.weight = weight;
	};
	
	public double getScore(IAtomContainer mol)
	{
		//TODO
		return 0.0;
	};

}
