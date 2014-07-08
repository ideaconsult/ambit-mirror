package ambit2.core.processors.structure.key;

import ambit2.base.data.Property;

public class DSSToxRID extends DSSToxID {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2117618200041344844L;
	/**
	 * 
	 */

	public DSSToxRID() {

		super(Property.getInstance("DSSTox_RID","DSSTox", "http://www.epa.gov/NCCT/dsstox/StandardChemFieldDefTable.html#DSSTox_RID"));
	}


}