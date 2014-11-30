package ambit2.db.facets.bundle;

import ambit2.base.facet.AbstractFacet;
import ambit2.base.json.JSONUtils;

public class BundleSummaryFacet  extends AbstractFacet<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1282793950608440487L;
	public BundleSummaryFacet(String url) {
		super(url);
	}
	@Override
	public String toJSON(String uri,String subcategory) {
		return String.format("\n\t{\n\t\"value\":%s,\n\t\"count\":%d,\n\t\"uri\":%s,\n\t\"subcategory\":%s,\t\"subcategoryuri\":%s\n\t}",
			getValue()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getValue().toString())),
			getCount(),
			uri==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(uri+"/"+getValue())),
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubcategoryTitle())),	
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubCategoryURL(subcategory)))
			);
	}
	@Override
	public String getSubcategoryTitle() {
		return "Bundle";
	}
	@Override
	public String getSubCategoryURL(String... params) {
		return url;
	}
	@Override
	public String getResultsURL(String... params) {
		return url;
	}
}
