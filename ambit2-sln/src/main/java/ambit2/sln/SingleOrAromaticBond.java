package ambit2.sln;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

public class SingleOrAromaticBond extends org.openscience.cdk.Bond implements IQueryBond
{
private static final long serialVersionUID = -99936889077824119L;
	
	public SingleOrAromaticBond() {
    }
	
	public boolean matches(IBond bond) 
	{		 
		if (bond.getOrder() == IBond.Order.SINGLE)
			return(true);
		if (bond.getFlag(CDKConstants.ISAROMATIC))
			return(true);		
		return false;
    };

}
