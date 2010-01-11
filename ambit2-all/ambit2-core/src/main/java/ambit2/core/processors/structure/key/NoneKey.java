package ambit2.core.processors.structure.key;

import ambit2.base.data.Property;

public class NoneKey extends PropertyKey<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5914214406075177640L;

	@Override
	public Class getType() {
		return String.class;
	}
	@Override
	protected boolean isValid(Object key, Object value) {
		return false;
	}
	@Override
	public boolean isKeyValid(Property key) {
		return false;
	}
}
