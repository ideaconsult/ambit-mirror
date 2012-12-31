package ambit2.core.processors.structure.key;

import ambit2.base.data.Property;

/**
 * DSSTox_CID
 * @author nina
 *
 */
public class DSSToxCID extends DSSToxID {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5172763951258433726L;
	public DSSToxCID() {
		super(Property.getInstance("DSSTox_CID","DSSTox", "http://www.epa.gov/NCCT/dsstox/StandardChemFieldDefTable.html#DSSTox_CID"));
	}


}
