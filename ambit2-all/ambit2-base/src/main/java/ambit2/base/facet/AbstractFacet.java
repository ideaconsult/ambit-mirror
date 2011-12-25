package ambit2.base.facet;

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
}
