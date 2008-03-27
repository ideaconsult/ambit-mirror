/**
 * Created on 2005-1-25
 *
 */
package ambit2.config;



/**
 * TODO - refactor or get rid of it
 * Column types 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public interface IColumnTypeSelection {
	/**
	 * Unknown 
	 */
	final static int _ctUnknown = 0;
	/**
	 * X descriptor
	 */
	final static int _ctX = 1;
	/**
	 * Predicted (Y)
	 */
	final static int _ctYpredicted = 2;
	/**
	 * Observed
	 */
	final static int _ctYobserved = 3;
	/**
	 * Residual
	 */
	final static int _ctYresidual = 4;
	/**
	 * Row identifier
	 */
	final static int _ctRowID = 5;
	/**
	 * CAS
	 */
	final static int _ctCAS = 6;
	/**
	 * SMILES
	 */
	final static int _ctSMILES = 7;
	/**
	 * Chemname
	 */
	final static int _ctChemName = 8;
	/**
	 * Equation
	 */
	final static int _ctEquation = 9;
	/**
	 * 
	 */
	final int[] _columnTypesI = {0,1,2,3,4,5,6,7,8,9};
	String[] _columnTypesS = {
			"(ignore)","X (descriptor)",
			"Y predicted","Y observed","Y residual",
			"RowID",
			AmbitCONSTANTS.CASRN,AmbitCONSTANTS.SMILES,AmbitCONSTANTS.NAMES,
			"Equation"};
	String typeToString(int type);

	int guessType(String descrName);
	int guessType(String descrName, Object value);
}
