package ambit2.groupcontribution.transformations;

import java.util.ArrayList;
import java.util.List;

public class TransformationLin implements IValueTransformation 
{
	double a = 1.0;
	double b = 0.0;
	
	public TransformationLin() {
	}
	
	public TransformationLin(double a, double b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public String getTransformationName() {
		return ("Lin(" + a + "," + b + ")");
	}

	@Override
	public List<Double> getParameters() {
		List<Double> params = new ArrayList<Double>(); 
		params.add(a);
		params.add(b);
		return params;
	}

	@Override
	public List<String> getParameterNames() {
		List<String> paramNames = new ArrayList<String>(); 
		paramNames.add("a");
		paramNames.add("b");
		return paramNames;
	}

	@Override
	public IValueTransformation getInverseTransformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double transform(double value) throws Exception {
		return a * value + b;
	}

}
