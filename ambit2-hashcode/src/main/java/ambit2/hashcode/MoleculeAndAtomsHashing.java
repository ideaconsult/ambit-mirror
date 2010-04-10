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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.geometry.BondTools;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;

public class MoleculeAndAtomsHashing {
	private int bonded_neighbor_atomsPrime  ;
	private int bonded_hydrogen_neighbor_atomsPrime ;
	private int atomic_numberPrime ;
	private int number_of_atomsPrime ;   
	
    //number of atoms in molecule
	private int number_of_atoms;
	private int atomic_number;
	private int bonded_neighbor_atoms;
	private int bonded_hydrogen_neighbor_atoms;
	private int stereo_center;
	private int stereo_parity;
	private int cis_trans;
    private int type_of_bond;
    private int formal_charge;
    private int formal_chargePrime;
	private int stereo_centerPrime;
	private int stereo_parityPrime;
	private int cis_transPrime;
	private int type_of_bondPrime;
	private List<IAtom> neighbors;
	private Hashtable<String,Integer> seedHashtable; 
	private Hashtable<IAtom,IAtomParity> atomParities;
	private Hash atomsHash,atomHash,moleculeSizeHash,moleculeHash,bondHash,bondsHash;
	private Hash atomic_numberHash,bonded_neighbor_atomsHash,bonded_hydrogen_neighbor_atomsHash,formal_chargeHash,stereo_centerHash,type_of_bondHash;
    
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
    	
    	
        IAtom neighbor;
        atomsHash = new Hash();
        
        
        int k = 0;
        for (IAtom atom: mol.atoms()) {

        	int atom_double_bonds = 0;
        	atomic_number = atom.getAtomicNumber();
        	neighbors = mol.getConnectedAtomsList(atom);
        	bonded_neighbor_atoms = neighbors.size();
        	bonded_hydrogen_neighbor_atoms = 0;
        	formal_charge = getFormalCharge(atom);        	
        	//stereo_parity = getStereoParity(atom);
        	/*if(BondTools.isStereo(mol, atom)){
        		stereo_parity = 1;
        	}
        	else{
        		stereo_parity = 0;
        	}*/
        	
    		for (int f = 0; f < neighbors.size(); f++){    			
    			neighbor = neighbors.get(f);
    			if (neighbor.getSymbol().equals("H")){
    				bonded_hydrogen_neighbor_atoms++;
    			}
    			
    		}
    		atomic_numberPrime = this.seedHashtable.get("SysNum"+atomic_number);
    		bonded_neighbor_atomsPrime = this.seedHashtable.get("NAtoms"+bonded_neighbor_atoms);
    		bonded_hydrogen_neighbor_atomsPrime = this.seedHashtable.get("NHAtoms"+bonded_hydrogen_neighbor_atoms);
    		formal_chargePrime = this.seedHashtable.get("FormalCharge"+formal_charge);
    		//stereo_parityPrime = this.seedHashtable.get("StereoParity"+stereo_parity);
    		//atomHash = new Hash(atomic_numberPrime+bonded_neighbor_atomsPrime+bonded_hydrogen_neighbor_atomsPrime+formal_chargePrime+stereo_parityPrime);
    		/*cis_trans = getCisTrans(k,mol);
    		if(cis_trans != -1){
    			atom_double_bonds++;
    			cis_transPrime = this.seedHashtable.get("CisTrans"+cis_trans);
    		}
    		if(atom_double_bonds >0){
    			atomHash = new Hash(atomic_numberPrime+bonded_neighbor_atomsPrime+bonded_hydrogen_neighbor_atomsPrime+formal_chargePrime+cis_transPrime);
            }
    		else{
    			atomHash = new Hash(atomic_numberPrime+bonded_neighbor_atomsPrime+bonded_hydrogen_neighbor_atomsPrime+formal_chargePrime);
    		}
    		k++;*/
    		//atomHash = new Hash(atomic_numberPrime+bonded_neighbor_atomsPrime+bonded_hydrogen_neighbor_atomsPrime+formal_chargePrime);
    		//
    		
    		atomic_numberHash = new Hash(atomic_numberPrime);
    		bonded_neighbor_atomsHash = new Hash(bonded_neighbor_atomsPrime);
    		bonded_hydrogen_neighbor_atomsHash = new Hash(bonded_hydrogen_neighbor_atomsPrime);
    		formal_chargeHash = new Hash(formal_chargePrime);
    		
    		//atomHash = new Hash(atomic_numberPrime*bonded_neighbor_atomsPrime*bonded_hydrogen_neighbor_atomsPrime*formal_chargePrime);
    	    //atomsHash = atomsHash.sag(atomHash);
    		
    		atomsHash = atomsHash.sag(atomic_numberHash);
    		atomsHash = atomsHash.sag(bonded_neighbor_atomsHash);
    		atomsHash = atomsHash.sag(bonded_hydrogen_neighbor_atomsHash);
    		atomsHash = atomsHash.sag(formal_chargeHash);
    		
    		
        }
        //System.out.println(atomsHash.hash_value());
        number_of_atomsPrime =  seedHashtable.get("MolSize"+number_of_atoms);
        moleculeSizeHash = new Hash(number_of_atomsPrime);
        moleculeHash = atomsHash; 
        moleculeHash = moleculeHash.sag(moleculeSizeHash);
        Iterator<IBond> bonds = mol.bonds().iterator();
        
