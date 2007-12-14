/**
 * Created on 2005-2-2
 *
 */
package ambit.misc;
import org.openscience.cdk.CDKConstants;

/**
 * Constants
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitCONSTANTS {
	public static final String version = " v0.04 "; 
	//
	public static String CASRN = CDKConstants.CASRN;
	public static String NAMES = CDKConstants.NAMES;
	public static String SMILES = "SMILES";
	public static String UNIQUE_SMILES = "USMILES";
	public static String UNIQUE_SMILES_TIME = "USMILESTIME";
	public static String FORMULA = "FORMULA";
	public static String STRUCTURETYPE = "STRUCTURE_TYPE";
	public static String SUBSTANCETYPE = "SUBSTANCE_TYPE";
	public static String MOLWEIGHT = "MolWeigth";
	public static String INCHI = "INChI";
	
	public static String AMBIT_IDSUBSTANCE = "AMBIT_IDSUBSTANCE";	
	public static String AMBIT_IDSTRUCTURE = "AMBIT_ID";
	public static String DATASET = "SOURCE";
	public static String EXPERIMENT = "EXPERIMENT";
	public static String EXPERIMENT_LIST = "EXPERIMENT_LIST";
	public static String PREDICTED = "PREDICTED";
	public static String QSARPOINT = "QSARPOINT";  //Training or Validation
	
	public static String AQUIRE = "AQUIRE";
	
	public static String Fingerprint = "org.openscience.cdk.fingerprint.Fingerprinter";
	public static String FingerprintTIME = "FP_TIME";
	public static String AtomEnvironment = "ambit.data.molecule.AtomEnvironmentDescriptor";
	public static String Tanimoto = "Tanimoto";
	public static String PairwiseSimilarity = "PairwiseSimilarity";
	public static String AESimilarity = "Atom environment similarity";
	
	/**
	 * 
	 */
	protected AmbitCONSTANTS() {
		super();
	}
	
}
