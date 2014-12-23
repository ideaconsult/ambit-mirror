package ambit2.sln;

import java.util.ArrayList;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

public class SLNBondExpression 
{

	public ArrayList<SLNExpressionToken> tokens = new ArrayList<SLNExpressionToken>(); 

	public boolean matches(IBond bond) 
	{
		SLNLogicalExpression sle = new SLNLogicalExpression();
		for (int i = 0; i< tokens.size(); i++)
		{
			SLNExpressionToken tok = tokens.get(i);
			if (tok.type < SLNConst.LO)
			{	
				sle.addArgument(getArgument(tok, bond));
			}	
			else
				sle.addLogOperation(tok.type - SLNConst.LO);
		}
		return (sle.getValue()); 
	}

	boolean getArgument(SLNExpressionToken tok, IBond bond)
    {	
		switch (tok.type)
		{
		case SLNConst.QB_ATTR_type:
			return match_type(tok.param, bond);
							
		case SLNConst.B_STEREO_C:
			//TODO
			break;
    	}
    	return true; //by default
    }
	
	
	boolean match_type(int param,  IBond bond)
	{
		switch (param)
		{
		case SLNConst.B_TYPE_ANY:
			return true;
		
		case SLNConst.B_TYPE_1:
		{	
			if (bond.getOrder() == IBond.Order.SINGLE)
				return true;
			else
				return false;
		}
		
		//TODO
		
		}
		
		return false;
	}
	
	public String toString() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < tokens.size(); i++)
			sb.append(tokens.get(i).toString(false));
		sb.append("]");
		return sb.toString();	
	}

	public boolean isIdenticalTo(SLNBondExpression bondExpression)
	{
		// TODO 
		return true;
	}
}
