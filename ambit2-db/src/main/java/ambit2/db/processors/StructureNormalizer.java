/* StructurereNormalizer.java
 * Author: nina
 * Date: Apr 2, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.db.processors;

import net.sf.jniinchi.INCHI_RET;

import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.processors.structure.InchiProcessor;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.core.processors.structure.StructureTypeProcessor;
import ambit2.core.processors.structure.key.InchiPropertyKey;
import ambit2.core.processors.structure.key.SmilesKey;

public class StructureNormalizer extends DefaultAmbitProcessor<IStructureRecord, IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -218665330663424435L;
	protected MoleculeReader molReader;
	protected SmilesKey smilesKey;
	protected InchiPropertyKey inchiKey;
	protected StructureTypeProcessor strucType;
	protected InchiProcessor inchiProcessor;

	
	public StructureNormalizer() {
		molReader = new MoleculeReader();
		smilesKey = new SmilesKey();
		inchiKey = new InchiPropertyKey();
		strucType = new StructureTypeProcessor();
	}
	public IStructureRecord process(IStructureRecord structure)
			throws AmbitException {
		IAtomContainer molecule = molReader.process(structure);
		
		if ((molecule == null) || (molecule.getAtomCount()==0)) structure.setType(STRUC_TYPE.NA);
		else {
			structure.setType(strucType.process(molecule));

		}
		if ((molecule != null) && (molecule.getProperties()!=null))
			structure.addProperties(molecule.getProperties());

		try {
			structure.setSmiles(smilesKey.process(molecule));
			if ("".equals(structure.getSmiles())) structure.setSmiles(null);
		} catch (Exception x) {
			structure.setSmiles(null);
		}		

		try {
			if (structure.getInchi()== null) {
				String inchi = null; 
				String key = null; 
				if (molecule.getAtomCount()==0){
					inchi = inchiKey.process(structure);
				} else
				try {
					if (inchiProcessor==null) inchiProcessor = new InchiProcessor();
					InChIGenerator gen  = inchiProcessor.process(molecule);
					if (INCHI_RET.OKAY.equals(gen.getReturnStatus()) || INCHI_RET.WARNING.equals(gen.getReturnStatus())) {
						inchi = gen.getInchi();
						key =  gen.getInchiKey(); 
					} else {
						inchi=null;
						key = String.format("ERROR:%s:%s", gen.getReturnStatus(),gen.getMessage());
					}
				} catch (Exception x) {
					inchi=null;
					key = null;
				}
				if ("".equals(inchi)) inchi = null;
				structure.setInchi(inchi);
				structure.setInchiKey(key);
			}
		} catch (Exception x) {
			structure.setInchi(null);
			structure.setInchiKey("ERROR:"+x.getMessage());
		}				

		try {
			IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(molecule);

			structure.setFormula(formula==null?null:MolecularFormulaManipulator.getString(formula));
		} catch (Exception x) {
			structure.setFormula(null);
		}		
		
		return structure;
	}

}
