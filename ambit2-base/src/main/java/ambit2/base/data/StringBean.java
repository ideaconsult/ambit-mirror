package ambit2.base.data;


public class StringBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1591592139930462874L;
	protected String name=null;
	public StringBean(String value) {
		setName(value);
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return getName();
	}
}
