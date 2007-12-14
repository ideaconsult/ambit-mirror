/**
 * Created on 2005-3-21
 *
 */
package ambit.data.descriptors;

import ambit.data.AmbitObject;

/**
 * a descriptor group (name and group identifier)
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DescriptorGroup extends AmbitObject {
	protected static String[] groupNames =
		{"Topological","Electronic","Indicatory","2D","3D"};
	/**
	 * an empty constructor
	 */
	public DescriptorGroup() {
		super();
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
	public void clear() {
		super.clear();
	}
	public boolean isPredefined() {
		return false;
	}
	public String[] predefinedvalues() {
		return groupNames;
	}
		
}