package ambit2.sln;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

public class SingleBondAromaticityNotSpecified extends org.openscience.cdk.Bond implements IQueryBond
{
	private static final long serialVersionUID = -23551788494267482L;

	public SingleBondAromaticityNotSpecified()
	{
	}

	public boolean matches(IBond bond) 
	{	
		if (bond.getOrder() == IBond.Order.SINGLE)
			return(true);

		return false;
	};
}
