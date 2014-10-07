package ambit2.sln;

import java.util.ArrayList;

import org.openscience.cdk.interfaces.IBond;

public class SLNBondExpression 
{

	public ArrayList<SLNExpressionToken> tokens = new ArrayList<SLNExpressionToken>(); 

	public boolean matches(IBond atom) 
	{
		//TODO
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
