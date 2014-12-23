package ambit2.base.data.substance;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;

public class SubstanceUUID extends Property {

	/**
	 * 
	 */
	private static final long serialVersionUID = 803512807002064994L;
	public SubstanceUUID() {
		super(Property.IUCLID5_UUID, LiteratureEntry.getI5UUIDReference());
		setLabel(opentox_IUCLID5_UUID);
		setOrder(3);
		setEnabled(true);
		setClazz(String.class);
	}
	@Override
	public String getRelativeURI() {
		return "/identifier/uuid";
	}

}