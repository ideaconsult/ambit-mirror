package ambit2.db.substance.study.facet;

import ambit2.base.data.study.EffectRecord;
import ambit2.base.json.JSONUtils;


public class SubstanceByEndpointFacet extends SubstanceByCategoryFacet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1301400016402834823L;
	protected EffectRecord<String,Object,String> effect;
	
	public EffectRecord<String, Object, String> getEffect() {
		return effect;
	}

	public void setEffect(EffectRecord<String, Object, String> effect) {
		this.effect = effect;
	}

	public SubstanceByEndpointFacet(String facetURL) {
		super(facetURL);
		effect = new EffectRecord<String, Object, String>();
	}
	@Override
	public String toJSON(String uri, String subcategory) {
		return String.format("\n\t{\n\t\"value\":%s,\t\"endpoint\":%s,\n\t\"count\":%d,\n\t\"uri\":%s,\n\t\"subcategory\":%s,\t\"subcategoryuri\":%s\n,\"effect\":%s\t}",
				getValue()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getValue().toString())),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(endpoint.name())),
				getCount(),
				uri==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(uri)),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubcategoryTitle())),	
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubCategoryURL(subcategory))),
				effect.toString()
				);
	}
	

}