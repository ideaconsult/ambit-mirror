package ambit2.db.substance.study.facet;

import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.json.JSONUtils;


public class ResultsCountFacet<E> extends SubstanceByCategoryFacet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1301400016402834823L;
	protected E result;
	
	public E getResult() {
		return result;
	}

	public void setResult(E effect) {
		this.result = effect;
	}

	public ResultsCountFacet(String facetURL, E result) {
		super(facetURL);
		this.result = result;

	}
	@Override
	public String toJSON(String uri, String subcategory) {
		if (result instanceof EffectRecord)
			return String.format("\n\t{\n\t\"top\":%s,\n\t\"category\":%s,\n\t\"value\":%s,\n\t\"endpoint\":%s,\n\t\"count\":%d\n\t}",
					JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubcategoryTitle())),	
					endpoint==null?"null":JSONUtils.jsonQuote(JSONUtils.jsonEscape(endpoint.name())),
					getValue()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getValue().toString())),
					JSONUtils.jsonQuote(JSONUtils.jsonEscape(((EffectRecord<String,Object,String>)result).getEndpoint())),
					getCount()
					);
		else if (result instanceof ProtocolApplication) {
			return String.format("\n\t{\n\t\"top\":%s,\n\t\"category\":%s,\n\t\"value\":%s,\n\t\"interpretation_result\":%s,\n\t\"count\":%d\n\t}",
					JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubcategoryTitle())),	
					endpoint==null?"null":JSONUtils.jsonQuote(JSONUtils.jsonEscape(endpoint.name())),
					getValue()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getValue().toString())),
					JSONUtils.jsonQuote(JSONUtils.jsonEscape(((ProtocolApplication)result).getInterpretationResult())),
					getCount()
					);			
		}
		else return super.toJSON(uri, subcategory);
	}
	

}