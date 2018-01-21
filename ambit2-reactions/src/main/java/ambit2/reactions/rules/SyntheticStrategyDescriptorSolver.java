package ambit2.reactions.rules;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.rules.scores.MolecularComplexity;
import ambit2.rules.conditions.AbstractDescriptorSolver;

public class SyntheticStrategyDescriptorSolver extends AbstractDescriptorSolver
{
	MolecularComplexity molComplexity = new MolecularComplexity();
	
	public SyntheticStrategyDescriptorSolver()
	{
		descriptorList.add("mol-complexity01");
		descriptorList.add("mol-complexity02");
		//TODO fill descriptor list
	}
	
	@Override
	public Object calculateDescriptor(String descrName, Object target) {
		int index = descriptorList.indexOf(descrName);
		switch (index)
		{
		case 0:
			molComplexity.setTarget((IAtomContainer)target);
			return molComplexity.calcMolecularComplexity01(); 
		case 1:
			molComplexity.setTarget((IAtomContainer)target);
			return molComplexity.calcMolecularComplexity02();
		}
		return null;
	}

}
