package ambit2.rules.functions;

public class ReciprocalFunction implements IFunction
{	
	@Override
	public String getName() {
		return "Reciprocal";
	}

	@Override
	public double getFunctionValue(double argument) {
		return 1 / argument;
	}

	@Override
	public double[] getParams() {
		return null;
	}

	@Override
	public void setParams(double[] params) {
	}
}