package ambit2.rules.functions;

import java.util.List;

public class NestedFunctions implements IFunction
{
	protected List<IFunction> functions = null;
	
	@Override
	public String getName() {
		return "Nested functions";
	}

	@Override
	public double getFunctionValue(double argument) {
		double value = argument;
		for (int i=0; i < functions.size(); i++)
			value = functions.get(i).getFunctionValue(value);
		return value;
	}

	@Override
	public double[] getParams() {
		return null;
	}

	@Override
	public void setParams(double[] params) {
	}

	public List<IFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(List<IFunction> functions) {
		this.functions = functions;
	}

}
