package ambit2.db.substance.study.facet;

import ambit2.base.facet.AbstractFacet;

public class OwnerFacet  extends AbstractFacet<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5464948093969109879L;
	protected String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() { return "Substance owner"; };
	public String getSubcategoryTitle() { return name;};
	@Override
	public String getSubCategoryURL(String... params) {
		return name;
	}
};