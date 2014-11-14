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

package ambit2.core.processors;

import java.io.StringWriter;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.sf.jniinchi.INCHI_RET;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.FixBondOrdersTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.core.io.dx.DXParser;
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
	protected transient MoleculeReader molReader;
	protected transient SmilesKey smilesKey;
	protected transient InchiPropertyKey inchiKey;
	protected transient StructureTypeProcessor strucType;
	protected transient InchiProcessor inchiProcessor;
	protected transient FixBondOrdersTool fbt = new FixBondOrdersTool();
	protected transient Structure2DBuilder builder  = null;
	protected DXParser dxParser = null;
	
	public StructureNormalizer() {
		molReader = new MoleculeReader();
		smilesKey = new SmilesKey();
		inchiKey = new InchiPropertyKey();
		strucType = new StructureTypeProcessor();
	}
	public IStructureRecord process(IStructureRecord structure)	throws AmbitException {
		if ((structure.getFormat()!=null) && MOL_TYPE.NANO.name().equals(structure.getFormat())) {
			try { //nanomaterial
				//NanoCMLReader already have set all the IStructureRecord fields
				return structure;
				/*
				Class clazz = FileInputState.class.getClassLoader().loadClass("net.idea.ambit2.nano.NanoStructureNormalizer");
				Method method = clazz.getMethod("normalizeNano", IStructureRecord.class);
				return (IStructureRecord) method.invoke(null, structure);
				*/
			} catch (Exception x) {
	  		   if (x instanceof AmbitException) throw (AmbitException)x;
	  		   else throw new AmbitException(x);
			}
		} //else process as chemical	
		
		IAtomContainer molecule = molReader.process(structure);
		
		if ((molecule != null) && (molecule.getProperties()!=null))
			structure.addProperties(molecule.getProperties());
		
		if ((molecule == null) || (molecule.getAtomCount()==0)) structure.setType(STRUC_TYPE.NA);
		else {
			structure.setType(strucType.process(molecule));
			if (build2D) try {
				IAtomContainer newmol = build2d(molecule,structure.getType());
				if (newmol != null) {
					molecule = newmol;
					StringWriter w = new StringWriter();
					ambit2.core.io.MDLWriter writer = new ambit2.core.io.MDLWriter(w);
					try { 
						writer.writeMolecule(molecule);
						writer.close();
						structure.setType(strucType.process(molecule));
						structure.setContent(w.toString());
						structure.setFormat(MOL_TYPE.SDF.name());
					} catch (Exception x) {
						
					}
				}
			} catch (Exception x) {}
		}
		
		try {
			if ((molecule != null) && (molecule.getAtomCount()>0)) {
				structure.setSmiles(smilesKey.process(molecule));
				if ("".equals(structure.getSmiles())) structure.setSmiles(null);
			}
		} catch (Exception x) {
			structure.setSmiles(null);
		}		

		try {
			if (structure.getInchi()== null) {
				String inchi = null; 
				String key = null; 
				if ((molecule==null) || (molecule.getAtomCount()==0)) {
					inchi = inchiKey.process(structure);
				} else
				try {
					if (inchiProcessor==null) inchiProcessor = new InchiProcessor();
					boolean kekulize = false;
					for (IBond bond:molecule.bonds()) if (bond.getFlag(CDKConstants.ISAROMATIC)) {
						kekulize = true; break;
					}
					IAtomContainer kekulized = (IAtomContainer) molecule;
					if (kekulize) try {
						//inchi can't process aromatic bonds...
						kekulized = (IMolecule) molecule.clone();
						//and kekulizer needs atom typing
						AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(kekulized); 
						kekulized = fbt.kekuliseAromaticRings((IMolecule)kekulized);
						for (IBond bond:kekulized.bonds()) bond.setFlag(CDKConstants.ISAROMATIC, false); 	
					} catch (Exception x) { logger.log(Level.FINE,x.getMessage(),x);; }
					InChIGenerator gen  = inchiProcessor.process(kekulized);
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
		if ((molecule != null) && (molecule.getAtomCount()>0)) 
		try {
			IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(molecule);
			structure.setFormula(formula==null?null:MolecularFormulaManipulator.getString(formula));
		} catch (Exception x) {
			structure.setFormula(null);
		}		
		
		if (DXParser.hasDxProperties(structure)) {
			if (dxParser==null) dxParser = new DXParser();
			structure = dxParser.process(structure);
		}
		return structure;
	}
	
	protected boolean build2D = false;
	
	public boolean isBuild2D() {
		return build2D;
	}


	public void setBuild2D(boolean build2d) {
		build2D = build2d;
	}

	protected IAtomContainer build2d(IAtomContainer molecule,STRUC_TYPE structype) throws AmbitException {
		if (!STRUC_TYPE.D1.equals(structype)) return null;
		if (builder == null) builder = new Structure2DBuilder();
        return builder.process(molecule);
	}
}
