package ambit2.groupcontribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import ambit2.groupcontribution.correctionfactors.ICorrectionFactor;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.groups.AtomGroup;
import ambit2.groupcontribution.groups.BondGroup;
import ambit2.groupcontribution.groups.IGroup;


public class Calculator 
{
	/*
	 * Currently not used. It calculated directly from GroupContributionModel object
	 * 
	public static double calcModelFor(IAtomContainer mol, GroupContributionModel model)
	{
		double modelValue = 0.0;
		
		//Group contributions
		Map<String,Integer> groups = getGroupsCount(mol, model);			
		Map<String, IGroup> modelGroups = model.getGroups(); 		
		
		for (Map.Entry<String,Integer> entry : groups.entrySet())
		{
			String grpDes = entry.getKey();
			System.out.println("-->" + grpDes);
			if (modelGroups.containsKey(grpDes))
				modelValue += modelGroups.get(grpDes).getContribution() * entry.getValue(); 
		}
		
		//Correction factors
		Map<ICorrectionFactor,Double> corrFactors = getCorrectionFactorsCount(mol, model);	
		for (Map.Entry<ICorrectionFactor,Double> entry : corrFactors.entrySet())
			modelValue +=  entry.getKey().getContribution() * entry.getValue();
			
		return modelValue;
	}
	*/	
	
	public static Map<String,Integer> getGroupsCount(IAtomContainer mol, GroupContributionModel model)
	{
		Map<String,Integer> groups = new HashMap<String,Integer>();
		switch (model.getModelType())
		{
		case ATOMIC:
			for (IAtom at : mol.atoms())
			{
				IGroup group = calcAtomGroup(at, mol,  model);				
				registerGroup(groups, group);
			}
			break;
		case BOND_BASED:
			//TODO
			break;
			
		default:
			break;
		}
		
		return groups;
	}
	
	public static void registerGroup(Map<String,Integer> groups, IGroup group)
	{
		String grpDes = group.getDesignation(); 
		if (groups.containsKey(grpDes))	
			groups.put(grpDes, groups.get(grpDes) + 1);
		
		else
			groups.put(grpDes, new Integer(1));
	}
	
	public static AtomGroup calcAtomGroup(IAtom a, IAtomContainer mol, GroupContributionModel model)
	{
		AtomGroup atGrp = new AtomGroup();
		StringBuffer des = new StringBuffer();
		for (ILocalDescriptor ld : model.getLocalDescriptors())
		{
			int descrVal = ld.calcForAtom(a, mol);
			des.append(ld.getDesignation(descrVal));
		}
		
		atGrp.setAtomDesignation(des.toString());
		return atGrp;
	}
	
	public static BondGroup calcBondGroup(IBond b, IAtomContainer mol, GroupContributionModel model)
	{
		BondGroup boGrp = new BondGroup();
		//TODO
		return boGrp;
	}
	
	
	
	public static Map<ICorrectionFactor,Double> getCorrectionFactorsCount(IAtomContainer mol, GroupContributionModel model)
	{
		Map<ICorrectionFactor,Double> corrFactors = new HashMap<ICorrectionFactor,Double>();
		List<ICorrectionFactor> modelCorrFactors = model.getCorrectionFactors();
		for (ICorrectionFactor factor : modelCorrFactors)
		{
			double cf = factor.calculateFor(mol);
			if (cf != 0.0)
				corrFactors.put(factor, cf);
		}
			
		return corrFactors;
	}
}
