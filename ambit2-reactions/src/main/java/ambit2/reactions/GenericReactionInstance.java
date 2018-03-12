package ambit2.reactions;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.rules.scores.ReactionScore;
import ambit2.rules.conditions.ICondition;

public class GenericReactionInstance 
{
	public GenericReaction reaction = null;
	//public double score = 0.0;
	public ReactionScore reactionScore = null;
	public List<IAtom> instanceAtoms = null;
	//public List<IAtom> instanceAtomIndices = null;
	public IAtomContainer target = null;
	public IAtomContainer products = null;
	public boolean conditionsStatus[] = null;
	public boolean applicationConditionsStatus[] = null;
	
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
	
	public void calculateConditionsStatus()
	{
		if (reaction == null)
			return;
		
		if (reaction.conditions != null)
		{	
			int n = reaction.conditions.size();
			conditionsStatus = new boolean[n];
			for (int i = 0; i < n ; i++)
			{
				ICondition cond = reaction.conditions.get(i);
				conditionsStatus[i] = cond.isTrue(this);
			}
		}
		
		if (reaction.applicationConditions != null)
		{	
			int n = reaction.applicationConditions.size();
			applicationConditionsStatus = new boolean[n];
			for (int i = 0; i < n ; i++)
			{
				ICondition cond = reaction.applicationConditions.get(i);
				applicationConditionsStatus[i] = cond.isTrue(this);
			}
		}
	}
	
	public double getConditionsScore()
	{
		double score = 0.0;
		if (conditionsStatus != null)
			for (int i = 0; i < conditionsStatus.length; i++)
			{
				if (conditionsStatus[i])
					score += reaction.conditionScores.get(i);
			}
		return score;
	}
	
	public boolean getApplicationStatus()
	{
		if (applicationConditionsStatus != null)
			for (int i = 0; i < applicationConditionsStatus.length; i++)
			{
				if (!applicationConditionsStatus[i])
					return false;
			}
		return true;
	}
	
}
