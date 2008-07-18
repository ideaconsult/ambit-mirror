/* $RCSfile$
 * $Author: rajarshi $
 * $Date: 2006-09-19 19:08:34 +0300 (Tue, 19 Sep 2006) $
 * $Revision: 6955 $
 *
 * Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package ambit.data.molecule;

import joelib.molecule.JOEAtom;
import joelib.molecule.JOEBond;
import joelib.molecule.JOEMol;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Abstract class that provides convertor procedures to
 * convert CDK classes to JOELib classes and visa versa.
 *
 * <p>JOELib is a Java implementation of the OELib classes and
 * can be found at: <a href="http://joelib.sourceforge.net/">http://joelib.sourceforge.net/</a>.
 *
 * @cdk.module libio-joelib
 *
 * @author     egonw
 * @author     Joerg K. Wegner <wegnerj@informatik.uni-tuebingen.de>
 *
 * @cdk.keyword    JOELib
 * @cdk.keyword    class convertor
 */
public class Convertor {

     private static LoggingTool logger = new LoggingTool();

     public static final int COORDINATES_3D = 3;
     public static final int COORDINATES_2D = 2;

    /**
     * Converts an org.openscience.cdk.Atom class into a
     * joelib.molecule.JOEAtom class.
     *
     * Conversion includes:
     *   - atomic number
     *   - coordinates
     *
     * @param   atom    class to be converted
     * @return          converted class in JOELib
     **/
    public static JOEAtom convert(IAtom atom) {
         return convert(atom, -1);
     }

     /**
      * Converts an org.openscience.cdk.Atom class into a
      * joelib.molecule.JOEAtom class.
      *
      * Conversion includes:
      *   - atomic number
      *   - coordinates
      *
      * @param   atom      CDK atom to be converted
      * @param   coordType coordinates to use. if -1 this converter uses the available coordinates.
      *                    If 3D and 2D coordinates are available, the 3D coordinates are used
      * @return            converted JOELib atom
      *
      * @see #COORDINATES_3D
      * @see #COORDINATES_2D
      **/
     public static JOEAtom convert(IAtom atom, int coordType) {
        if (atom != null) {
            JOEAtom convertedAtom = new JOEAtom();
            if (coordType == COORDINATES_3D ||
                (atom.getPoint3d() != null && coordType != -1)) {
                convertedAtom.setVector(
                    atom.getX3d(),
                    atom.getY3d(),
                    atom.getZ3d()
                );
            } else if (coordType == COORDINATES_2D ||
                       (atom.getPoint2d() != null && coordType != -1)) {
                convertedAtom.setVector(
                    atom.getX2d(),
                    atom.getPoint2d().y,
                    0.0
                );
            } else {
                convertedAtom.setVector(0.0, 0.0, 0.0);
            }
            
            if (atom.getAtomicNumber()<=0) 
            	try {
            		org.openscience.cdk.config.IsotopeFactory isotopeFactory =
            			org.openscience.cdk.config.IsotopeFactory.getInstance(atom.getBuilder());
            		isotopeFactory.configure(atom);
            	} catch (Exception x) {
            		
            	}
            
            convertedAtom.setAtomicNum(atom.getAtomicNumber());
            return convertedAtom;
        } else {
            return null;
        }
    }

    /**
     * Converts an joelib.molecule.JOEAtom class into a
     * org.openscience.cdk.Atom class.
     *
     * Conversion includes:
     *   - atomic number
     *   - coordinates
     *
     * @param   atom    class to be converted
     * @return          converted class in CDK
     **/
    public static Atom convert(JOEAtom atom) {
        if (atom != null) {
            Atom convertedAtom = new Atom("C");
            try {
                // try to give the atom the correct symbol
                org.openscience.cdk.config.IsotopeFactory isotopeFactory =
                    org.openscience.cdk.config.IsotopeFactory.getInstance(convertedAtom.getBuilder());
                //isotopeFactory.configure(convertedAtom);
                IElement element = isotopeFactory.getElement(atom.getAtomicNum());
                convertedAtom = new Atom(element.getSymbol());
            } catch (java.lang.Exception exception) {
                logger.debug("Error in getting the isotope factory");
            }
            try {
                // try to give the atom its coordinates
                convertedAtom.setX3d(atom.getVector().x());
                convertedAtom.setY3d(atom.getVector().y());
                convertedAtom.setZ3d(atom.getVector().z());
            } catch (java.lang.Exception exception) {
                logger.debug("Error in setting coordinates");
            }
            try {
                // try to give the atom its atomic number
                convertedAtom.setAtomicNumber(atom.getAtomicNum());
            } catch (java.lang.Exception exception) {
                // System.out.println("AtomicNumber failed");
                logger.debug("Error in setting atomic number");
            }
            return convertedAtom;
        } else {
            return null;
        }
    }

