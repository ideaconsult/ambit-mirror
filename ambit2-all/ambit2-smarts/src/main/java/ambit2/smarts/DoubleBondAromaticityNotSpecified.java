package ambit2.smarts;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

public class DoubleBondAromaticityNotSpecified extends SMARTSBond
{
	private static final long serialVersionUID = -9341431640874352L;
	
	public DoubleBondAromaticityNotSpecified() {
    }
	
	public boolean matches(IBond bond) 
	{	
		if (bond.getOrder() == IBond.Order.DOUBLE)
			return(true);
					
		return false;
    };

}
