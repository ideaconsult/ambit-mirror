package ambit2.sln;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;

/**
 * Abstract sln atom.
 * 
 */

public class SLNAtom extends org.openscience.cdk.PseudoAtom implements IQueryAtom
{
	public SLNAtom()
	{
	}

	int atomType = 0;
	String atomName = null; 
	SLNAtomExpression atomExpression = null;
	
	
	public boolean matches(IAtom atom) 
	{
		if (atomType < 0) // Atom expression
		{	
			return atomExpression.matches(atom);
		}
			
		if (atomType == 0) //any atom
			return true;
		
		if (atomType < SLNConst.GlobDictOffseet) //atomic symbol
		{
			//TODO
			return false;
		}
		
		if (atomType < SLNConst.LocalDictOffseet) //It is a global dictionary definition
		{
			//TODO
			return false;
		}
		
		//It is a local dictionary definition
		//TODO
		return false;
	}
}
