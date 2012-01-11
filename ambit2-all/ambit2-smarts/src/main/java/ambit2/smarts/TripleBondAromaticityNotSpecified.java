package ambit2.smarts;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

public class TripleBondAromaticityNotSpecified extends SMARTSBond
{
	private static final long serialVersionUID = -433358060406382L;
	
	public TripleBondAromaticityNotSpecified() {
    }
	
	public boolean matches(IBond bond) 
	{	
		if (bond.getOrder() == IBond.Order.TRIPLE)
			return(true);
					
		return false;
    };

}
