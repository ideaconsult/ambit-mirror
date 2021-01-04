package ambit2.sln.dictionary;

import ambit2.sln.SLNConst;

public class MarkushExpressionToken 
{
	public int type;
	public int slnContainerIndex = -1;
	
	public MarkushExpressionToken(int type)
	{	
		this.type = type;
	}
	
	public MarkushExpressionToken(int type, int slnContainerIndex)
	{	
		this.type = type;
		this.slnContainerIndex = slnContainerIndex;
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

	/*
	private String logOperationToString()
	{
		int lo = getLogOperation();
		switch (lo)
		{
		case SLNConst.LO_NOT:
			return "!";			
		case SLNConst.LO_AND:
			return "&";
		case SLNConst.LO_OR:
			return "|";
		case SLNConst.LO_ANDLO:
			return ";";
		}
		return "*";
	}
	*/
	
}
