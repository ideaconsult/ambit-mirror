/*
Copyright Ideaconsult Ltd. (C) 2005-2007 

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/
package ambit2.query.smarts;

import javax.vecmath.Point3d;

import joelib.molecule.JOEAtom;
import joelib.molecule.JOEBond;
import joelib.molecule.JOEMol;

import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Abstract class that provides convertor procedures to
 * convert CDK classes to JOELib classes and visa versa. Fixed from CDK cConvertor class.
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
                    atom.getPoint3d().x,
                    atom.getPoint3d().y,
                    atom.getPoint3d().z
                );
            } else if (coordType == COORDINATES_2D ||
                       (atom.getPoint2d() != null && coordType != -1)) {
                convertedAtom.setVector(
                    atom.getPoint2d().x,
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
            //convertedAtom.setPartialCharge(atom.get);
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
    public static IAtom convert(JOEAtom atom) {
        if (atom != null) {
            IAtom convertedAtom = DefaultChemObjectBuilder.getInstance().newAtom(Elements.CARBON);
            try {
                // try to give the atom the correct symbol
                org.openscience.cdk.config.IsotopeFactory isotopeFactory =
                    org.openscience.cdk.config.IsotopeFactory.getInstance(convertedAtom.getBuilder());
                //isotopeFactory.configure(convertedAtom);
                IElement element = isotopeFactory.getElement(atom.getAtomicNum());
                convertedAtom = DefaultChemObjectBuilder.getInstance().newAtom(element.getSymbol());
            } catch (java.lang.Exception exception) {
                logger.debug("Error in getting the isotope factory");
            }
            try {
                // try to give the atom its coordinates
                convertedAtom.setPoint3d(new Point3d(
                			atom.getVector().x(),
                			atom.getVector().y(),
                			atom.getVector().z()));
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
            convertedBond.setBegin(convert(bond.getAtom(0)));
            convertedBond.setEnd(convert(bond.getAtom(1)));
            if (bond.getOrder().equals(IBond.Order.SINGLE)) convertedBond.setBO(1);
            if (bond.getOrder().equals(IBond.Order.DOUBLE)) convertedBond.setBO(2);
            if (bond.getOrder().equals(IBond.Order.TRIPLE)) convertedBond.setBO(3);

           if (bond.getFlag(CDKConstants.ISAROMATIC))
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
    public static IBond convert(JOEBond bond) {
        if (bond != null) {
            IBond.Order order = IBond.Order.SINGLE;
            if (bond.isSingle()) order = IBond.Order.SINGLE;
            if (bond.isDouble()) order = IBond.Order.DOUBLE;
            if (bond.isTriple()) order = IBond.Order.TRIPLE;
            
            
            IBond bond_cdk = new Bond(
                                    convert(bond.getBeginAtom()),
                                    convert(bond.getEndAtom()),
                                    order);
            bond_cdk.setFlag(CDKConstants.ISAROMATIC, bond.isAromatic());
            return bond_cdk;
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
    public static JOEMol convert(IMolecule mol) {
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
    public static JOEMol convert(IMolecule mol, int coordType) {
        if (mol != null) {
            JOEMol converted = new JOEMol();
            
            // start molecule modification
            converted.beginModify();

            int NOatoms = mol.getAtomCount();
            
            // add atoms
            converted.reserveAtoms(NOatoms);
            for (int i=0; i<NOatoms; i++) {
                converted.addAtom(convert(mol.getAtom(i), coordType));
            }
            
            // add bonds
            double[][] matrix = ConnectionMatrix.getMatrix(mol);
            for (int i=0; i<NOatoms-1; i++) {
                for (int j=i+1; j<NOatoms; j++) {
                    if (matrix[i][j] != 0.0) {
                        // atoms i,j are connected
                        /* JOEMol.addBond() needs atom ids [1,...] */
                        converted.addBond(i+1,j+1, (int)matrix[i][j]);
                        /*
                        if (mol.getBond(mol.getAtom(i), mol.getAtom(j)).getFlag(CDKConstants.ISAROMATIC))
                        	converted.getBond(i+1,j+1).setAromatic();
                        	*/            
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
    public static IMolecule convert(JOEMol mol) {
        if (mol != null) {
            IMolecule converted = new Molecule();
            int NOatoms = mol.numAtoms();
            for (int i=1; i<=NOatoms; i++) {
                /* JOEMol.getAtom() needs ids [1,...] */
                JOEAtom joeAtom = mol.getAtom(i);
                IAtom cdka = convert(joeAtom);
                converted.addAtom(cdka);
            }
            int NObonds = mol.numBonds();
            for (int i=1; i<=NObonds; i++) {
                /* JOEMol.getBond() needs ids [0,...] */
                JOEBond joeBond = mol.getBond(i-1);
                /* Molecule.addBond() need atom ids [0,...] */

                
                IBond.Order order = IBond.Order.SINGLE;
                if (joeBond.isSingle()) order = IBond.Order.SINGLE;
                if (joeBond.isDouble()) order = IBond.Order.DOUBLE;
                if (joeBond.isTriple()) order = IBond.Order.TRIPLE;
                converted.addBond(joeBond.getBeginAtomIdx()-1,
                        joeBond.getEndAtomIdx()-1,
                        order);
                //TODO Aromatic bond 
                /*
                bond_cdk.setFlag(CDKConstants.ISAROMATIC, bond.isAromatic());
                return bond_cdk;
                */                
            }
            return converted;
        } else {
            return null;
        }
    }

}
