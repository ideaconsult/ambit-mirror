package ambit2.smarts;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class SingleNonAromaticBond extends SMARTSBond
{
	private static final long serialVersionUID = -9341634575490679L;
	
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
