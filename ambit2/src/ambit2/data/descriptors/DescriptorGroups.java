/**
 * Created on 2005-3-21
 *
 */
package ambit2.data.descriptors;

import ambit2.data.AmbitList;

/**
 * A List of {@link DescriptorGroup}s 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DescriptorGroups extends AmbitList<DescriptorGroup> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1589959018153158474L;

	/**
	 * 
	 */
	public DescriptorGroups() {
		super();

	}

	public DescriptorGroup createNewItem() {
		return new DescriptorGroup();
	}	

}
