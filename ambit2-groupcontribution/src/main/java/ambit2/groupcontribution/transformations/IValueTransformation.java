package ambit2.groupcontribution.transformations;

import java.util.List;

public interface IValueTransformation 
{
	public String getTransformationName();
	public List<Double> getParameters();
	public List<String> getParameterNames();
	public double transform(double value) throws Exception;
}
