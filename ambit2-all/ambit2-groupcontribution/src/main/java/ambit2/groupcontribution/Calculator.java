package ambit2.groupcontribution;

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.correctionfactors.ICorrectionFactor;
import ambit2.groupcontribution.groups.IGroup;

public class Calculator 
{
	
	public static double calcModelFor(IAtomContainer mol, GroupContributionModel model)
	{
		Map<IGroup,Integer> groups = getGroupsCount(mol, model);
		Map<ICorrectionFactor,Integer> corrFactors = getCorrectionFactorsCount(mol, model);
		
		//TODO
		return 0.0;
	}
	
	public static Map<IGroup,Integer> getGroupsCount(IAtomContainer mol, GroupContributionModel model)
	{
		//TODO
		return null;
	}
	
	public static Map<ICorrectionFactor,Integer> getCorrectionFactorsCount(IAtomContainer mol, GroupContributionModel model)
	{
		//TODO
		return null;
	}
}
