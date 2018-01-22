package ambit2.rules.functions;

public class LinearFunction implements IFunction
{
	protected double params[] =  {1.0, 0.0};
	
	public LinearFunction(double params[])
	{
		this.params = params;
	}
	
	@Override
	public String getName() {
		return "Linear";
	}

	@Override
	public double getFunctionValue(double argument) {
		return params[0]*argument + params[1];
	}

	@Override
	public double[] getParams() {
		return params;
	}

	@Override
	public void setParams(double[] params) {
		if (params != null)
			if (params.length == 2)
			{
				this.params[0] = params[0];
				this.params[1] = params[1];
			}
	}
	
}
