/**
 * Created on 2005-1-25
 *
 */
package ambit2.io;


import java.util.List;

import ambit2.config.AmbitCONSTANTS;
import ambit2.data.descriptors.DescriptorsList;


/**
 * Column types 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public interface IColumnTypeSelection {
	final static int _ctUnknown = 0;
	final static int _ctX = 1;
	final static int _ctYpredicted = 2;
	final static int _ctYobserved = 3;
	final static int _ctYresidual = 4;
	final static int _ctRowID = 5;
	final static int _ctCAS = 6;	
	final static int _ctSMILES = 7;
	final static int _ctChemName = 8;
	final static int _ctEquation = 9;
	int[] _columnTypesI = {0,1,2,3,4,5,6,7,8,9};
	String[] _columnTypesS = {
			"(ignore)","X (descriptor)",
			"Y predicted","Y observed","Y residual",
			"RowID",
			AmbitCONSTANTS.CASRN,AmbitCONSTANTS.SMILES,AmbitCONSTANTS.NAMES,
			"Equation"};
	public DescriptorsList getDescriptors(List xnames);
	public String typeToString(int type);

	public int guessType(String descrName);
	public int guessType(String descrName, Object value);
}
