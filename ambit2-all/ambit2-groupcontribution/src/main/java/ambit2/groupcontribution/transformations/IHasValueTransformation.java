package ambit2.groupcontribution.transformations;

public interface IHasValueTransformation 
{
	public IValueTransformation getValueTransformation();
	public void setValueTransformation(IValueTransformation transformation);
}
