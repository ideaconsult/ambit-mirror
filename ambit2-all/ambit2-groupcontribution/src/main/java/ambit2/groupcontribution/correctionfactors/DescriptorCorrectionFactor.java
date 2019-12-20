package ambit2.groupcontribution.correctionfactors;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.groupcontribution.descriptors.CDKDescriptorInfo;
import ambit2.groupcontribution.descriptors.CDKDescriptorManager;
import ambit2.groupcontribution.transformations.IValueTransformation;

public class DescriptorCorrectionFactor implements ICorrectionFactor
{
	private CDKDescriptorInfo cdkDescrInfo = null;
	private CDKDescriptorManager cdkDescrMan = null;
	private double contribution = 0.0;
	
	public DescriptorCorrectionFactor (CDKDescriptorInfo cdkDescrInfo) {
		this.cdkDescrInfo = cdkDescrInfo;
	}
	
	public DescriptorCorrectionFactor (CDKDescriptorInfo cdkDescrInfo, CDKDescriptorManager cdkDescrMan) {
		this.cdkDescrInfo = cdkDescrInfo;
		this.cdkDescrMan = cdkDescrMan; 
	}
	
	@Override
	public IValueTransformation getValueTransformation() {
		if (cdkDescrInfo != null)
			return cdkDescrInfo.valueTranform; 
		return null;
	}

	@Override
	public void setValueTransformation(IValueTransformation transformation) {
		//Do nothing. It is set via cdkDescrInfo field.
	}

	@Override
	public double getContribution() {
		return contribution;
	}

	@Override
	public void setContribution(double contribution) {
		this.contribution = contribution;		
	}

	@Override
	public String getDesignation() {
		if (cdkDescrInfo != null)
			return cdkDescrInfo.fullString; 
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
