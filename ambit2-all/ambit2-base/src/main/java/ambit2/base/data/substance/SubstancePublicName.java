package ambit2.base.data.substance;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;

public class SubstancePublicName extends Property {

	public SubstancePublicName() {
		super(Property.Names,LiteratureEntry.getInstance("Substance",""));
		setLabel(Property.opentox_Name);
		setEnabled(true);
		setOrder(2);
		setClazz(String.class);
	}
	
	@Override
	public String getRelativeURI() {
		return "/identifier/name";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3780278515180239734L;

}
