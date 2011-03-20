package ambit2.base.facet;

public abstract class AbstractFacet<T> implements IFacet<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1486718382701377463L;
	protected int count;

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
	public String getResultsURL() {
		return null;
	}
	protected T value;
}
