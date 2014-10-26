package ambit2.sln;

import java.util.ArrayList;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IBond;

public class SLNBondExpression 
{

	public ArrayList<SLNExpressionToken> tokens = new ArrayList<SLNExpressionToken>(); 

	public boolean matches(IBond atom) 
	{
		//TODO
		return true;
	}

	boolean getArgument(SLNExpressionToken tok, SLNBond bond)
    {	
		switch (tok.type)
		{
		case SLNConst.B_TYPE_1:
			if (bond.getOrder() == IBond.Order.SINGLE)
				return(true);   
			else 
				return(false);
		case SLNConst.B_TYPE_2:
			if (bond.getOrder() == IBond.Order.DOUBLE)
				return(true);   
			else 
				return(false);
		case SLNConst.B_TYPE_3:
			if (bond.getOrder() == IBond.Order.TRIPLE)
				return(true);   
			else 
				return(false);	
		case SLNConst.B_TYPE_aromatic:
			if (bond.getFlag(CDKConstants.ISAROMATIC));
				return(true);	
		case SLNConst.B_STEREO_C:
			//TODO
    	}
    	return true; //by default
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
