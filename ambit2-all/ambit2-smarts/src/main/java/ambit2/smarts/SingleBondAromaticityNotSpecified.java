package ambit2.smarts;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

public class SingleBondAromaticityNotSpecified extends SMARTSBond
{
	private static final long serialVersionUID = -43451788424567482L;
	
	public SingleBondAromaticityNotSpecified() {
    }
	
	public boolean matches(IBond bond) 
	{	
		if (bond.getOrder() == IBond.Order.SINGLE)
			return(true);
					
		return false;
    };

}