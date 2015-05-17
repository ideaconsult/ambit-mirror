package ambit2.base.facet;

import net.idea.modbcum.i.facet.IFacet;
import ambit2.base.json.JSONUtils;

public abstract class AbstractFacet<T> implements IFacet<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1486718382701377463L;
	protected String url;
	protected int count;
	public AbstractFacet() {
		this(null);
	}
	public AbstractFacet(String url) {
		this.url = url;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	@Override
	public String getResultsURL(String... params) {
		return null;
	}
	protected T value;
	@Override
	public String getSubCategoryURL(String... params) {
		return null;
	}
	@Override
	public String getURL() {
		return url;
	}
	@Override
	public String getSubcategoryTitle() {
		return null;
	}
	@Override
	public String getTitle() {
		return "Category";
	}
	@Override
	public String toString() {
		return String.format("%s (%d)",value==null?"":value.toString(),count);
	}
	public String toJSON(String uri,String subcategory) {
		return String.format("\n\t{\n\t\"value\":%s,\n\t\"count\":%s,\n\t\"uri\":%s,\n\t\"subcategory\":%s,\t\"subcategoryuri\":%s\n\t}",
			getValue()==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(getValue().toString())),
			JSONUtils.jsonNumber(getCount()),
			uri==null?null:JSONUtils.jsonQuote(JSONUtils.jsonEscape(uri)),
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubcategoryTitle())),	
			JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubCategoryURL(subcategory)))
			);
	}
}
