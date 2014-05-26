package ambit2.db.substance.study.facet;

import ambit2.base.facet.AbstractFacet;

public class SubstanceByCategoryFacet extends AbstractFacet<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3229573174462112181L;
	protected String subcategory;
	public void setSubcategoryTitle(String subcategory) {
		this.subcategory = subcategory;
	}
	@Override
	public String getSubcategoryTitle() {
		return subcategory;
	}
	
	public SubstanceByCategoryFacet(String facetURL) {
		super(facetURL);
	}
	
	@Override
	public String getSubCategoryURL(String... params) {
		if (getURL()==null) return null;
		String root = params.length>0?params[0]:"";
		
		return String.format("%s/substance?type=topcategory&search=%s",
				root,
				getSubcategoryTitle().toString()
			    );	

	}
}
