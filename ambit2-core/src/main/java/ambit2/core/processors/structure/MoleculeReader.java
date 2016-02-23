/* MoleculeReader.java
 * Author: Nina Jeliazkova
 * Date: Oct 7, 2015 
 * Revision: 03.0 
 * 
 * Copyright (C) 2005-2015  
 * 
 * Contact: www.ideconsult.net
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

package ambit2.core.processors.structure;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.processors.CASProcessor;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.data.MoleculeTools;
import ambit2.core.io.FileInputState;

public class MoleculeReader extends
		DefaultAmbitProcessor<IStructureRecord, IAtomContainer> {

	protected InChIGeneratorFactory inchiFactory = null;
	protected CASProcessor casTransformer = null;
	protected SmilesParser smiParser = null;
	protected CDKHydrogenAdder hadder = CDKHydrogenAdder
			.getInstance(SilentChemObjectBuilder.getInstance());
	protected boolean atomtypingonsmiles = false;
	protected boolean removeCDKTitle = true;
	public boolean isRemoveCDKTitle() {
		return removeCDKTitle;
	}

	public void setRemoveCDKTitle(boolean removeCDKTitle) {
		this.removeCDKTitle = removeCDKTitle;
	}

	public boolean isAtomtypingonsmiles() {
		return atomtypingonsmiles;
	}

	public void setAtomtypingonsmiles(boolean atomtypingonsmiles) {
		this.atomtypingonsmiles = atomtypingonsmiles;
	}
	public MoleculeReader() {
		this(false,true);
	}
	public MoleculeReader(boolean atomtypingonsmiles,boolean removeCDKTitle) {
		super();
		setAtomtypingonsmiles(atomtypingonsmiles);
		setRemoveCDKTitle(removeCDKTitle);
	}
	

	/**
     * 
     */
	private static final long serialVersionUID = 1811923574213153916L;

	public IAtomContainer process(IStructureRecord target)
			throws AmbitException {
		if (target.getContent() == null)
			return null;
		if (target.getFormat() == null)
			throw new AmbitException("Unknown format " + target.getFormat());
		MOL_TYPE format = MOL_TYPE.SDF;
		try {
			format = MOL_TYPE.valueOf(target.getFormat());
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		return handleFormat(format, target);
	}

	protected IAtomContainer handleFormat(MOL_TYPE format,
			IStructureRecord target) throws AmbitException {
		switch (format) {
		case SDF: {
			try {
				IAtomContainer ac = MoleculeTools.readMolfile(target
						.getContent());
				if ((ac != null) && (ac.getProperties() != null)) {
					Object title = ac.getProperty(CDKConstants.TITLE);
					if (title != null) {
						if (CASProcessor.isValidFormat(title.toString()))
							try {
								if (casTransformer == null)
									casTransformer = new CASProcessor();
								ac.setProperty(
										AmbitCONSTANTS.CASRN,
										casTransformer.process(title.toString()));
								ac.removeProperty(CDKConstants.TITLE);
							} catch (Exception x) {
							}
						if (removeCDKTitle) ac.removeProperty(CDKConstants.TITLE);
					}
					// CAS transformation was moved from MyIterating MDLReader
					Iterator props = ac.getProperties().entrySet().iterator();
					while (props.hasNext()) {
						Map.Entry entry = (Map.Entry) props.next();
						Object key = entry.getKey();
						Object value = entry.getValue();
						if (Property.test4CAS(key.toString().toLowerCase())) {
							if (casTransformer == null)
								casTransformer = new CASProcessor();
							ac.setProperty(key,
									casTransformer.process(value.toString()));
						}
					}

					/* !%&$* PubChem */
					Object sid = ac.getProperty("PUBCHEM_SUBSTANCE_ID");
					if (sid != null) {
						ac.setProperty("PUBCHEM_SID", sid);
						ac.removeProperty("PUBCHEM_SUBSTANCE_ID");
					}
					Object cid = ac.getProperty("PUBCHEM_COMPOUND_CID");
					if (cid != null) {
						ac.setProperty("PUBCHEM_CID", cid);
						ac.removeProperty("PUBCHEM_COMPOUND_CID");
					}

					Object synonyms = ac
							.getProperty("PUBCHEM_SUBSTANCE_SYNONYM");
					if (synonyms != null) {
						BufferedReader reader = new BufferedReader(
								new StringReader(synonyms.toString()));
						String line = null;
						while ((line = reader.readLine()) != null) {

							String type = "PUBCHEM Name";
							String value = line;
							if (value.startsWith("DSSTox_RID_")) {
								type = "DSSTox_RID";
								value = value.substring(11);
							} else if (value.startsWith("DSSTox_GSID_")) {
								type = "DSSTox_GSID";
								value = value.substring(12);
							} else if (value.startsWith("DSSTox_CID_")) {
								type = "DSSTox_CID";
								value = value.substring(11);
							} else if (value.startsWith("Tox21_")) {
								type = "Tox21";
								value = value.substring(6);
							} else if (value.startsWith("CAS-")) {
								type = "CASRN";
								value = value.substring(4);
							} else if (value.startsWith("NCGC")) {
								type = "NCGC";
							} else {
								value = value.toLowerCase();
							}
							ac.setProperty(type, value);
						}
						reader.close();
						ac.removeProperty("PUBCHEM_SUBSTANCE_SYNONYM");
					}
					ac.removeProperty(CDKConstants.REMARK);
				}

				if (ac != null) {
					if (MoleculeTools.repairBondOrder4(ac)) {
						// ok, all set
					} else {
						try {
							AtomContainerManipulator
									.percieveAtomTypesAndConfigureAtoms(ac);
						} catch (Exception x) {
							ac.setProperty("ERROR.atomtypes", String.format(
									"%s\t%s", x.getClass().getName(),
									x.getMessage()));
						}
						try {
							hadder.addImplicitHydrogens(ac);
						} catch (Exception x) {
							ac.setProperty("ERROR.implicith", String.format(
									"%s\t%s", x.getClass().getName(),
									x.getMessage()));
						}
					}
				}

				return ac;
			} catch (Exception x) {
				throw new AmbitException(x);
			}

		}

		case CML:
			try {
				IAtomContainer ac = MoleculeTools.readCMLMolecule(target
						.getContent());
				if ((ac != null) && (ac.getProperties() != null)) {
					Object title = ac.getProperty(CDKConstants.TITLE);
					if (title != null) {
						if (CASProcessor.isValidFormat(title.toString()))
							try {
								if (casTransformer == null)
									casTransformer = new CASProcessor();
								ac.setProperty(
										AmbitCONSTANTS.CASRN,
										casTransformer.process(title.toString()));
							} catch (Exception x) {
							}
						ac.removeProperty(CDKConstants.TITLE);
					}
					ac.removeProperty(CDKConstants.REMARK);
				}
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
				hadder.addImplicitHydrogens(ac);
				return ac;
			} catch (Exception x) {
				throw new AmbitException(x);
			}
		case INC:
			try {
				if (target.getContent().startsWith("InChI=")) {
					if (inchiFactory == null)
						inchiFactory = InChIGeneratorFactory.getInstance();
					InChIToStructure c = inchiFactory.getInChIToStructure(
							target.getContent(),
							SilentChemObjectBuilder.getInstance());
					return c.getAtomContainer();
				} else { // smiles
					if (smiParser == null)
						smiParser = new SmilesParser(
								SilentChemObjectBuilder.getInstance());
					IAtomContainer mol = smiParser.parseSmiles(target
							.getContent());
					// atom typing will drop aromatic flags ...
					if (atomtypingonsmiles) {
						AtomContainerManipulator
								.percieveAtomTypesAndConfigureAtoms(mol);
						for (IBond b : mol.bonds())
							if (b.isAromatic())
								for (IAtom a : b.atoms())
									a.setFlag(CDKConstants.ISAROMATIC, true);
					}
					return mol;
				}
			} catch (Exception x) {
				throw new AmbitException(x);
			}
		case NANO:
			try {
				Class clazz = FileInputState.class.getClassLoader().loadClass(
						"net.idea.ambit2.rest.nano.MoleculeNanoReader");
				Method method = clazz.getMethod("nm2atomcontainer",
						IStructureRecord.class);
				return (IAtomContainer) method.invoke(null, target);
			} catch (Exception x) {
				if (x instanceof AmbitException)
					throw (AmbitException) x;
				else
					throw new AmbitException(x);
			}
		case PDB:
			try {
				IAtomContainer ac = MoleculeTools.readPDBfile(target
						.getContent());
				for (IAtom atom : ac.atoms()) {
					if (atom.getImplicitHydrogenCount() == null)
						atom.setImplicitHydrogenCount(0);
				}
				return ac;
			} catch (Exception x) {
				throw new AmbitException(x);
			}
		default: {
			throw new AmbitException("Unknown format " + target.getFormat());
		}
		}
	}
}
