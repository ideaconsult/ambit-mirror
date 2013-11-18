package ambit2.sln;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

public class DoubleNonAromaticBond extends org.openscience.cdk.Bond implements IQueryBond
{
	private static final long serialVersionUID = -9341456787784352L;

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
