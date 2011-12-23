package ambit2.core.processors.structure.key;

import ambit2.base.data.Property;

public class NameKey extends PropertyKey<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4988862647891373896L;
	public NameKey() {
		super(Property.getNameInstance());
	}
	@Override
	public Object getKey() {
		return null;
	}
	
	@Override
	public boolean isKeyValid(Property newkey) {
		return key.getName().equals(newkey.getName());
	}
	@Override
	protected boolean isValid(Object newkey, Object value) {
		return (value != null) && !".".equals(value) && !"".equals(value.toString().trim());
	}	
	@Override
	public Object getQueryKey() {
		return null;
	}
	@Override
	public Class getType() {
		return String.class;
	}
}
