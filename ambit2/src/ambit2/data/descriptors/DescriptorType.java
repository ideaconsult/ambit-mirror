/**
 * Created on 2005-3-24
 *
 */
package ambit2.data.descriptors;

import ambit2.data.AmbitObject;

/**
 * Descriptor values types : continuous, discrete, string  
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DescriptorType extends AmbitObject {
	public final static int _typeReal = 0;
	public final static int _typeInt = 1;
	public final static int _typeStr = 2;
	public final static String[] _typename = {"Numeric (continuous)","Numeric (discrete)","String"};

	protected String defaultValue = ""; 
	/**
	 * 
	 */
	public DescriptorType() {
		super();
		setId(0);
	}
	public DescriptorType(int id) {
		super();
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
		if ((id < 0) || (id > 2)) id = _typeReal;
		name = _typename[id];
		switch(id) {
		case(_typeReal) : defaultValue = "0";
		case(_typeInt) :  defaultValue = "0";
		case(_typeStr) :  defaultValue = "";
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
