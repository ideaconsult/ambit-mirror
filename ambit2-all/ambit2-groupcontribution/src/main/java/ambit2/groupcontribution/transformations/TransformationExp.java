package ambit2.groupcontribution.transformations;

import java.util.List;

public class TransformationExp implements IValueTransformation 
{

	@Override
	public String getTransformationName() {
		return "EXP";
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
		return Math.exp(value);
	}

	@Override
	public IValueTransformation getInverseTransformation() {
		// TODO Auto-generated method stub
		return null;
	}

}
