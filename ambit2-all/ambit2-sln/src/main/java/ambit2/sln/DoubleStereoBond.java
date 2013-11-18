package ambit2.sln;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

public class DoubleStereoBond extends org.openscience.cdk.Bond implements IQueryBond
{
	private static final long serialVersionUID = -87456804683457479L;
	public int stereoParameter = 0;
	
	public DoubleStereoBond() {
    }
	
	public boolean matches(IBond bond) 
	{		 
		if (bond.getOrder() != IBond.Order.DOUBLE)
			return(false); 
		return true;
	}
}
