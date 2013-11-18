package ambit2.db.substance.study.facet;

import ambit2.base.facet.AbstractFacet;

public class SubstanceStudyFacet extends AbstractFacet<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9014078415852427522L;

	public SubstanceStudyFacet(String url) {
		super(url);
	}
	@Override
	public String getResultsURL(String... params) {
		return getURL();
	}
	@Override
	public String getSubcategoryTitle() {
		// TODO Auto-generated method stub
		return super.getSubcategoryTitle();
	}
	@Override
	public String getSubCategoryURL(String... params) {
		// TODO Auto-generated method stub
		return super.getSubCategoryURL(params);
	}
	
}
