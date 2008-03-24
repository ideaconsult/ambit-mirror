/*
Copyright Ideaconsult Ltd. (C) 2005-2007  
Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/


package ambit2.query.smarts;

import java.util.List;

import joelib.molecule.JOEMol;
import joelib.smarts.JOESmartsPattern;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

/**
 * Encapsulates Joelib SMARTS parser.
 * JoeSmartsPattern is hidden, mainly to avoid some problems in XML serialization.
 * @author Nina Jeliazkova
 *
 */
public class SmartsPattern extends  AbstractSmartsPattern<JOEMol>{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2664576457218216126L;
	/**
     * 
     */
	protected transient JOESmartsPattern joeSmartsPatern = null;
	
	public SmartsPattern() throws SMARTSException {
		this("C");
	}
	public SmartsPattern(String smarts) throws SMARTSException {
		this(smarts,false);
	}
	public SmartsPattern(String smarts,boolean negate) throws SMARTSException {
		super();
		setSmarts(smarts);
		setNegate(negate);
	}
	public String getSmarts() {
		return smarts;
	}
	public void setSmarts(String smarts) throws SMARTSException {
		joeSmartsPatern = null;
		joeSmartsPatern = new JOESmartsPattern();
        if (!joeSmartsPatern.init(smarts))
        	throw new SMARTSException("Invalid SMARTS pattern " +  smarts );
    	this.smarts = smarts;
	}
    public int hasSMARTSPattern(JOEMol mol) throws SMARTSException
    {	
    	if (joeSmartsPatern == null) throw new SMARTSException("Undefined SMARTS pattern");
        joeSmartsPatern.match(mol);
        return joeSmartsPatern.numMatches();
    }
    public JOEMol getObjectToVerify(IAtomContainer mol) {
    	return Convertor.convert((IMolecule)mol);
    }
    public String getImplementationDetails() {
		if (joeSmartsPatern == null)
			return "Joelib SMARTS";
		else return joeSmartsPatern.getClass().getName();    	
    }
	public List<List<Integer>> getUniqueMatchingAtoms() throws SMARTSException {
		throw new SMARTSException("Not implemented!");
	}
}
