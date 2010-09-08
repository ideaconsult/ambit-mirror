package ambit2.core.processors.structure.key;

import ambit2.base.data.Property;

public class NameOnlyProperty extends Property {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4971559595379407362L;

	public NameOnlyProperty(String name) {
		super(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Property) {
			return
			((Property)obj).getName().equals(getName()) ; 
		} else return false;
	}
}
