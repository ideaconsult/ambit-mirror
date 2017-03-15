package ambit2.smarts.smirks;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

public class SmartsMatch 
{
	public static enum MatchMode {
		GENERIC, EXEACT
	}
	
	public MatchMode mode = MatchMode.GENERIC;
	
	public boolean match(SMARTSAtom qa, IAtom ta)
	{
		//TODO
		return true;
	}
	
	public boolean match(SMARTSBond qb, IBond tb)
	{
		//TODO
		return true;
	}
}
