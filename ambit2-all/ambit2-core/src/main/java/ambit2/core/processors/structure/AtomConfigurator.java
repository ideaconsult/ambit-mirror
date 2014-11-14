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

/**
 * <b>Filename</b> MolAnalyser.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-8-2
 * <b>Project</b> toxTree
 */
package ambit2.core.processors.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

import ambit2.base.config.Preferences;


/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-8-2
 */
public class AtomConfigurator extends DefaultAmbitProcessor<IAtomContainer,IAtomContainer>{

    /**
	 * 
	 */
	private static final long serialVersionUID = -1245226849382037921L;
	//protected static Hashtable<String,Integer> elements = null;
	
    /**
     * 
     */
    public AtomConfigurator() {
        super();
    }
    
 
    public IAtomContainer process(IAtomContainer mol) throws AmbitException {
    	if (mol==null) throw new AmbitException("Null molecule!");
    	if (mol.getAtomCount()==0) throw new AmbitException("No atoms!");
    	

    		logger.fine("Configuring atom types ...");
        	//AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
    		CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
    		Iterator<IAtom> atoms = mol.atoms().iterator();
    		List<String> errors = null;
    		while (atoms.hasNext()) {
    			IAtom atom = atoms.next();
                if (!(atom instanceof IPseudoAtom)) 
                try {
                    IAtomType matched = matcher.findMatchingAtomType(mol, atom);
                    if (matched != null) {
                    	AtomTypeManipulator.configure(atom, matched);
                    	atom.setValency(matched.getValency());
                        atom.setAtomicNumber(matched.getAtomicNumber());
                        atom.setExactMass(matched.getExactMass());                      	
                    } else {
                    	if (errors == null) errors = new ArrayList<String>();
                    	if (errors.indexOf(atom.getSymbol())<0)
                    		errors.add(String.format("%s",atom.getSymbol()));
                    }
 	           } catch (Exception x) {
               	if (errors == null) errors = new ArrayList<String>();
            	if (errors.indexOf(atom.getSymbol())<0) {
            		errors.add(String.format("%s",atom.getSymbol()));
            		
            	}

                   
	           }
            }    
            if ((errors != null) && "true".equals(Preferences.getProperty(Preferences.STOP_AT_UNKNOWNATOMTYPES))) {
            	Collections.sort(errors);
                throw new AmbitException(errors.toString());
            }    		
        	/*
            CDKHueckelAromaticityDetector.detectAromaticity(molecule);    		
	        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
	        Iterator<IAtom> atoms = mol.atoms();
	        while (atoms.hasNext()) {
	           IAtom atom = atoms.next();
	           IAtomType type = matcher.findMatchingAtomType(mol, atom);
	           try {
	        	   AtomTypeManipulator.configure(atom, type);
                   logger.debug("Found " + atom.getSymbol() + " of type " + type.getAtomTypeName());                   
	           } catch (Exception x) {
	        	   logger.error(x.getMessage() + " " + atom.getSymbol(),x);
                   
                   if ("true".equals(Preferences.getProperty(Preferences.STOP_AT_UNKNOWNATOMTYPES))) {
                       throw new AmbitException(atom.getSymbol(),x);
                   }
                   
	           }
	        }    	   
	        */     

        	atoms = mol.atoms().iterator();
	        while (atoms.hasNext()) {
		           IAtom atom = atoms.next();
		           if ((atom.getAtomicNumber() == null) || (atom.getAtomicNumber() == 0)) {
		        	   Integer no = PeriodicTable.getAtomicNumber(atom.getSymbol());
		        	   if (no != null)
		        		   atom.setAtomicNumber(no.intValue());
		           }	   
		        }        

	        return mol;

    }

}

