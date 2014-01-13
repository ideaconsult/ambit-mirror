package ambit2.sln;


public class SLNExpressionToken 
{
	public int type;
	public int param;
	
	public SLNExpressionToken(int nType, int nParam)
	{
		type = nType;
		param = nParam;
		
		String userDefAttrName = null;
		String userDefAttrValue = null;
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
