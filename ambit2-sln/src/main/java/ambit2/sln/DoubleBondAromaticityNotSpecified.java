package ambit2.sln;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

public class DoubleBondAromaticityNotSpecified extends org.openscience.cdk.Bond implements IQueryBond
{
	private static final long serialVersionUID = -93421123456904119L;
	
	public DoubleBondAromaticityNotSpecified() 
	{
    }
	
	public boolean matches(IBond bond) 
	{	
		if (bond.getOrder() == IBond.Order.DOUBLE)
			return(true);
					
		return false;
    };

}
