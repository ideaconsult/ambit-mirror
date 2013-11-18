package ambit2.sln;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;

/**
 * This matcher any aromatic atom. This assumes that aromaticity in the molecule
 * has been perceived.
 */

public class AromaticAtom extends SLNAtom
{
	private static final long serialVersionUID = -1098357468892669829L;
	/**
	 * Creates a new instance
	 */
	public AromaticAtom()
	{
		setFlag(CDKConstants.ISAROMATIC, true);
	}

	public boolean matches(IAtom atom) 
	{
		return atom.getFlag(CDKConstants.ISAROMATIC); 
	}

	public String toString()
	{
		return "AromaticAtom()";
	}
}
