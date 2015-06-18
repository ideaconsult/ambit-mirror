package ambit2.base.facet;

import java.net.URLEncoder;

import ambit2.base.json.JSONUtils;

public class SubstanceStudyFacet extends AbstractFacet<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9014078415852427522L;
	protected String subcategory;
	protected int sortingOrder;
	protected String interpretation_result;

	public String getInterpretation_result() {
		return interpretation_result;
	}

	public void setInterpretation_result(String interpretation_result) {
		this.interpretation_result = interpretation_result;
	}

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
		if ((params != null) && (params.length > 0))
			return params[0];
		else
			return null;
	}

	@Override
	public String toJSON(String uri, String subcategory) {
		StringBuilder output = new StringBuilder();
		uri = uri + "/study";
		output.append("\n\t{");
		try {

			output.append(String
					.format("\n\t\"topcategory\": {\n\t\t\t\"title\": "));
			output.append(getValue() == null ? "null" : JSONUtils
					.jsonQuote(JSONUtils.jsonEscape(getValue().toString())));
			output.append(",\n\t\t\t\"uri\":");
			output.append(JSONUtils.jsonQuote(JSONUtils
					.jsonEscape(uri
							+ "?top="
							+ (getValue() == null ? "" : URLEncoder
									.encode(getValue().toString())))));

			output.append("\n\t},\n\t\"category\":  {\n\t\t\t\"title\":");
			String v = JSONUtils.jsonQuote(JSONUtils
					.jsonEscape(getSubcategoryTitle()));
			output.append(v == null ? "null" : v);

			output.append(",\n\t\t\t\"description\":");

			v = null;
			try {
				v = ambit2.base.data.study.Protocol._categories.valueOf(
						getSubcategoryTitle()).toString();
				v = JSONUtils.jsonQuote(JSONUtils.jsonEscape(v));
			} catch (Exception x) {
				v = JSONUtils.jsonQuote(JSONUtils
						.jsonEscape(getSubcategoryTitle()));
			}
			output.append(v == null ? "null" : v);
			// change description to order, but leave the former for
			// compatibility
			output.append(",\n\t\t\t\"order\":");
			output.append(getSortingOrder());

			output.append(",\n\t\t\t\"uri\":");
			String suri = getSubCategoryURL(uri)
					+ "?top="
					+ (getValue() == null ? "" : URLEncoder.encode(getValue()
							.toString()))
					+ "&category="
					+ (getSubcategoryTitle() == null ? "" : URLEncoder
							.encode(getSubcategoryTitle()));
			output.append(suri == null ? "null" : JSONUtils.jsonQuote(JSONUtils
					.jsonEscape(suri)));
			output.append("\n\t}");

			output.append(",\n\t\"interpretation_result\":");
			v = JSONUtils.jsonQuote(JSONUtils
					.jsonEscape(getInterpretation_result()));
			output.append(v == null ? "null" : v);

			output.append(",\n\t\"count\":");
			output.append(Integer.toString(getCount()));

		} catch (Exception x) {

		} finally {
			output.append("\n\t}");
		}
		return output.toString();
	}

}
