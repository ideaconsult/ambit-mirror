package ambit2.sln;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

public class SingleNonAromaticBond extends org.openscience.cdk.Bond implements IQueryBond
{
	private static final long serialVersionUID = -93421665754904119L;
	
	public SingleNonAromaticBond() {
    }
	
	public boolean matches(IBond bond) 
	{	
		if (bond.getOrder() == IBond.Order.SINGLE)
			if (!bond.getFlag(CDKConstants.ISAROMATIC))
				return(true);
					
		return false;
    };

}