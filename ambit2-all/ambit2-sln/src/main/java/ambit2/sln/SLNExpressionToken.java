package ambit2.sln;


public class SLNExpressionToken 
{
	public int type;
	public int param = 0;
	public double doubleParam = 0;
	public String attrName = null;
	public String attrValue = null;
	
	public SLNExpressionToken(int type, int param, String attrName, String attrValue)
	{
		this.type = type;
		this.param = param;
		this.attrName = attrName;
		this.attrValue = attrValue;
	}
	
	public SLNExpressionToken(int type, double doubleParam, String attrName, String attrValue)
	{
		this.type = type;
		this.doubleParam = doubleParam;
		this.attrName = attrName;
		this.attrValue = attrValue;
	}
	
	public boolean isLogicalOperation()
	{
		if(type >= SLNConst.LO)
			return(true);
		return(false);
	}
	
	public int getLogOperation()
	{
		return(type - SLNConst.LO);
	}
	
}
