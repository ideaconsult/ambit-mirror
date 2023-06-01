/*  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2008-12-17 18:32:36 +0200 (Wed, 17 Dec 2008) $
 *  $Revision: 13633 $
 *
 *  Copyright (C) 2001-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package ambit2.descriptors;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openscience.cdk.config.isotopes.IsotopeReader;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Used to store and return data of a particular isotope. As this class is a
 * singleton class, one gets an instance with: 
 * <pre>
 * IsotopeFactory ifac = IsotopFactory.getInstance(new IChemObject().getBuilder());
 * </pre>
 *
 * <p>Data about the isotopes are read from the file
 * org.openscience.cdk.config.isotopes.xml in the cdk-standard
 * module. Part of the data in this file was collected from
 * the website <a href="http://www.webelements.org">webelements.org</a>.
 *
 * <p>The use of this class is exemplified as follows. To get information 
 * about the major isotope of hydrogen, one can use this code:
 * <pre>
 *   IsotopeFactory factory = IsotopeFactory.getInstance(SilentChemObjectBuilder.getInstance());
 *   Isotope major = factory.getMajorIsotope("H");
 * </pre> 
 *
 * @cdk.module core
 * @cdk.svnrev  $Revision: 13633 $
 *
 * @author     steinbeck
 * @cdk.created    2001-08-29
 * @cdk.keyword    isotope
 * @cdk.keyword    element
 */
public class IsotopeFactory
{

	private static IsotopeFactory ifac = null;
	private List<IIsotope> isotopes = null;
    private HashMap<String, IIsotope> majorIsotopes = null;
    private boolean debug = false;
    private ILoggingTool logger;

    /**
     * Private constructor for the IsotopeFactory object.
     *
     *@exception IOException             A problem with reading the isotopes.xml
     *      file
     * @param builder The builder from which we the factory will be generated
     */
	private IsotopeFactory(IChemObjectBuilder builder) throws IOException {
        logger = LoggingToolFactory.createLoggingTool(IsotopeFactory.class);
        logger.info("Creating new IsotopeFactory");

        InputStream ins;
        // ObjIn in = null;
        String errorMessage = "There was a problem getting ambit2/descriptors/" +
                              "isotopes.xml as a stream";
        try {
            String configFile = "ambit2/descriptors/isotopes.xml";  //"org/openscience/cdk/config/data/isotopes.xml";
            if (debug) logger.debug("Getting stream for ", configFile);
            ins = IsotopeFactory.class.getClassLoader().getResourceAsStream(configFile);
        } catch (Exception exception) {
            logger.error(errorMessage);
            logger.debug(exception);
            throw new IOException(errorMessage);
        }
        if (ins == null) {
            logger.error(errorMessage);
            throw new IOException(errorMessage);
        }
        IsotopeReader reader = new IsotopeReader(ins, builder);
        //in = new ObjIn(ins, new Config().aliasID(false));
        isotopes = reader.readIsotopes();
        if (debug) logger.debug("Found #isotopes in file: ", isotopes.size());
        /* for (int f = 0; f < isotopes.size(); f++) {
              Isotope isotope = (Isotope)isotopes.elementAt(f);
          } What's this loop for?? */

        majorIsotopes = new HashMap<String, IIsotope>();
    }


	/**
	 * Returns an IsotopeFactory instance.
	 *
         * @param      builder                 ChemObjectBuilder used to construct the Isotope's
	 * @return                             The instance value
	 * @exception  IOException             Description of the Exception
	 */
    public static IsotopeFactory getInstance(IChemObjectBuilder builder)
			 throws IOException {
        if (ifac == null) {
            ifac = new IsotopeFactory(builder);
        }
        return ifac;
    }


	/**
	 *  Returns the number of isotopes defined by this class.
	 *
	 *@return    The size value
	 */
    public int getSize()
	{
		return isotopes.size();
	}

	/**
	 * Gets an array of all isotopes known to the IsotopeFactory for the given
	 * element symbol.
	 *
	 *@param  symbol  An element symbol to search for
	 *@return         An array of isotopes that matches the given element symbol
	 */
    public IIsotope[] getIsotopes(String symbol) {
        ArrayList<IIsotope> list = new ArrayList<IIsotope>();
        for (IIsotope isotope : isotopes) {
            if (isotope.getSymbol().equals(symbol)) {
                try {
                    IIsotope clone = (IIsotope) isotope.clone();
                    list.add(clone);
                } catch (CloneNotSupportedException e) {
                    logger.error("Could not clone IIsotope: ", e.getMessage());
                    logger.debug(e);
                }
            }
        }
        return list.toArray(new IIsotope[list.size()]);
    }


