package ambit2.sln;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;

/**
 * This matcher any non-aromatic atom. This assumes that aromaticity in the
 * molecule has been perceived.
 * 
 */

public class AliphaticAtom extends SLNAtom
{
	private static final long serialVersionUID = 2515945498714205622L;

	/**
	 * Creates a new instance
	 */
	public AliphaticAtom() {
		setFlag(CDKConstants.ISALIPHATIC, true);
	}

	public boolean matches(IAtom atom) 
	{
		return !atom.getFlag(CDKConstants.ISAROMATIC); 
	}

	public String toString()
	{
		return "AliphaticAtom()";
	}
}
