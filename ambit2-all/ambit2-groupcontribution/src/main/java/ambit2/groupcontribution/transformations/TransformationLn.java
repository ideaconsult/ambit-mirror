package ambit2.groupcontribution.transformations;

import java.util.ArrayList;
import java.util.List;

public class TransformationLn implements IValueTransformation 
{
	Double shift = null;
	
	public TransformationLn() {
	}
	
	public TransformationLn(double shift) {
		this.shift = shift;
	}
	
	@Override
	public String getTransformationName() {
		if (shift != null)
			return "LN(" + shift + ")";
		else
			return "LN";
	}

	@Override
	public List<Double> getParameters() {
		if (shift != null)
		{
			List<Double> params = new ArrayList<Double>(); 
			params.add(shift);
			return params;
		}
		else
			return null;
	}
	
	@Override
	public List<String> getParameterNames() {
		if (shift != null)
		{
			List<String> paramNames = new ArrayList<String>(); 
			paramNames.add("shift");
			return paramNames;
		}
		else
			return null;
	}

	@Override
	public double transform(double value) throws Exception {
		if (shift != null)
			return Math.log(value + shift);
		else
			return Math.log(value);
	}
	
	public void setShift(double shift) {
		this.shift = shift;
	}

	@Override
	public IValueTransformation getInverseTransformation() {
		// TODO Auto-generated method stub
		return null;
	}
}
