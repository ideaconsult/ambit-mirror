package ambit2.sln;

import java.util.ArrayList;
import org.openscience.cdk.interfaces.IAtom;

import ambit2.smarts.SmartsExpressionToken;


public class SLNAtomExpression 
{
	public int atomID = -1;
	public ArrayList<SLNExpressionToken> tokens = new ArrayList<SLNExpressionToken>(); 
	
	public boolean matches(IAtom atom) 
	{
		//TODO
		return false;
	}
	
	
	String tokenToString(SLNExpressionToken tok)
    {
		//TODO
		return "";
    }
	
	public String toString() 
    {
    	StringBuffer sb = new StringBuffer();
    	sb.append("[");
    	for (int i=0; i < tokens.size(); i++)
    		sb.append(tokenToString(tokens.get(i)));
    	sb.append("]");
    	return sb.toString();
    }
}
