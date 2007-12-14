/**
 * Created on 2005-3-28
 *
 */
package ambit.data.molecule;

import ambit.data.AmbitObject;

/**
 * 
 *
 * CompoundType corresponds to STYPE table in Ambit Database
 * @author Nina Jeliazkova
 */
public class CompoundType extends AmbitObject {
	public static final String[] substanceType = 
		{"organic","inorganic","mixture/unknown","organometallic"};
	public static final int substTypeOrganic = 1;	
	public static final int substTypeInorganic = 2;
	public static final int substTypeMixture = 3;
	public static final int substTypeMetallic = 4;
	
	/**
	 * 
	 */
	public CompoundType() {
		super("organic");
	}

	/**
	 * @param name
	 */
	public CompoundType(String name) {
		super(name);
	}

	/**
	 * @param name
	 * @param id
	 */
	public CompoundType(String name, int id) {
		super(name, id);
	}
	public CompoundType(int id) {
		super("", id);
		setId(id);
	}	
	public boolean isPredefined() {
		return true;
	}
	public String[] predefinedvalues() {
		return substanceType;
	}	
	public void setId(int id) {
		if ((id < 1) || (id > substanceType.length)) id = 1;
		super.setId(id);
		name = substanceType[id-1];
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		for (int i = 0; i < substanceType.length; i++)
			if (name.equals(substanceType[i])) {
				setId(i+1);
			}
	}	

}
