/**
 * Created on 2005-4-4
 *
 */
package ambit2.model.structure;




/**
 * Fingerprint mode comparison 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public enum FingerprintDistanceType {

	_missing_fragments {
		@Override
		public String getName() {
			return "Missing fragments";
		}

	},
	_tanimoto {
		@Override
		public String getName() {
			return "1-Tanimoto";
		}

	},
	;
	public abstract String getName();
	@Override
	public String toString() {
		return getName();
	}    
    
}
