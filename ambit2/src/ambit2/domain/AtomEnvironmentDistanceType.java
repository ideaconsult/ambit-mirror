/**
 * <b>Filename</b> AtomEnvironmentDistanceType.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-23
 * <b>Project</b> ambit
 */
package ambit2.domain;

import ambit2.data.AmbitObjectFixedValues;

/**
 * Defines how to compare {@link ambit2.data.descriptors.AtomEnvironmentDescriptor}.
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-23
 */
public class AtomEnvironmentDistanceType extends AmbitObjectFixedValues {
    public static final String[] fpComparison = {"Consensus fingerprint, Hellinger distance","kNN, Average (1-tanimoto) distance"};
    /**
     * 
     */
    public AtomEnvironmentDistanceType() {
        super();
    }

    /**
     * @param name
     */
    public AtomEnvironmentDistanceType(String name) {
        super(name);

    }

    /**
     * @param id
     */
    public AtomEnvironmentDistanceType(int id) {
        super(id);
    }

    /**
     * @param name
     * @param id
     */
    public AtomEnvironmentDistanceType(String name, int id) {
        super(name, id);
    }
	public String[] predefinedvalues() {
		return fpComparison;
	}
}

