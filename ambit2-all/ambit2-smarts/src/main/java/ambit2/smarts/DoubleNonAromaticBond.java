package ambit2.smarts;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

public class DoubleNonAromaticBond extends SMARTSBond
{
	private static final long serialVersionUID = -93413322217784352L;
	
	public DoubleNonAromaticBond() {
    }
	
	public boolean matches(IBond bond) 
	{	
		if (bond.getOrder() == IBond.Order.DOUBLE)
			if (!bond.getFlag(CDKConstants.ISAROMATIC))
				return(true);
					
		return false;
    };

}

