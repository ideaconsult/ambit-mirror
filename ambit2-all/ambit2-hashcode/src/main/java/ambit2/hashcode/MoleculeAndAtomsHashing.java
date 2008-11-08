package ambit2.hashcode ;
import java.util.*;


import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.structgen.stochastic.operator.ChemGraph;

public class MoleculeAndAtomsHashing {
	int bonded_neighbor_atomsPrime  ;
    int bonded_hydrogen_neighbor_atomsPrime ;
    int atomic_numberPrime ;
    int number_of_atomsPrime ;   
    int number_rings;
    //number of atoms in molecule
    int number_of_atoms;
    int atomic_number;
    int bonded_neighbor_atoms;
    int bonded_hydrogen_neighbor_atoms;
    List neighbors, mAtoms;
    Hashtable seedHashtable;    
    Hash atomsHash,atomHash,moleculeSizeHash,moleculeHash;
    
	public  MoleculeAndAtomsHashing(){
	 this.seedHashtable= Prime.createPrimeNumberHashtable();
	}
    
    
    
    public long getMoleculeHash( IAtomContainer mol){
    	ChemGraph ch_graph = new ChemGraph(mol);	    
        number_of_atoms = ch_graph.getNumAtoms();
        
        Iterator<IAtom> atoms = mol.atoms();
        IAtom atom, neighbor;
        atomsHash = new Hash();
        while (atoms.hasNext()) {
        	atom = (IAtom)atoms.next();
        	atomic_number = atom.getAtomicNumber();
        	neighbors = mol.getConnectedAtomsList(atom);
        	bonded_neighbor_atoms = neighbors.size();
        	bonded_hydrogen_neighbor_atoms = 0;
    		for (int f = 0; f < neighbors.size(); f++){    			
    			neighbor = (IAtom)neighbors.get(f);
    			if (neighbor.getSymbol().equals("H")){
    				bonded_hydrogen_neighbor_atoms++;
    			}
    			
    		}
    		atomic_numberPrime = (Integer)this.seedHashtable.get("SysNum"+atomic_number);
    		bonded_neighbor_atomsPrime = (Integer)this.seedHashtable.get("NAtoms"+bonded_neighbor_atoms);
    		bonded_hydrogen_neighbor_atomsPrime = (Integer)seedHashtable.get("NHAtoms"+bonded_hydrogen_neighbor_atoms);
    		atomHash = new Hash(atomic_numberPrime+bonded_neighbor_atomsPrime+bonded_hydrogen_neighbor_atomsPrime);
    		atomsHash = atomsHash.sag(atomHash);
        }
        number_of_atomsPrime =  (Integer)seedHashtable.get("MolSize"+number_of_atoms);
        moleculeSizeHash = new Hash(number_of_atomsPrime);
        moleculeHash = atomsHash; 
        moleculeHash = moleculeHash.sag(moleculeSizeHash);
        return moleculeHash.hash_value();
    	
    }

}
