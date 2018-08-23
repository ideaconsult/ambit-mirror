package ambit2.groupcontribution.groups;

import ambit2.groupcontribution.transformations.IHasValueTransformation;
import ambit2.groupcontribution.transformations.IValueTransformation;

public abstract class ValueTransformedGroup implements IGroup, IHasValueTransformation
{
	IValueTransformation transformation = null;
	
	public IValueTransformation getValueTransformation()
	{
		return transformation;
	}
	
	public void setValueTransformation(IValueTransformation transformation)
	{
		this.transformation  = transformation;
	}
}
