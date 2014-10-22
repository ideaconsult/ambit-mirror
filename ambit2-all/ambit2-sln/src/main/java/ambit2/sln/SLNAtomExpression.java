package ambit2.sln;

import java.util.ArrayList;
import org.openscience.cdk.interfaces.IAtom;




public class SLNAtomExpression 
{
	public int atomID = -1;
	public ArrayList<SLNExpressionToken> tokens = new ArrayList<SLNExpressionToken>(); 
	
	public boolean matches(IAtom atom) 
	{
		SLNLogicalExpression sle = new SLNLogicalExpression();
		for (int i = 0; i< tokens.size(); i++)
		{
			SLNExpressionToken tok = tokens.get(i);
			if (tok.type < SLNConst.LO)
			{	
				sle.addArgument(getArgument(tok, atom));
			}	
			else
				sle.addLogOperation(tok.type - SLNConst.LO);
		}
		return (sle.getValue()); 
		
	}
	
	boolean getArgument(SLNExpressionToken tok, IAtom atom)
	{
		switch (tok.type)
		{
		case SLNConst.QB_ATTR_hac:  // QB --> QA to be changed by Elena!!
			if (tok.param == atom.getFormalNeighbourCount())
    			return(true);
    		else	
    			return(false);
			
			//TODO
		}
		return true; //by default
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
