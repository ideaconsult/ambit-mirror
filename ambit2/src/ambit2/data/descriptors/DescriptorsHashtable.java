package ambit2.data.descriptors;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;


/**
 * A hashtable of ( Object, {@link ambit2.data.descriptors.DescriptorDefinition} ) pairs.
 * Used to provide correspondence between descriptor string name and {@link ambit2.data.descriptors.DescriptorDefinition} or
 * between {@link org.openscience.cdk.qsar.IDescriptor} and {@link ambit2.data.descriptors.DescriptorDefinition}.
 * See exapmle at {@link ambit2.database.writers.DbSubstanceWriter}.   
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DescriptorsHashtable extends Hashtable {

	public DescriptorsHashtable() {
		super();
	}

	public DescriptorsHashtable(int initialCapacity, float loadFactor) {
		super(initialCapacity,loadFactor);
	}

	public DescriptorsHashtable(int initialCapacity) {
		super(initialCapacity);
	}
	
	public DescriptorsHashtable(Map t) {
		super(t);
	}
	
	public void addDescriptorPair(Object descriptor, DescriptorDefinition ambitDescriptor) {
		if (descriptor != null)
			put(descriptor,ambitDescriptor);		
	}
	
	public DescriptorDefinition getAmbitDescriptor(Object key) {
		Object ambitDescriptor = get(key);
		if (ambitDescriptor instanceof DescriptorDefinition)
			return (DescriptorDefinition)ambitDescriptor;
		else return null;
	}
	public DescriptorsList getDescriptors() {
		DescriptorsList list = new DescriptorsList();
		Enumeration e = elements();
		while (e.hasMoreElements()) {
			list.addItem((DescriptorDefinition)e.nextElement());
		}
		return list;
	}
}
