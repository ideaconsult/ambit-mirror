package ambit2.groupcontribution.transformations;

import java.util.ArrayList;
import java.util.List;

public class TransformationPow implements IValueTransformation 
{
	double power = 1;
	
	public TransformationPow (double power) {
		this.power = power;
	}
	
	@Override
	public String getTransformationName() {
		return "POW(" + power + ")";
	}

	@Override
	public List<Double> getParameters() {
		List<Double> params = new ArrayList<Double>(); 
		params.add(power);
		return params;
	}

	@Override
	public List<String> getParameterNames() {
		List<String> paramNames = new ArrayList<String>(); 
		paramNames.add("power");
		return paramNames;
	}

	@Override
	public double transform(double value) throws Exception {
		return Math.pow(value, power);
	}

	@Override
	public IValueTransformation getInverseTransformation() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
