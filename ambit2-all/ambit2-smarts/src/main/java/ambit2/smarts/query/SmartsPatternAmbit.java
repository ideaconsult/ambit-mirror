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

import java.util.logging.Level;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.core.data.MoleculeTools;
import ambit2.smarts.SmartsManager;
import ambit2.smarts.processors.SMARTSPropertiesReader;

/**
 * Encapsulates Ambit SMARTS parser.
 * @author nina
 *
 */
public class SmartsPatternAmbit extends AbstractSmartsPattern<IAtomContainer> {
	protected transient SmartsManager sman;
	protected transient SMARTSPropertiesReader reader = new SMARTSPropertiesReader();
	protected boolean useCDKIsomorphism = true;
	
	
	public boolean isUseCDKIsomorphism() {
		return sman==null?useCDKIsomorphism:sman.isFlagUseCDKIsomorphismTester();
	}

	public void setUseCDKIsomorphism(boolean useCDKIsomorphism) {
		this.useCDKIsomorphism = useCDKIsomorphism;
		if (sman!=null) {
			sman.setUseCDKIsomorphismTester(useCDKIsomorphism);
		} 
		/*
		else {
			sman = new SmartsManager();
			sman.setUseCDKIsomorphismTester(useCDKIsomorphism);
		}
		*/
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 4400701166436805492L;

	public SmartsPatternAmbit() throws SMARTSException  {
		this(SilentChemObjectBuilder.getInstance());
	}
	public SmartsPatternAmbit(IChemObjectBuilder builder) throws SMARTSException  {
		this("C",builder);

	}
	public SmartsPatternAmbit(String smarts) throws SMARTSException {
		this(smarts,SilentChemObjectBuilder.getInstance());
	}
	public SmartsPatternAmbit(String smarts,IChemObjectBuilder builder) throws SMARTSException {
		this(smarts,false,builder);
	}	
	public SmartsPatternAmbit(String smarts,boolean negate,IChemObjectBuilder builder) throws SMARTSException {
		super();
		sman = new SmartsManager(builder);	
		sman.setUseCDKIsomorphismTester(useCDKIsomorphism);
		setSmarts(smarts);
		setNegate(negate);

	}	
	public IQueryAtomContainer getQuery() {
		return sman.getQueryContaner();
	}
	public IAtomContainer getObjectToVerify(IAtomContainer mol) {
		return mol;
	}
	public IAtomContainer getMatchingStructure(IAtomContainer mol) throws SMARTSException {
		IAtomContainerSet set;
		try {
			set = sman.getAllIsomorphismMappings(mol);
		} catch (Exception x) {
			throw new SMARTSException(x);
		}
		if (set==null) return null;
		if (set.getAtomContainerCount()==0) return null;
		if (set.getAtomContainerCount()==1) return set.getAtomContainer(0);
		//a hack before refactoring code to use set for selections
		IAtomContainer match = MoleculeTools.newAtomContainer(SilentChemObjectBuilder.getInstance());
		for (int i=0; i < set.getAtomContainerCount();i++)
			match.add(set.getAtomContainer(i));
		return match;

	}

	public int hasSMARTSPattern(IAtomContainer mol) throws SMARTSException {
		/*
		 * setSmartsDataForTarget(false)   if properties are read from db
		 */
		if (sman == null) {
			throw new SMARTSException("Smarts parser not initialized!");
		}		
		try {
			
			sman.setSmartsDataForTarget(reader.process((IAtomContainer)mol) == null);
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
			sman.setSmartsDataForTarget(true);
		}
		

		try {
			if (sman.searchIn(mol)) {
				return 1; 
			} else {
				return 0;
			}
		} catch (Exception x) {
			throw new SMARTSException(x);
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
		/*
		if (sman == null) {
			sman = new SmartsManager();
			sman.setUseCDKIsomorphismTester(useCDKIsomorphism);
		}
		*/
		sman.setQuery(smarts);		
		if (!sman.getErrors().equals("")) {
			String errors = sman.getErrors();
			sman = null;
			throw new SMARTSException(errors);
		} 	
	}
	public void useMOEvPrimitive(boolean flag) throws UnsupportedOperationException {
		/*
		if (sman ==null) {
			sman = new SmartsManager();
			sman.setUseCDKIsomorphismTester(useCDKIsomorphism);
		}
		*/
		sman.useMOEvPrimitive(flag);
	}

}




