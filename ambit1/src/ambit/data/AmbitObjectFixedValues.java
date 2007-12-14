/**
 * Created on 2005-4-4
 *
 */
package ambit.data;

/**
 * An {@link AmbitObject} which has only a fixed studyList of string values allowed
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitObjectFixedValues extends AmbitObject {

	/**
	 * 
	 */
	public AmbitObjectFixedValues() {
		super();
		
	}

	/**
	 * @param name
	 */
	public AmbitObjectFixedValues(String name) {
		super(name);
		setName(name);
	}

	/**
	 * @param name
	 * @param id
	 */
	/**
	 * 
	 * @param id
	 */
	public AmbitObjectFixedValues(int id) {
		super("");
		setId(id);
	}	
	public AmbitObjectFixedValues(String name, int id) {
		super(name, id);
	}
	public boolean isPredefined() {
		return true;
	}
	//to be overrided
	public String[] predefinedvalues() {
		return new String[1];
	}		
	public void setName(String name) {
		String[] values = predefinedvalues();
		for (int i = 0; i < values.length; i++)
			if (name.equals(values[i])) {
				setId(i);
			}
	}
	public void setId(int id) {
		super.setId(id);
		String[] values = predefinedvalues();
		if ((id < 0) || (id > values.length)) id = 0;
	
		name = values[id];
	}
	
}
