package ambit2.rules.functions;

public interface IFunction 
{
	public String getName();
	public double getFunctionValue(double argument);
	public double[] getParams();
	public void setParams(double params[]);
}
