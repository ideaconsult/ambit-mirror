/**
 * <b>Filename</b> AtomEnvironmentDistanceType.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-23
 * <b>Project</b> ambit
 */
package ambit2.model.structure;


/**
 * Defines how to compare {@link ambit2.data.descriptors.AtomEnvironmentDescriptor}.
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-23
 */
public enum AtomEnvironmentDistanceType {
   // public static final String[] fpComparison = {,};
    
	_consensus_fingerprint_hellinger {
		@Override
		public String getName() {
			return "Consensus fingerprint, Hellinger distance";
		}

	},
	_kNN_tanimoto {
		@Override
		public String getName() {
			return "kNN, Average (1-tanimoto) distance";
		}

	},
	;
	public abstract String getName();
	@Override
	public String toString() {
		return getName();
	}    
    
}

