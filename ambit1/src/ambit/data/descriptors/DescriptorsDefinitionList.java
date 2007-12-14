/**
 * Created on 2005-1-26
 *
 */
package ambit.data.descriptors;

import java.util.List;

import ambit.data.AmbitList;
import ambit.data.literature.LiteratureEntry;
import ambit.data.literature.ReferenceFactory;


/**
 * A studyList of {@link DescriptorDefinition}s
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DescriptorsDefinitionList extends AmbitList {
	
	/**
	 * 
	 */
	protected DescriptorsDefinitionList() {
		super();
	}
	protected DescriptorsDefinitionList(List names) {
		super();
		DescriptorDefinition d;
		LiteratureEntry ref = ReferenceFactory.createEmptyReference();
		for (int i = 0; i < names.size(); i++) { 
			d = new DescriptorDefinition((String) names.get(i),ref);
			addItem(d);
		}
	}
	/**
	 * 
	 * @param i item index
	 * @return DescriptorDefinition
	 */
	public DescriptorDefinition getDescriptorDef(int i) {
		return (DescriptorDefinition) getItem(i); 
	}
	/**
	 * 
	 * @param i item index
	 * @return descriptor name
	 */
	public String getDescriptorName(int i) {
		return getItem(i).getName(); 
	}
	/**
	 * 
	 * @param i  item index
	 * @param vtype value type as defined in DescriptorDefinition
	 */
	public void setDescriptorVType(int i,int vtype) {
		((DescriptorDefinition) getItem(i)).getValuetype().setId(vtype); 
	}		
	/**
	 * 
	 * @return int[] of value types as defined in DescriptorDefinition 
	 */
	public int[] getDescriptorVTypes() {
		int[] s = new int[size()];
		for (int i = 0; i < size(); i ++) {
			s[i] =  ((DescriptorDefinition) getItem(i)).getValuetype().getId();  
		}	
		return s;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < size(); i++) {
			if (i>0) buf.append(',');
			buf.append(getItem(i).toString());
		}	
		return buf.toString();
	}

	
}

