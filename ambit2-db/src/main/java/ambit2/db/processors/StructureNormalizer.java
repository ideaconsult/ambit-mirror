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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.core.processors.structure.key.InchiPropertyKey;
import ambit2.core.processors.structure.key.SmilesKey;
import ambit2.hashcode.HashcodeKey;

public class StructureNormalizer extends DefaultAmbitProcessor<IStructureRecord, IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -218665330663424435L;
	protected MoleculeReader molReader;
	protected HashcodeKey hashcode;	
	protected SmilesKey smilesKey;
	protected InchiPropertyKey inchiKey;
	public StructureNormalizer() {
		molReader = new MoleculeReader();
		hashcode = new HashcodeKey();
		smilesKey = new SmilesKey();
		inchiKey = new InchiPropertyKey();
	}
	public IStructureRecord process(IStructureRecord structure)
			throws AmbitException {
		IAtomContainer molecule = molReader.process(structure);
		structure.addProperties(molecule.getProperties());
		
		try {
			structure.setHash(hashcode.process(molecule));
		} catch (Exception x) {
			logger.warn(x);
			structure.setHash(0L);
		} finally {
		}
		
		try {
			structure.setSmiles(smilesKey.process(molecule));
			if ("".equals(structure.getSmiles())) structure.setSmiles(null);
		} catch (Exception x) {
			structure.setSmiles(null);
		}		
		
		try {
			structure.setInchi(inchiKey.process(structure));
			if ("".equals(structure.getInchi())) structure.setInchi(null);
		} catch (Exception x) {
			structure.setInchi(null);
		}				

		try {
			MFAnalyser mf = new MFAnalyser(molecule);
			structure.setFormula(mf.getMolecularFormula());
			if ("".equals(structure.getFormula())) structure.setFormula(null);
		} catch (Exception x) {
			structure.setFormula(null);
		}		
		
		return structure;
	}

}
