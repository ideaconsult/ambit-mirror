package ambit2.db.substance.study.facet;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.study.Protocol._categories;
import ambit2.base.facet.AbstractFacet;
import ambit2.base.facet.BundleRoleFacet;
import ambit2.base.json.JSONUtils;

public class SubstanceByCategoryFacet extends AbstractFacet<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3229573174462112181L;
	protected BundleRoleFacet bundleRole;


	public BundleRoleFacet getBundleRole() {
		return bundleRole;
	}

	public void setBundleRole(BundleRoleFacet bundleRole) {
		this.bundleRole = bundleRole;
	}


	protected String subcategory;
	protected _categories endpoint;
	protected int substancesCount = 0;
		
	public int getSubstancesCount() {
		return substancesCount;
	}

	public void setSubstancesCount(int substancesCount) {
		this.substancesCount = substancesCount;
	}

	public _categories getEndpoint() {
		return endpoint;
	}

	@Override
	public void setValue(String value) {
		try {
			endpoint = _categories.valueOf(value);
			value = endpoint.getNumber() + ". " + endpoint.toString();
		} catch (Exception x) {
			endpoint = null;
			super.setValue(value);
		}
	}
	
	@Override
	public String getValue() {
		return endpoint==null?super.getValue():(endpoint.getNumber() + ". " + endpoint.toString());
	}
	
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
				subcategory
			    );	

	}
	
	@Override
	public String toJSON(String uri,String subcategory) {
		return String.format("\n\t{\n\t\"value\":%s,\t\"endpoint\":%s,\n\t\"count\":%s,\n\t\"substancescount\":%s,\n\t\"uri\":%s,\n\t\"subcategory\":%s,\t\"subcategoryuri\":%s,\n\t\"bundles\":{%s}\n\t}",
			getValue()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getValue().toString())),
			endpoint==null?"null":JSONUtils.jsonQuote(JSONUtils.jsonEscape(endpoint.name())),
			JSONUtils.jsonNumber(getCount()),
			JSONUtils.jsonNumber(getSubstancesCount()),
			uri==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(uri)),
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubcategoryTitle())),	
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubCategoryURL(subcategory))),
			bundleRole==null?"":bundleRole.toJSON(bundleRole.getURL(), "")
			);
	}
	
	
	protected List<BundleRoleFacet> bundles;
	
	public void addFacet(BundleRoleFacet facet) {
		if (bundles == null) bundles = new ArrayList<BundleRoleFacet>();
		bundles.add(facet);
	}
	public void clearFacets() {
		if (bundles!=null) bundles.clear();
		
	}
	public Iterable<BundleRoleFacet> getFacets() {
		return bundles;
	}
	public void removeFacet(BundleRoleFacet facet) {
		if (bundles!=null) bundles.remove(facet);
	}
}