        bondsHash = new Hash();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
            
        	if (bond.getAtomCount() != 2) {
        		
        	} else {
        	switch(bond.getStereo()){
        	
			case UP:
				stereo_center = 1;
				break;
			case UP_INVERTED:
				stereo_center = 1;
				break;
			case DOWN:
				stereo_center = -1;
				break;
			case DOWN_INVERTED:
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
    		//bondHash = new Hash(stereo_centerPrime+type_of_bondPrime);
    		//*
    		//bondHash = new Hash(stereo_centerPrime*type_of_bondPrime);
    		//bondsHash = bondsHash.sag(bondHash);
    		stereo_centerHash = new Hash(stereo_centerPrime);
    		type_of_bondHash = new Hash(type_of_bondPrime);
    		bondsHash = bondsHash.sag(stereo_centerHash);
    		bondsHash = bondsHash.sag(type_of_bondHash);
    		
        	
        }
       // System.out.println(bondsHash.hash_value());
        moleculeHash = moleculeHash.sag(bondsHash); 
       // System.out.println(moleculeHash.hash_value());
       // System.out.println("END");
        return moleculeHash.hash_value();
    	
    }
    public static int getFormalCharge(IAtom atom){
    	Integer charge = atom.getFormalCharge();
    	int formal_charge = 4;
        if (charge != CDKConstants.UNSET && charge != 0) {
        	 if (charge == 3) {
        		 formal_charge = 1;
             } else if (charge == 2) {
            	 formal_charge = 2;
             } else if (charge == 1) {
            	 formal_charge = 3;
             } else if (charge == 0) {
             } else if (charge == -1) {
            	 formal_charge = 5;
             } else if (charge == -2) {
            	 formal_charge = 6;
             } else if (charge == -3) {
            	 formal_charge = 7;             }
        
        	 return formal_charge;
        }
        else{
        	 return formal_charge;
        }
    }
    
    public static int getStereoParity(IAtom atom ){
    	int stereo_parity = 0;
    	
    	try{
    	if (atom.getStereoParity() == CDKConstants.STEREO_ATOM_PARITY_MINUS)
    		stereo_parity= -1;
		else if (atom.getStereoParity() == CDKConstants.STEREO_ATOM_PARITY_PLUS)
			stereo_parity= 1;
    	System.out.println(stereo_parity);
    	}
    	catch(Exception e){}
    	return stereo_parity;
    }
    
    public static int getCisTrans(int k,IAtomContainer mol){
	    boolean trans=false;
	    //IMolecule withh = mol.getBuilder().newMolecule(mol);
	    
   		if(GeometryTools.has2DCoordinatesNew(mol)==2){
   		try{
   			if(k>2 && BondTools.isValidDoubleBondConfiguration(mol,mol.getBond(mol.getAtom(k-2),mol.getAtom(k-1)))){
   				trans=BondTools.isCisTrans(mol.getAtom(k-3),mol.getAtom(k-2),mol.getAtom(k-1),mol.getAtom(k-0),mol);
   				
   				if(trans){
   					//cis
   					return 0;
   					
   				}
   				
   				else{
   					//trans
   					return 1;
   					
   				}
   				
   					
   			}
   			
   			}
   			catch(Exception ex){
				
			}
   			
   		}
		return -1;
   }
    

}
