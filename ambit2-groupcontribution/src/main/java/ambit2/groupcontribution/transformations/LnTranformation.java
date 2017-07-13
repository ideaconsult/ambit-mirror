package ambit2.groupcontribution.transformations;

import java.util.List;

public class LnTranformation implements IValueTransformation 
{

	@Override
	public String getTransformationName() {
		return "LN";
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
		return Math.log(value);
	}
}
