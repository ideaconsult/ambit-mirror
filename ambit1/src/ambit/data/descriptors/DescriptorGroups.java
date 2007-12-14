/**
 * Created on 2005-3-21
 *
 */
package ambit.data.descriptors;

import java.util.Collection;

import ambit.data.AmbitList;
import ambit.data.AmbitObject;

/**
 * A List of {@link DescriptorGroup}s 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DescriptorGroups extends AmbitList {

	/**
	 * 
	 */
	public DescriptorGroups() {
		super();

	}

	/**
	 * @param initialCapacity
	 */
	public DescriptorGroups(int initialCapacity) {
		super(initialCapacity);

	}

	/**
	 * @param c
	 */
	public DescriptorGroups(Collection c) {
		super(c);

	}
	public AmbitObject createNewItem() {
		return new DescriptorGroup();
	}	

}
