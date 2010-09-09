package ambit2.core.processors.structure.key;

import ambit2.base.data.Property;

public class SampleKey extends PropertyNameKey {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6702546532075828636L;

	public SampleKey() {
		super("SAMPLE");
	}	
	@Override
	public boolean isKeyValid(Property newkey) {

		return key.getName().equals(newkey.getName());
	}
	@Override
	protected boolean isValid(Object newkey, Object value) {
		return (value != null) && !".".equals(value);
	}
	
}
