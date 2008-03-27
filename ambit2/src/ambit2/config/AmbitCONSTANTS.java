/**
 * Created on 2005-2-2
 *
 */
package ambit2.config;
import org.openscience.cdk.CDKConstants;

/**
 * Constants
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitCONSTANTS {
	/**
	 * Version
	 */
	public static final String version = " v2.0 "; 
	/**
	 * CAS RN
	 */
	public static final String CASRN = CDKConstants.CASRN;
	/**
	 * Chemical name
	 */
	public static final String NAMES = CDKConstants.NAMES;
	/**
	 * SMILES
	 */
	public static final String SMILES = "SMILES";
	/**
	 * Unique SMILES
	 */
	public static final String UNIQUE_SMILES = "USMILES";
	/**
	 * Time to generate an unique smiles
	 */
	public static final String UNIQUE_SMILES_TIME = "USMILESTIME";
	/**
	 * Chemical Formula
	 */
	public static final String FORMULA = "FORMULA";
	/**
	 * Structure tyoe
	 */
	public static final String STRUCTURETYPE = "STRUCTURE_TYPE";
	/**
	 * Substance type
	 */
	public static final String SUBSTANCETYPE = "SUBSTANCE_TYPE";
	/**
	 * Molecular weight
	 */
	public static final String MOLWEIGHT = "MolWeigth";
	/**
	 * Inchi
	 */
	public static final String INCHI = "INChI";
	/**
	 * the database field substance.idsubstance 
	 */
	public static final String AMBIT_IDSUBSTANCE = "AMBIT_IDSUBSTANCE";
	/**
	 * the database field structure.idstructure
	 */
	public static final String AMBIT_IDSTRUCTURE = "AMBIT_ID";
	/**
	 * Dataset source (table struc_dataset)
	 */
	public static final String DATASET = "SOURCE";
	/**
	 * Experimental data record
	 */
	public static final String EXPERIMENT = "EXPERIMENT";
	/**
	 * List of experiments
	 */
	public static final String EXPERIMENT_LIST = "EXPERIMENT_LIST";
	/**
	 * Predicted data record
	 */
	public static final String PREDICTED = "PREDICTED";
	/**
	 * QSAR point
	 */
	public static final String QSARPOINT = "QSARPOINT";  //Training or Validation
	/**
	 * AQUIRE data (TO DO)
	 */
	public static final String AQUIRE = "AQUIRE";
	/**
	 * Daylight - like hashed fingerprint
	 */
	public static final String Fingerprint = "org.openscience.cdk.fingerprint.Fingerprinter";
	/**
	 * Time to generate hashed fingerprint
	 */
	public static final String FingerprintTIME = "FP_TIME";
	/**
	 * Atom environment (circular fingerprints)
	 */
	public static final String AtomEnvironment = "ambit2.data.molecule.AtomEnvironmentDescriptor";
	/**
	 * Ttanimoto coefficient
	 */
	public static final String Tanimoto = "Tanimoto";
	/**
	 * Pairwise similarity
	 */
	public static final String PairwiseSimilarity = "PairwiseSimilarity";
	/**
	 * Atom environment similarity
	 */
	public static final String AESimilarity = "Atom environment similarity";

	/**
	 * SMARTS Query
	 */
	public static final String SMARTSQuery = "SMARTSQuery";
	
	/**
	 * max heavy atoms  for skipping huge compounds
	 */
	public static final int maxHeavyAtoms = 70;
	/**
	 * max H atoms  for skipping huge compounds
	 */
	public static final int  maxHAtoms = 70;
	/**
	 * max cyclic bonds for skipping huge compounds
	 */
	public static final int  maxCyclicBonds = 37;
	
	/**
	 * 
	 */
	private AmbitCONSTANTS() {

	}
	
}
