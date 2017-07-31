package ambit2.db.substance.study.facet;

import java.util.List;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.json.JSONUtils;

public class SubstanceDataAvailabilityFacet extends SubstanceByCategoryFacet {
	protected SubstanceRecord record = new SubstanceRecord();

	public SubstanceRecord getRecord() {
		return record;
	}

	public void setRecord(SubstanceRecord record) {
		this.record = record;
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
				record.getSubstanceUUID(),
				getSubcategoryTitle()
			    );	

	}
	
	@Override
	public String toJSON(String uri, String subcategory) {
		List<ProtocolApplication> papps = record.getMeasurements();
		ProtocolApplication<Protocol,Object,String,Object,String> papp = papps==null || papps.size()==0?null:papps.get(0);
		return String.format(
				"\n\t{\n\t\"value\":%s,\t\"endpoint\":%s,\n\t\"count\":%s,\n\t\"substancescount\":%s,\n\t\"uri\":%s,\n\t\"subcategory\":%s,\t\"subcategoryuri\":%s,\t\"substance\":%s,\t\"reference\":%s,\t\"provider\":%s,\t\"protocol\":%s,\t\"publicname\":%s,\t\"substancetype\":%s,\t\"substance_uuid\":%s,\t\"substance_owner\":%s\t}",
				getValue() == null ? null : JSONUtils.jsonQuote(JSONUtils.jsonEscape(getValue().toString())),
				endpoint == null ? "null" : JSONUtils.jsonQuote(JSONUtils.jsonEscape(endpoint.name())),
				JSONUtils.jsonNumber(getCount()), JSONUtils.jsonNumber(getSubstancesCount()),
				uri == null ? null : JSONUtils.jsonQuote(JSONUtils.jsonEscape(uri)),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubcategoryTitle())),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubCategoryURL(subcategory))),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(record.getSubstanceName())),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(papp.getReference())),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(papp.getReferenceOwner())),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(papp.getProtocol().getGuideline()==null || papp.getProtocol().getGuideline().size()==0?"":papp.getProtocol().getGuideline().get(0))),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(record.getPublicName())),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(record.getSubstancetype())),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(record.getSubstanceUUID())),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(record.getOwnerName()))
				);
	}

}