    /**
	 * Returns the most abundant (major) isotope with a given atomic number.
     *
     * <p>The isotope's abundancy is for atoms with atomic number 60 and smaller
     * defined as a number that is proportional to the 100 of the most abundant
     * isotope. For atoms with higher atomic numbers, the abundancy is defined
     * as a percentage.
	 *
	 * @param  atomicNumber  The atomicNumber for which an isotope is to be returned
	 * @return               The isotope corresponding to the given atomic number
     *
     * @see #getMajorIsotope(String symbol)
     */
    public IIsotope getMajorIsotope(int atomicNumber) {
        IIsotope major = null;
        for (IIsotope isotope : isotopes) {
            if (isotope.getAtomicNumber() == atomicNumber) {
                try {
                    if (major == null) {
                        major = (IIsotope) isotope.clone();
                    } else {
                        if (isotope.getNaturalAbundance() > major.getNaturalAbundance()) {
                            major = (IIsotope) isotope.clone();
                        }
                    }
                } catch (CloneNotSupportedException e) {
                    logger.error("Could not clone IIsotope: ", e.getMessage());
                    logger.debug(e);
                }
            }
        }
        if (major == null) logger.error("Could not find major isotope for: ", atomicNumber);
        return major;
    }

    /**
     * Checks whether the given element exists.
     *
     * @param  elementName   The element name to test
     * @return               True is the element exists, false otherwise
     */
    public boolean isElement(String elementName) {
        return (getElement(elementName) != null);
    }
    
    /**
     *  Returns the most abundant (major) isotope whose symbol equals element.
     *
     *@param  symbol  Description of the Parameter
     *@return         The Major Isotope value
     */
    public IIsotope getMajorIsotope(String symbol) {
        IIsotope major = null;
        if (majorIsotopes.containsKey(symbol)) {
            major = majorIsotopes.get(symbol);
        } else {
            for (IIsotope isotope : isotopes) {
                if (isotope.getSymbol().equals(symbol)) {
                    try {
                        if (major == null) {
                            major = (IIsotope) isotope.clone();
                        } else {
                            if (isotope.getNaturalAbundance() > major.getNaturalAbundance()) {
                                major = (IIsotope) isotope.clone();
                            }
                        }
                    } catch (CloneNotSupportedException e) {
                        logger.error("Could not clone IIsotope: ", e.getMessage());
                        logger.debug(e);
                    }
                }
            }
            if (major == null) {
                logger.error("Could not find major isotope for: ", symbol);
            } else {
                majorIsotopes.put(symbol, major);
            }
        }
        return major;
    }

	/**
	 *  Returns an Element with a given element symbol.
	 *
	 *@param  symbol  The element symbol for the requested element
	 *@return         The configured element
	 */
    public IElement getElement(String symbol)
	{
        return getMajorIsotope(symbol);
	}


	/**
	 *  Returns an element according to a given atomic number.
	 *
	 *@param  atomicNumber  The elements atomic number
	 *@return               The Element
	 */
    public IElement getElement(int atomicNumber)
	{
        return getMajorIsotope(atomicNumber);
	}

    /**
     * Returns the symbol matching the element with the given atomic number.
     *
     * @param  atomicNumber  The elements atomic number
     * @return               The symbol of the Element
     */
    public String getElementSymbol(int atomicNumber) {
        IIsotope isotope = getMajorIsotope(atomicNumber);
        return isotope.getSymbol();
    }

	/**
	 *  Configures an atom. Finds the correct element type
	 *  by looking at the atoms element symbol.
	 *
	 *@param  atom  The atom to be configured
	 *@return       The configured atom
	 */
    public IAtom configure(IAtom atom)
	{
		IIsotope isotope = getMajorIsotope(atom.getSymbol());
		return configure(atom, isotope);
	}


	/**
	 *  Configures an atom to have all the data of the
	 *  given isotope.
	 *
	 *@param  atom     The atom to be configure
	 *@param  isotope  The isotope to read the data from
	 *@return          The configured atom
	 */
    public IAtom configure(IAtom atom, IIsotope isotope)
	{
		atom.setMassNumber(isotope.getMassNumber());
		atom.setSymbol(isotope.getSymbol());
		atom.setExactMass(isotope.getExactMass());
		atom.setAtomicNumber(isotope.getAtomicNumber());
		atom.setNaturalAbundance(isotope.getNaturalAbundance());
		return atom;
	}


	/**
	 *  Configures atoms in an AtomContainer to 
	 *  carry all the correct data according to their element type.
	 *
	 *@param  container  The AtomContainer to be configured
	 */
    public void configureAtoms(IAtomContainer container)
	{
		for (int f = 0; f < container.getAtomCount(); f++)
		{
			configure(container.getAtom(f));
		}
	}
    /**
	 *  Gets the natural mass of this element, defined as average of masses of isotopes, 
	 *  weighted by abundance.
	 *
	 * @param  element                     Description of the Parameter
	 * @return                             The natural mass value
	 */
	public double getNaturalMass(IElement element){
		IIsotope[] isotopes = getIsotopes(element.getSymbol());
		double summedAbundances = 0;
		double summedWeightedAbundances = 0;
		double getNaturalMass = 0;
		for (int i = 0; i < isotopes.length; i++) {
			summedAbundances += isotopes[i].getNaturalAbundance();
			summedWeightedAbundances += isotopes[i].getNaturalAbundance() * isotopes[i].getExactMass();
			getNaturalMass = summedWeightedAbundances / summedAbundances;
		}
		return getNaturalMass;
	}

}

