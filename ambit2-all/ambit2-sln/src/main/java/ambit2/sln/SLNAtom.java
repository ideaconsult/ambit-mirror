package ambit2.sln;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;

/**
 * Abstract sln atom.
 * 
 */

public class SLNAtom extends org.openscience.cdk.PseudoAtom implements
IQueryAtom
{

	public SLNAtom()
	{
	}

	public boolean matches(IAtom atom) {
		return false;
	}
}
