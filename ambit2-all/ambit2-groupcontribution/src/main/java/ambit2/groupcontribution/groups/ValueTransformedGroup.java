package ambit2.groupcontribution.groups;

import ambit2.groupcontribution.transformations.IHasValueTransformation;
import ambit2.groupcontribution.transformations.IValueTransformation;

public abstract class ValueTransformedGroup implements IGroup, IHasValueTransformation
{
	IValueTransformation transformation = null;
	protected double contribution = 0.0;
	
	@Override
	public double getContribution() {
		return contribution;
	}
	
	@Override
	public void setContribution(double contribution) {
		this.contribution = contribution;
	}
	
	public IValueTransformation getValueTransformation()
	{
		return transformation;
	}
	
	public void setValueTransformation(IValueTransformation transformation)
	{
		this.transformation  = transformation;
	}
}
