/*
Copyright (C) 2005-2008  

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

package ambit2.hashcode.test;

import java.util.Hashtable;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.hashcode.Hash;
import ambit2.hashcode.Prime;
import junit.framework.TestCase;

public class testHash extends TestCase {
	
	public void  test() throws Exception {
		calcHash(5,10);
	}
	public void calcHash(int NAtomsNumber, int NHAtomsNumber) throws Exception {

	    Hashtable<String,Integer> seedHashtable = Prime.createPrimeNumberHashtable();
	    int NAtomsNumberPrime = (Integer)seedHashtable.get("NAtoms"+NAtomsNumber);
	    int NHAtomsNumberPrime = (Integer)seedHashtable.get("NHAtoms"+NHAtomsNumber);
	    
	    System.out.println("NAtomsNumberPrime:"+NAtomsNumberPrime);
	    System.out.println("NHAtomsNumberPrime:"+NHAtomsNumberPrime);
	    String smiles ="c1ccccc1Cl";
	    int number_rings;	    
	    SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
	    IAtomContainer mol = p.parseSmiles(smiles);
	    /*
	    try {
	    	
	    MolAnalyser.analyse(mol);
	    } catch (MolAnalyseException x) {
			
		} 
	    MolFlags mf = (MolFlags) mol.getProperty(MolFlags.MOLFLAGS);	   
	    IRingSet rings = mf.getRingset();
		if ((rings == null)) {
			number_rings = -1;
		} else if (rings.getAtomContainerCount() == 1) {
			number_rings = 0;
		} else {
			number_rings = 1;
		}
		int RingIndexNumberPrime = (Integer)seedHashtable.get("RingIndex"+number_rings);
	    
	    Hash testHashFinal = new Hash(NAtomsNumberPrime+NHAtomsNumberPrime+RingIndexNumberPrime);
	    */	   
	    Hash testHashFinal = new Hash(NAtomsNumberPrime+NHAtomsNumberPrime);
	    //System.out.println(RingIndexNumberPrime);
	}
}
