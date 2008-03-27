package ambit2.data.qmrf;

import java.util.ArrayList;

public class CatalogEntryPanel extends QMRFAttributesPanel {
	protected CatalogEntry catalogEntry;
	public CatalogEntryPanel(CatalogEntry catalogEntry) {
		super(catalogEntry.attributes);
		this.catalogEntry = catalogEntry;
	}

	public CatalogEntryPanel(CatalogEntry catalogEntry, String[] fields,
			String[] fieldNames) {
		super(catalogEntry.attributes, fields, fieldNames);
		this.catalogEntry = catalogEntry;
	}

	public CatalogEntryPanel(CatalogEntry catalogEntry,
			ArrayList<String> fields, ArrayList<String> fieldNames) {
		super(catalogEntry.attributes, fields, fieldNames);
		this.catalogEntry = catalogEntry;
	}
	@Override
	protected void update(String key, String value) {
		catalogEntry.setproperty(key, value);
	}

}
