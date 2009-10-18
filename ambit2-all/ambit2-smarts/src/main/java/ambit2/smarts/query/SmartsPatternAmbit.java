/*
Copyright (C) 2005-2007  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.smarts.query;

import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

import ambit2.smarts.SmartsManager;
import ambit2.smarts.processors.SMARTSPropertiesReader;

/**
 * Encapsulates Ambit SMARTS parser.
 * @author nina
 *
 */
public class SmartsPatternAmbit extends AbstractSmartsPattern<IAtomContainer> {
	protected SmartsManager sman;
	protected SMARTSPropertiesReader reader = new SMARTSPropertiesReader();
	/**
	 * 
	 */
	private static final long serialVersionUID = 4400701166436805492L;

	public SmartsPatternAmbit() throws SMARTSException  {
		this("C");

	}
	public SmartsPatternAmbit(String smarts) throws SMARTSException {
		this(smarts,false);
	}	
	public SmartsPatternAmbit(String smarts,boolean negate) throws SMARTSException {
		sman = new SmartsManager();				
		setSmarts(smarts);
		setNegate(negate);

	}	
	public QueryAtomContainer getQuery() {
		return sman.getQueryContaner();
	}
	public IAtomContainer getObjectToVerify(IAtomContainer mol) {
		return mol;
	}
	public IAtomContainer getMatchingStructure(IAtomContainer mol)
			throws SMARTSException {
/*
		List bondMaps = sman.getBondMappings(object);
		boolean searchResult = man.searchIn(mol);
 
 */

		//List<IAtom> atoms =sman.getAtomMappings(mol);
		IAtomContainer c = NoNotificationChemObjectBuilder.getInstance().newAtomContainer();
		
		List<List<RMap>> bonds =sman.getBondMappings(mol);
		
		for (List<RMap> maps : bonds)
			for (RMap map : maps)
			c.addBond(mol.getBond(map.getId1()));
		
		//List<IAtom> atoms =sman.getAtomMappings(mol,sman.getQueryContaner()); -- replaced by Nick :-)
		List<IAtom> atoms =sman.getFirstPosAtomMappings(mol);
		for (IAtom atom: atoms)
			c.addAtom(atom);
			
		return c;
	}

	public int hasSMARTSPattern(IAtomContainer mol) throws SMARTSException {
		/*
		 * setSmartsDataForTarget(false)   if properties are read from db
		 */
		
		try {
			
			sman.setSmartsDataForTarget(reader.process((IAtomContainer)mol) == null);
		} catch (Exception x) {
			x.printStackTrace();
			sman.setSmartsDataForTarget(true);
		}
		
		if (sman == null) {
			throw new SMARTSException("Smarts parser not initialized!");
		}

		if (sman.searchIn(mol)) {
			return 1; 
		} else {
			return 0;
		}
		/*
		Vector<IAtom> hits = sman.getAtomMappings(object);
		if (hits == null) return 0;
		else return hits.size();
		*/
	}

	public String getImplementationDetails() {
		return "AMBIT smarts package";
	}
	@Override
	public void setSmarts(String smarts) throws SMARTSException {
		super.setSmarts(smarts);
		if (sman == null) {
			sman = new SmartsManager();
		}
		sman.setQuery(smarts);		
		if (!sman.getErrors().equals("")) {
			String errors = sman.getErrors();
			sman = null;
			throw new SMARTSException(errors);
		} 	
	}
	public void useMOEvPrimitive(boolean flag) throws UnsupportedOperationException {
		if (sman ==null) 
			sman = new SmartsManager();
		sman.useMOEvPrimitive(flag);
	}

}




