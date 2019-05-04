package net.idea.ambit.templates.db;

import ambit2.base.facet.AbstractFacet;
import ambit2.base.json.JSONUtils;
import net.enanomapper.maker.TR;

public class AssayTemplateFacet extends AbstractFacet<String> {

	protected TR record;

	public TR getRecord() {
		return record;
	}

	public void setRecord(TR record) {
		this.record = record;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3563919985055501735L;

	@Override
	public String toString() {
		return String.format("%s (%d)", value == null ? "" : getValue(), count);
	}

	@Override
	public String getTitle() {
		return TR.hix.Sheet.get(record).toString();
	}

	@Override
	public String getSubcategoryTitle() {
		try {
			Object o = TR.hix.endpoint.get(record);
			return o == null ? null : o.toString();
		} catch (Exception x) {
			return null;
		}

	}
	@Override
	public String toJSON(String uri,String subcategory) {
		return String.format("\n\t{\n\t\"value\":%s,\n\t\"count\":%s,\n\t\"subcategory\":%s,\t\"title\":%s\n\t}",
			getValue()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getValue().toString())),
			JSONUtils.jsonNumber(getCount()),
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubcategoryTitle())),	
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(getTitle()))
			);
	}
}
