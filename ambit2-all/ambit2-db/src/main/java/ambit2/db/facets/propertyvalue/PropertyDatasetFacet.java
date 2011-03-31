package ambit2.db.facets.propertyvalue;

import ambit2.base.facet.AbstractFacet;

public class PropertyDatasetFacet<PROPERTY,DATASET> extends AbstractFacet<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2260081381044742166L;
	protected PROPERTY property;
	protected DATASET dataset;

	public PropertyDatasetFacet(String url) {
		super(url);
	}
	
	public PROPERTY getProperty() {
		return property;
	}

	public void setProperty(PROPERTY property) {
		this.property = property;
	}


	
	public DATASET getDataset() {
		return dataset;
	}

	public void setDataset(DATASET dataset) {
		this.dataset = dataset;
	}
	@Override
	public String toString() {
		return String.format("%s (%d)",getValue(),getCount());
	}


}
