package ambit2.base.facet;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.json.JSONUtils;

public class BundleRoleFacet extends AbstractFacet<SubstanceEndpointsBundle> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4897070929247809353L;
	protected String tag;
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	protected String remarks;
	
	public BundleRoleFacet(String url) {
		super(url);
	}
	@Override
	public String toJSON(String uri,String subcategory) {
		return String.format("%s: {\"count\": %d, \"tag\": %s, \"remarks\": %s}\n",
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(uri + "/bundle/" + getValue().getID())),
			getCount(),
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(getTag())),
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(getRemarks()))
			);
	}
	@Override
	public String getSubcategoryTitle() {
		return tag;
	}
	@Override
	public String getSubCategoryURL(String... params) {
		return url;
	}
	@Override
	public String getResultsURL(String... params) {
		return url;
	}
	public void clear() {
		value  = null;
		tag = null;
		remarks = null;
		count = 0;
		
	}
}
