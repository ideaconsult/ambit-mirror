package ambit2.db.substance.study.facet;

import ambit2.base.data.I5Utils;
import ambit2.base.json.JSONUtils;

public class SubstanceDataAvailabilityFacet extends SubstanceByCategoryFacet {
	protected String substanceid;

	public String getSubstanceID() {
		return substanceid;
	}

	public void setSubstanceID(String prefix, String uuid) {
		this.substanceid = I5Utils.getPrefixedUUID(prefix, uuid);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7344551689563932326L;

	public SubstanceDataAvailabilityFacet(String facetURL) {
		super(facetURL);
	}

	
	@Override
	public String getSubCategoryURL(String... params) {
		if (getURL()==null) return null;
		String root = params.length>0?params[0]:"";
		
		return String.format("%s/substance/%s/study?top=%s",
				root,
				substanceid,
				getSubcategoryTitle()
			    );	

	}
	
	@Override
	public String toJSON(String uri, String subcategory) {
		return String.format(
				"\n\t{\n\t\"value\":%s,\t\"endpoint\":%s,\n\t\"count\":%s,\n\t\"substancescount\":%s,\n\t\"uri\":%s,\n\t\"subcategory\":%s,\t\"subcategoryuri\":%s,\t\"substance\":%s\t}",
				getValue() == null ? null : JSONUtils.jsonQuote(JSONUtils.jsonEscape(getValue().toString())),
				endpoint == null ? "null" : JSONUtils.jsonQuote(JSONUtils.jsonEscape(endpoint.name())),
				JSONUtils.jsonNumber(getCount()), JSONUtils.jsonNumber(getSubstancesCount()),
				uri == null ? null : JSONUtils.jsonQuote(JSONUtils.jsonEscape(uri)),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubcategoryTitle())),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubCategoryURL(subcategory))),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubstanceID())));
	}

}
