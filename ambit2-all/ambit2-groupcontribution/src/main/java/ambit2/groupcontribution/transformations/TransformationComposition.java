package ambit2.groupcontribution.transformations;

import java.util.List;

public class TransformationComposition implements IValueTransformation 
{
	public static final String composeSeparator = ">>";
	
	IValueTransformation transformations[] = null;
	
	public TransformationComposition(IValueTransformation transformations[])
	{
		this.transformations = transformations;
	}
	
	@Override
	public String getTransformationName() {
		StringBuffer sb = new StringBuffer(); 
		for (int i = 0; i < transformations.length; i++)
		{
			sb.append(transformations[i].getTransformationName());
			if (i < (transformations.length-1) )
				sb.append(composeSeparator);
		}
		return sb.toString();
	}

	@Override
	public List<Double> getParameters() {
		return null;
	}

	@Override
	public List<String> getParameterNames() {
		return null;
	}

	@Override
	public double transform(double value) throws Exception {
		double resValue = value;
		for (int i = 0; i < transformations.length; i++)
		{
			resValue = transformations[i].transform(resValue);
		}
		return resValue;
	}

	@Override
	public IValueTransformation getInverseTransformation() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
