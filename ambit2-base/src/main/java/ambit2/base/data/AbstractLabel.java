package ambit2.base.data;



public abstract class AbstractLabel<LABEL  extends Enum>  extends AmbitBean implements Comparable<AbstractLabel<LABEL>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1373737719026207986L;

	protected LABEL label;
	protected AmbitUser user=null;
	protected String text=null;
	public AbstractLabel() {
		
	}
	public AbstractLabel(LABEL label) {
		setLabel(label);
	}	
	public AmbitUser getUser() {
		return user;
	}
	public void setUser(AmbitUser user) {
		this.user = user;
	}
	public LABEL getLabel() {
		return label;
	}
	public void setLabel(LABEL label) {
		this.label = label;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return getLabel().toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return toString().equals(obj.toString());
	}
	
	@Override
	public int compareTo(AbstractLabel<LABEL> o) {
		return label.compareTo(o.label);
	}
	
}
