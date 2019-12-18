package ambit2.groupcontribution.correctionfactors;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.transformations.IValueTransformation;

public class DescriptorCorrectionFactor implements ICorrectionFactor
{

	@Override
	public IValueTransformation getValueTransformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValueTransformation(IValueTransformation transformation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getContribution() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setContribution(double contribution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDesignation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double calculateFor(IAtomContainer mol) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
