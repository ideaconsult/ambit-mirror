package ambit2.base.data.substance;

import java.util.UUID;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;

public class SubstanceProperty extends Property {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4436949404781828869L;
	public SubstanceProperty(String name, String reference) {
		super(name,LiteratureEntry.getInstance(reference,""));
		setLabel(name);
		setEnabled(true);
	}
	@Override
	public String getVisibleIdentifier() {
		return UUID.nameUUIDFromBytes((getName() + getTitle()).toString().getBytes()).toString();
	}

}