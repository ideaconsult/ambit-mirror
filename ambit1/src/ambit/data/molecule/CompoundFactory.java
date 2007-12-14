/***
 * Created on 2005-3-24

 */
package ambit.data.molecule;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit.misc.AmbitCONSTANTS;


/**
 * Provides static function to create several {@link ambit.data.molecule.Compound}s
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class CompoundFactory {

	/**
	 * 
	 */
	protected CompoundFactory() {
		super();

	}
	/**
	 * Creates Benzene with CAS, SMILES, name and structure
	 * @return {@link Compound}
	 */
	public static Compound createBenzene() {
		Compound mol = new Compound(MoleculeFactory.makeBenzene());
		mol.setCAS_RN("71-43-2");
		mol.setSMILES("c1ccccc1");
		mol.setName("Benzene");	
		return mol;
	}
	/**
	 * Creates 4Aminobiphenyl with CAS, SMILES, name and structure	 * 
	 * @return {@link Compound}
	 */
	public static Compound create4Aminobiphenyl() {
		Compound mol = new Compound(create4AminobiphenylMol());
	
		return mol;
	}
	public static IMolecule create4AminobiphenylMol() {
	    IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
	
	    mol.setProperty(AmbitCONSTANTS.SMILES,"Nc1ccc(cc1)c2ccccc2");
		mol.setProperty(CDKConstants.CASRN,"92-67-1");
		mol.setProperty(CDKConstants.NAMES,"4-Aminobiphenyl");
		return mol;
	}	
	/**
	 * Creates 2Aminonaphtalene with CAS, SMILES, name and structure	 * 
	 * @return {@link Compound}
	 */
	public static Compound create2Aminonaphthalene() {
		Compound mol = new Compound(create2AminonaphthaleneMol());
		return mol;
	}
	public static IMolecule create2AminonaphthaleneMol() {
		IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
		mol.setProperty(AmbitCONSTANTS.SMILES,"Nc1ccc2ccccc2(c1)");
		mol.setProperty(CDKConstants.CASRN,"91-59-8");
		mol.setProperty(CDKConstants.NAMES,"2-Aminonaphthalene");	
		return mol;
	}	
	/**
	 * Creates 2Aminofluorene with CAS, SMILES, name and structure	 * 
	 * @return {@link Compound}
	 */
	public static Compound create2Aminofluorene() {
		Compound mol = new Compound(create2AminofluoreneMol());
		return mol;
	}
	public static IMolecule create2AminofluoreneMol() {
		IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
		mol.setProperty(AmbitCONSTANTS.SMILES,"Nc2ccc3c1ccccc1Cc3(c2)");
		mol.setProperty(CDKConstants.CASRN,"153-78-6");
		mol.setProperty(CDKConstants.NAMES,"2-AMINOFLUORENE");		
		return mol;
	}	
}
