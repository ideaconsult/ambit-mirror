package ambit2.groupcontribution;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	public static double calcModelFor(IAtomContainer mol, GroupContributionModel model)
	{
		double modelValue = 0.0;
		
		//Group contributions
		Map<String,Integer> groups = getGroupsCount(mol, model);			
		Map<String, IGroup> modelGroups = model.getGroups(); 		
		
		for (Map.Entry<String,Integer> entry : groups.entrySet())
		{
			String grpDes = entry.getKey();
			if (modelGroups.containsKey(grpDes));
				modelValue += modelGroups.get(grpDes).getContribution() * entry.getValue(); 
		}
		
		//Correction factors
		Map<ICorrectionFactor,Integer> corrFactors = getCorrectionFactorsCount(mol, model);	
		for (Map.Entry<ICorrectionFactor,Integer> entry : corrFactors.entrySet())
			modelValue +=  entry.getKey().getContribution() * entry.getValue();
			
		return modelValue;
	}	
	
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
