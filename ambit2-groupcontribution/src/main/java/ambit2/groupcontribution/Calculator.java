package ambit2.groupcontribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;



import org.openscience.cdk.interfaces.IBond;

import ambit2.groupcontribution.correctionfactors.ICorrectionFactor;
import ambit2.groupcontribution.descriptors.ILocalDescriptor;
import ambit2.groupcontribution.groups.BondGroup;
import ambit2.groupcontribution.groups.IGroup;
import ambit2.groupcontribution.groups.AtomGroup;


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
		switch (model.getModelType())
		{
		case ATOMIC:
			for (IAtom at : mol.atoms())
			{
				//TODO -- calcAtomGroup
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
