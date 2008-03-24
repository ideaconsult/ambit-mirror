/**
 * Created on 2005-3-24
 *
 */
package ambit2.data.descriptors;

import ambit2.data.AmbitObject;

/**
 * TODO Refactor to use definition from IMolecularDescriptor
 * Descriptor values types : continuous, discrete, string  
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DescriptorType extends AmbitObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8129776106317550646L;
	/**
	 * Real valued
	 */
	public static final int _typeReal = 0;
	/**
	 * Integer
	 */
	public static final int _typeInt = 1;
	/**
	 * String
	 */
	public static final int _typeStr = 2;
	/**
	 * Strings
	 */
	public static final String[] _typename = {"Numeric (continuous)","Numeric (discrete)","String"};

	protected String defaultValue = ""; 
	/**
	 * 
	 */
	public DescriptorType() {

		setId(0);
	}
	public DescriptorType(int id) {
		setId(id);
	}

	/**
	 * @param name
	 */
	public DescriptorType(String name) {
		super(name);
		setId(0);
	}

	/**
	 * @param name
	 * @param id
	 */
	public DescriptorType(String name, int id) {
		super(name, id);
		setId(id);
	}
	public void setId(int id) {
		super.setId(id);
		if ((id < 0) || (id > 2)) {
			id = _typeReal;
		}
		name = _typename[id];
		switch(id) {
		case(_typeReal) : { defaultValue = "0"; break; }
		case(_typeInt) :  { defaultValue = "0"; break;}
		case(_typeStr) :  { defaultValue = ""; break;}
		default: {defaultValue = ""; }
		}
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		for (int i = 0; i < _typename.length; i++)
			if (name.equals(_typename[i])) {
				setId(i);
			}
	}
	public static String valueToString(int id) {
		return _typename[id];
	}
	public String readDefaultValue() {
		return defaultValue;
	}
	public void writeDefaultValue(String defaultValue) {
		boolean m = (defaultValue != this.defaultValue);
		this.defaultValue = defaultValue;
		setModified(m);
	}
	public boolean isPredefined() {
		return true;
	}
	public String[] predefinedvalues() {
		return _typename;
	}	
}
