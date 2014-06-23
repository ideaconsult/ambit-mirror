package ambit2.sln;

import java.util.ArrayList;
import org.openscience.cdk.interfaces.IAtom;


public class SLNAtomExpression 
{
	public int atomID = -1;
	public ArrayList<SLNExpressionToken> tokens = new ArrayList<SLNExpressionToken>(); 
	
	public boolean matches(IAtom atom) 
	{
		//TODO
		return false;
	}
	
	
	public String toString() 
    {
    	StringBuffer sb = new StringBuffer();
    	sb.append("[");
    	if (atomID >= 0)
    	{	
    		sb.append(atomID);
    		if (!tokens.isEmpty())
    			sb.append(":");
    	}
    	for (int i=0; i < tokens.size(); i++)
    		sb.append(tokens.get(i).toString(true));
    	sb.append("]");
    	return sb.toString();
    }
}
