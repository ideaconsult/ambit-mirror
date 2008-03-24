/**
 * Created on 2005-3-21
 *
 */
package ambit2.data.descriptors;

import ambit2.data.AmbitObject;



/**
 * a descriptor group (name and group identifier)
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DescriptorGroup extends AmbitObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7960532733788764761L;
	protected static String[] groupNames =
		{"Topological","Electronic","Indicatory","2D","3D"};
	/**
	 * an empty constructor
	 */
	public DescriptorGroup() {
	}	
	/**
	 * DescriptorGroup constructor
	 * @param name
	 */
	public DescriptorGroup(String name) {
		super(name);
	}
	/**
	 * DescriptorGroup constructor
	 * @param name
	 * @param idgroup
	 */
	public DescriptorGroup(String name, int idgroup) {
		super(name,idgroup);
	}

	public boolean isPredefined() {
		return false;
	}
	public String[] predefinedvalues() {
		return groupNames;
	}
		
}