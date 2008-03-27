/**
 * Created on 2005-4-4
 *
 */
package ambit2.domain;

import ambit2.data.AmbitObjectFixedValues;



/**
 * Fingerprint mode comparison 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class FingerprintDistanceType extends AmbitObjectFixedValues {
	public static final String[] fpComparison = {"Missing fragments","1-Tanimoto"};
	/**
	 * 
	 */
	public FingerprintDistanceType() {
		super();

	}
	/**
	 * 
	 */
	public FingerprintDistanceType(int id) {
		super(id);

	}

	/**
	 * @param name
	 */
	public FingerprintDistanceType(String name) {
		super(name);
	}

	/**
	 * @param name
	 * @param id
	 */
	public FingerprintDistanceType(String name, int id) {
		super(name, id);
	}
	public String[] predefinedvalues() {
		return fpComparison;
	}		
}
