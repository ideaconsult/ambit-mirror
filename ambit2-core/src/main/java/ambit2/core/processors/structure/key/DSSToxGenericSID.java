package ambit2.core.processors.structure.key;

import ambit2.base.data.Property;

/**
 * DSSToxGenericSID
 * @author nina
 *
 */
public class DSSToxGenericSID extends DSSToxID {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5172763951258433726L;
	public DSSToxGenericSID() {
		super(Property.getInstance("DSSTox_Generic_SID","DSSTox", "http://www.epa.gov/NCCT/dsstox/StandardChemFieldDefTable.html#DSSTox_Generic_SID"));
	}

}