    /**
     * Converts an org.openscience.cdk.Bond class into a
     * joelib.molecule.JOEBond class.
     *
     * Conversion includes:
     *   - atoms which it conects
     *   - bond order
     *
     * @param   bond    class to be converted
     * @return          converted class in JOELib
     **/
    public static JOEBond convert(IBond bond) {
        if (bond != null) {
            JOEBond convertedBond = new JOEBond();
            convertedBond.setBegin(convert(bond.getAtomAt(0)));
            convertedBond.setEnd(convert(bond.getAtomAt(1)));
            convertedBond.setBO((int)bond.getOrder());
            if (bond.getOrder() == CDKConstants.BONDORDER_AROMATIC)
            	convertedBond.setAromatic();            

            return convertedBond;
        } else {
            return null;
        }
    }

    /**
     * Converts an joelib.molecule.JOEBond class into a
     * org.openscience.cdk.Bond class.
     *
     * Conversion includes:
     *   - atoms which it conects
     *   - bond order
     *
     * @param   bond    class to be converted
     * @return          converted class in CDK
     **/
    public static Bond convert(JOEBond bond) {
        if (bond != null) {
            return new Bond(
                                    convert(bond.getBeginAtom()),
                                    convert(bond.getEndAtom()),
                                    (double)bond.getBondOrder());
        } else {
            return null;
        }
    }

    /**
     * Converts an org.openscience.cdk.Molecule class into a
     * joelib.molecule.JOEMol class.
     *
     * Conversion includes:
     *   - atoms
     *   - bonds
     *
     * @param   mol     molecule to be converted
     * @return          converted JOELib molecule
     *
     * @see #COORDINATES_3D
     * @see #COORDINATES_2D
     **/
    public static JOEMol convert(IAtomContainer mol) {
        return convert(mol, -1);
    }

    /**
     * Converts an org.openscience.cdk.Molecule class into a
     * joelib.molecule.JOEMol class.
     *
     * Conversion includes:
     *   - atoms
     *   - bonds
     *
     *
     * @param   mol       class to be converted
     * @param   coordType coordinates to use. if -1 this converter uses the available coordinates.
     *                    If 3D and 2D coordinates are available, the 3D coordinates are used
     * @return            converted class in JOELib
     *
     * @see #COORDINATES_3D
     * @see #COORDINATES_2D
     **/
    public static JOEMol convert(IAtomContainer mol, int coordType) {
        if (mol != null) {
            JOEMol converted = new JOEMol();
            
            // start molecule modification
            converted.beginModify();

            int NOatoms = mol.getAtomCount();
            
            // add atoms
            converted.reserveAtoms(NOatoms);
            for (int i=0; i<NOatoms; i++) {
                converted.addAtom(convert(mol.getAtomAt(i), coordType));
            }
            
            // add bonds
            
            double[][] matrix = ConnectionMatrix.getMatrix(mol);
            for (int i=0; i<NOatoms-1; i++) {
                for (int j=i+1; j<NOatoms; j++) {
                    if (matrix[i][j] != 0.0) {
                        // atoms i,j are connected
                    	JOEBond jbond = new JOEBond();
                    	
                    	jbond.setBegin(converted.getAtom(i+1));
                    	jbond.setEnd(converted.getAtom(j+1));
                    	
                        if (matrix[i][j] == CDKConstants.BONDORDER_AROMATIC) {
                        	jbond.setBO(JOEBond.JOE_AROMATIC_BOND_ORDER);
                        	jbond.setAromatic();  
                        } else
                        	jbond.setBO((int)matrix[i][j]);
                    	converted.addBond(jbond);
                        //converted.addBond(i+1,j+1, (int)matrix[i][j]);
                        
                    } 
                }
            }

            return converted;
        } else {
            return null;
        }
    }

    /**
     * Converts an joelib.molecule.JOEMol class into a
     * org.openscience.cdk.Molecule class.
     *
     * Conversion includes:
     *   - atoms
     *   - bonds
     *
     * @param   mol     class to be converted
     * @return          converted class in CDK
     **/
    public static Molecule convert(JOEMol mol) {
        if (mol != null) {
            Molecule converted = new Molecule();
            int NOatoms = mol.numAtoms();
            for (int i=1; i<=NOatoms; i++) {
                /* JOEMol.getAtom() needs ids [1,...] */
                JOEAtom joeAtom = mol.getAtom(i);
                Atom cdka = convert(joeAtom);
                converted.addAtom(cdka);
            }
            int NObonds = mol.numBonds();
            for (int i=1; i<=NObonds; i++) {
                /* JOEMol.getBond() needs ids [0,...] */
                JOEBond joeBond = mol.getBond(i-1);
                /* Molecule.addBond() need atom ids [0,...] */
                converted.addBond(joeBond.getBeginAtomIdx()-1,
                                  joeBond.getEndAtomIdx()-1,
                                  joeBond.getBondOrder());
            }
            return converted;
        } else {
            return null;
        }
    }

}
