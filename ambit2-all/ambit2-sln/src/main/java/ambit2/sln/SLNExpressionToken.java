package ambit2.sln;


public class SLNExpressionToken 
{
	public int type;
	public int param;
	public String attrName = null;
	public String attrValue = null;
	
	public SLNExpressionToken(int type, int param, String attrName, String attrValue)
	{
		this.type = type;
		this.param = param;
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
