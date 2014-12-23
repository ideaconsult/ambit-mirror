package ambit2.base.data.substance;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;

public class SubstanceName  extends Property {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4436949404781828869L;
	public SubstanceName() {
		super("Name", LiteratureEntry.getTradeNameReference());
		setLabel(opentox_TradeName);
		setEnabled(true);
		setOrder(1);
		setClazz(String.class);
	}
	@Override
	public String getRelativeURI() {
		return "/identifier/tradename";
	}

}