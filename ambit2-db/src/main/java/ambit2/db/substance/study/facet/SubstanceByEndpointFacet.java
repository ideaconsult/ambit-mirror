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
		return String.format("\n\t{\n\t\"top\":%s,\n\t\"category\":%s,\n\t\"value\":%s,\n\t\"endpoint\":%s,\n\t\"count\":%d\n\t}",
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubcategoryTitle())),	
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(endpoint.name())),
				getValue()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getValue().toString())),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(effect.getEndpoint())),
				getCount()
				);
	}
	

}