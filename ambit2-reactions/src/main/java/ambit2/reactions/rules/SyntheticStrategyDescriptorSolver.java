package ambit2.reactions.rules;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.rules.scores.MolecularComplexity;
import ambit2.reactions.rules.scores.RingComplexity;
import ambit2.rules.conditions.AbstractDescriptorSolver;

public class SyntheticStrategyDescriptorSolver extends AbstractDescriptorSolver
{
	MolecularComplexity molComplexity = new MolecularComplexity();
	
	public SyntheticStrategyDescriptorSolver()
	{
		descriptorList.add("MOL_COMPLEXITY_01");
		descriptorList.add("MOL_COMPLEXITY_02");
		descriptorList.add("NUMBER_OF_STEREO_ELEMENTS");
		descriptorList.add("RING_COMPLEXITY");
		descriptorList.add("CYCLOMATIC_NUMBER");
		descriptorList.add("WEIGHTED_NUMBER_OF_STEREO_ELEMENTS");
		descriptorList.add("NUMBER_OF_TETRAHEDRAL_STEREO_ELEMENTS");
		
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
		case 2:
			return new Double(MolecularComplexity.
					numberOfStereoElements((IAtomContainer)target));
		case 3:
			return RingComplexity.ringComplexity((IAtomContainer)target);
		case 4:
			return new Double(RingComplexity.
					cyclomaticNumber((IAtomContainer)target));
		case 5:
			return new Double(MolecularComplexity.
					weightedNumberOfStereoElements((IAtomContainer)target));
		case 6:
			return new Double(MolecularComplexity.
					numberOfTetrahedralStereoElements((IAtomContainer)target));
			
		}
		return null;
	}
	   

}
