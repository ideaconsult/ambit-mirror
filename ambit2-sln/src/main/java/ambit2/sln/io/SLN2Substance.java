package ambit2.sln.io;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.relation.composition.CompositionRelation;
import ambit2.sln.SLNContainer;
import ambit2.sln.SLNContainerSet;

public class SLN2Substance 
{
	//Conversion flags
	public boolean FlagProportionToSLN = true;
	
	public List<CompositionRelation> slnToSubstanceComposition(SLNContainerSet slnContSet)
	{
		if (slnContSet == null)
			return null;
		
		List<CompositionRelation> composition = new ArrayList<CompositionRelation>();
		for (int i = 0; i < slnContSet.containers.size(); i++)
		{
			SLNContainer slnContainer = slnContSet.containers.get(i);
			CompositionRelation compRel = slnToCompositionRelation(slnContainer);
			composition.add(compRel);
		}
		
		return composition;
	}
	
	public SLNContainerSet substanceCompositionToSln(List<CompositionRelation> composition)
	{
		//TODO
		return null;
	}
	
	
	public CompositionRelation slnToCompositionRelation(SLNContainer slnContainer)
	{
		//TODO
		return null;
	}
	
	public SLNContainer CompositionRelationslnTo(CompositionRelation compRel)
	{
		//TODO
		return null;
	}
	
}
