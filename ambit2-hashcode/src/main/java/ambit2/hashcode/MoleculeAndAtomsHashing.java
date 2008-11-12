/*
Copyright (C) 2008  

Contact: martinmartinov

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

package ambit2.hashcode ;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.CDKConstants;

public class MoleculeAndAtomsHashing {
	private int bonded_neighbor_atomsPrime  ;
	private int bonded_hydrogen_neighbor_atomsPrime ;
	private int atomic_numberPrime ;
	private int number_of_atomsPrime ;   
	private int number_rings;
    //number of atoms in molecule
	private int number_of_atoms;
	private int atomic_number;
	private int bonded_neighbor_atoms;
	private int bonded_hydrogen_neighbor_atoms;
	private int stereo_center;
    private int type_of_bond;
	private int stereo_centerPrime;
	private int type_of_bondPrime;
	private List<IAtom> neighbors;
	private Hashtable<String,Integer> seedHashtable;    
	private Hash atomsHash,atomHash,moleculeSizeHash,moleculeHash,bondHash,bondsHash;
    
	public  MoleculeAndAtomsHashing(){
	 this.seedHashtable= Prime.createPrimeNumberHashtable();
	}
    
    
    
    public long getMoleculeHash( IAtomContainer mol){
    	/*
    	 * Is there a reason to use ChemGraph, instead of mol.getAtomCount() ? 
    	ChemGraph ch_graph = new ChemGraph(mol);	    
        number_of_atoms = ch_graph.getNumAtoms();
        */
    	number_of_atoms = mol.getAtomCount();
        
        Iterator<IAtom> atoms = mol.atoms();
        IAtom atom, neighbor;
        atomsHash = new Hash();
        while (atoms.hasNext()) {
        	atom = atoms.next();
        	atomic_number = atom.getAtomicNumber();
        	neighbors = mol.getConnectedAtomsList(atom);
        	bonded_neighbor_atoms = neighbors.size();
        	bonded_hydrogen_neighbor_atoms = 0;
    		for (int f = 0; f < neighbors.size(); f++){    			
    			neighbor = neighbors.get(f);
    			if (neighbor.getSymbol().equals("H")){
    				bonded_hydrogen_neighbor_atoms++;
    			}
    			
    		}
    		atomic_numberPrime = this.seedHashtable.get("SysNum"+atomic_number);
    		bonded_neighbor_atomsPrime = this.seedHashtable.get("NAtoms"+bonded_neighbor_atoms);
    		bonded_hydrogen_neighbor_atomsPrime = seedHashtable.get("NHAtoms"+bonded_hydrogen_neighbor_atoms);
    		atomHash = new Hash(atomic_numberPrime+bonded_neighbor_atomsPrime+bonded_hydrogen_neighbor_atomsPrime);
    		atomsHash = atomsHash.sag(atomHash);
        }
        number_of_atomsPrime =  seedHashtable.get("MolSize"+number_of_atoms);
        moleculeSizeHash = new Hash(number_of_atomsPrime);
        moleculeHash = atomsHash; 
        moleculeHash = moleculeHash.sag(moleculeSizeHash);
        Iterator bonds = mol.bonds();
        
        bondsHash = new Hash();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
            
        	if (bond.getAtomCount() != 2) {
        		
        	} else {
        	switch(bond.getStereo()){
			case CDKConstants.STEREO_BOND_UP:
				stereo_center = 1;
				break;
			case CDKConstants.STEREO_BOND_UP_INV:
				stereo_center = 1;
				break;
			case CDKConstants.STEREO_BOND_DOWN:
				stereo_center = -1;
				break;
			case CDKConstants.STEREO_BOND_DOWN_INV:
				stereo_center = -1;
				break;
			default:
				stereo_center = 0;
        		}
        	Order o = bond.getOrder();        	 
        	if (o.equals(Order.DOUBLE)) type_of_bond = 2;             
        	if (o.equals(Order.SINGLE)) type_of_bond = 1;             
        	if (o.equals(Order.TRIPLE)){
        		type_of_bond = 3;
        	}
        	if (o.equals(Order.QUADRUPLE)){
        		type_of_bond = 4;        	
        	}
         }
        	stereo_centerPrime = this.seedHashtable.get("StereoCenter"+stereo_center);
    		type_of_bondPrime = this.seedHashtable.get("TypeOfBond"+type_of_bond);
    		bondHash = new Hash(stereo_centerPrime+type_of_bondPrime);
    		bondsHash = bondsHash.sag(bondHash);
        	
        }
        moleculeHash = moleculeHash.sag(bondsHash);
        return moleculeHash.hash_value();
    	
    }

}
