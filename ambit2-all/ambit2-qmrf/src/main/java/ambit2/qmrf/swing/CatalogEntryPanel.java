package ambit2.qmrf.swing;

import java.util.ArrayList;

import ambit2.qmrf.catalogs.CatalogEntry;

public class CatalogEntryPanel extends QMRFAttributesPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6376839926198844553L;
	protected CatalogEntry catalogEntry;
	public CatalogEntryPanel(CatalogEntry catalogEntry) {
		super(catalogEntry.getAttributes());
		this.catalogEntry = catalogEntry;
	}

	public CatalogEntryPanel(CatalogEntry catalogEntry, String[] fields,
			String[] fieldNames) {
		super(catalogEntry.getAttributes(), fields, fieldNames);
		this.catalogEntry = catalogEntry;
	}

	public CatalogEntryPanel(CatalogEntry catalogEntry,
			ArrayList<String> fields, ArrayList<String> fieldNames) {
		super(catalogEntry.getAttributes(), fields, fieldNames);
		this.catalogEntry = catalogEntry;
	}
	@Override
	protected void update(String key, String value) {
		catalogEntry.setproperty(key, value);
	}

}
