package ambit2.db.substance.study.facet;

import ambit2.base.facet.AbstractFacet;

public class SubstanceStudyFacet extends AbstractFacet<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9014078415852427522L;
	protected String subcategory;
	protected int sortingOrder;

	public int getSortingOrder() {
		return sortingOrder;
	}
	public void setSortingOrder(int sortingOrder) {
		this.sortingOrder = sortingOrder;
	}
	public void setSubcategoryTitle(String subcategory) {
		this.subcategory = subcategory;
	}
	public SubstanceStudyFacet(String url) {
		super(url);
	}
	@Override
	public String getResultsURL(String... params) {
		return getURL();
	}
	@Override
	public String getSubcategoryTitle() {
		return subcategory;
	}
	@Override
	public String getSubCategoryURL(String... params) {
		if ((params!=null) && (params.length>0)) return params[0];
		else return null;
	}
	
}
