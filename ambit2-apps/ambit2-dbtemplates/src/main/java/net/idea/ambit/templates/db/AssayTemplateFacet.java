package net.idea.ambit.templates.db;

import ambit2.base.facet.AbstractFacet;
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
		return TR.hix.endpoint.get(record).toString();
	}

}
