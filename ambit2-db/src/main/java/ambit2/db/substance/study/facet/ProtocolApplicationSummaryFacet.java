package ambit2.db.substance.study.facet;

import ambit2.base.json.JSONUtils;


public class ProtocolApplicationSummaryFacet extends SubstanceByCategoryFacet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8084077562134464946L;

	protected String guidance;
	public String getGuidance() {
		return guidance;
	}

	public void setGuidance(String guidance) {
		this.guidance = guidance;
	}

	protected String interpretation_result;
	
	public String getInterpretation_result() {
		return interpretation_result;
	}

	public void setInterpretation_result(String interpretation_result) {
		this.interpretation_result = interpretation_result;
	}

	public ProtocolApplicationSummaryFacet(String facetURL) {
		super(facetURL);
	}
	
	@Override
	public String toJSON(String uri,String subcategory) {
		return String.format("\n\t{\n\t\"value\":%s,\t\"endpoint\":%s,\n\t\"count\":%s,\n\t\"substancescount\":%s,\n\t\"uri\":%s,\n\t\"subcategory\":%s,\t\"subcategoryuri\":%s,\t\"protocol\":%s,\t\"interpretation_result\":%s,\n\t\"bundles\":{%s}\n\t}",
			getValue()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getValue().toString())),
			endpoint==null?"null":JSONUtils.jsonQuote(JSONUtils.jsonEscape(endpoint.name())),
			JSONUtils.jsonNumber(getCount()),
			JSONUtils.jsonNumber(getSubstancesCount()),
			uri==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(uri)),
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubcategoryTitle())),	
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubCategoryURL(subcategory))),
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(guidance)),
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(interpretation_result)),
			bundleRole==null?"":bundleRole.toJSON(bundleRole.getURL(), "")
			);
	}
		
	
}