package ambit2.reactions.generator.score;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IAtomContainer;


public class ScoreFunction 
{	
	private ArrayList<IScoreComponent> scoreComponents = new ArrayList<IScoreComponent>(); 
	
	public double getScore(IAtomContainer mol)
	{
		double score = 0.0;
		double w = 0.0;
		
		for (IScoreComponent component : scoreComponents)
		{	
			score += component.getWeight() * component.getScore(mol);
			w += component.getWeight();
		}
		return score / w;
	}
}
