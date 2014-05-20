package ambit2.sln;


import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

public class SLNBond extends SMARTSBond
{
	private static final long serialVersionUID = 456218569809086068L;
	
	int bondType = 0;
	SLNBondExpression bondExpression = null;
	
	public boolean matches(IBond bond) 
	{
		//TODO
		return false;
	}
}
