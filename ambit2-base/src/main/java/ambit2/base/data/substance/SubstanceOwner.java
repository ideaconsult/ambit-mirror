package ambit2.base.data.substance;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;

public class SubstanceOwner   extends Property {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7547507885884497869L;
	/**
	 * 
	 */

	public SubstanceOwner() {
		super("Data source", LiteratureEntry.getOwnerNameReference());
		setLabel(opentox_SubstanceDataSource);
		setEnabled(true);
		setOrder(4);
		setClazz(String.class);
	}
	@Override
	public String getRelativeURI() {
		return "/owner/name";
	}

}