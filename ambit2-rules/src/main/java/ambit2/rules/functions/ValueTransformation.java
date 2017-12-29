package ambit2.rules.functions;

public class ValueTransformation 
{
	public static enum Type {
		LINEAR, NONE
	}
	
	public Type type = Type.NONE;
	public double params[] = null;
	
	public double apply(double value)
	{
		switch (type)
		{
		case NONE:
			return value;
		case LINEAR:
			return params[0]*value + params[1];
		}
		return 0.0;
	}
}
