package ambit2.groupcontribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.correctionfactors.ICorrectionFactor;
import ambit2.groupcontribution.groups.IGroup;


public class Calculator 
{
	
	public static double calcModelFor(IAtomContainer mol, GroupContributionModel model)
	{
		double modelValue = 0.0;
		
		//Group contributions
		Map<IGroup,Integer> groups = getGroupsCount(mol, model);			
		Map<String, IGroup> modelGroups = model.getGroups(); 		
		for (Map.Entry<IGroup,Integer> entry : groups.entrySet())
		{
			IGroup group = entry.getKey();
			if (modelGroups.containsKey(group.getDesignation()));
				modelValue += group.getContribution() * entry.getValue(); 
		}
		
		//Correction factors
		Map<ICorrectionFactor,Integer> corrFactors = getCorrectionFactorsCount(mol, model);	
		for (Map.Entry<ICorrectionFactor,Integer> entry : corrFactors.entrySet())
			modelValue +=  entry.getKey().getContribution() * entry.getValue();
			
		return modelValue;
	}	
	
	public static Map<IGroup,Integer> getGroupsCount(IAtomContainer mol, GroupContributionModel model)
	{
		Map<IGroup,Integer> groups = new HashMap<IGroup,Integer>();
		//TODO
		return groups;
	}
	
	public static Map<ICorrectionFactor,Integer> getCorrectionFactorsCount(IAtomContainer mol, GroupContributionModel model)
	{
		Map<ICorrectionFactor,Integer> corrFactors = new HashMap<ICorrectionFactor,Integer>();
		ArrayList<ICorrectionFactor> modelCorrFactors = model.getCorrectionFactors();
		for (ICorrectionFactor factor : modelCorrFactors)
		{
			int count = factor.calculateFor(mol);
			if (count > 0)
				corrFactors.put(factor, new Integer(count));
		}
			
		return corrFactors;
	}
}
