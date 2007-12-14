/**
 * Created on 2005-1-26
 *
 */
package ambit.data.descriptors;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import ambit.data.AmbitObject;
import ambit.data.IAmbitEditor;
import ambit.data.literature.LiteratureEntry;
import ambit.data.literature.ReferenceFactory;
import ambit.ui.data.descriptors.DescriptorsPanel;

/**
 * A List of {@link Descriptor}s
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DescriptorsList extends DescriptorsDefinitionList {
	public DescriptorsList() {
		super();
	}	
	public DescriptorsList(List names) {
		super();
		Descriptor d;
		LiteratureEntry ref = ReferenceFactory.createEmptyReference();
		for (int i = 0; i < names.size(); i++) { 
			d = new Descriptor(names.get(i).toString(),ref);
			int id = d.getTypeInModel().guessType(names.get(i).toString());
			if (id >= 0) d.getTypeInModel().setId(id);
			
			addItem(d);
		}
	}
	public DescriptorsList(Hashtable properties) {
		super();
		Descriptor d;
		LiteratureEntry ref = ReferenceFactory.createEmptyReference();
		Enumeration e = properties.keys();
		while (e.hasMoreElements()) {
		    Object key = e.nextElement().toString();
			d = new Descriptor(key.toString(),ref);
			
			int id = d.getTypeInModel().guessType(key.toString(),properties.get(key));
			if (id >= 0) d.getTypeInModel().setId(id);
			addItem(d);
		}
	}	
	/**
	 * 
	 * @param i item index
	 * @return DescriptorDefinition
	 */
	public Descriptor getDescriptor(int i) {
		return (Descriptor) getItem(i); 
	}
	/**
	 * 
	 * @param i item index
	 * @return descriptor type as defined in IColumnTypeSelection
	 */
	public int getDescriptorDType(int i) {
		return ((Descriptor) getItem(i)).getTypeInModel().getId(); 
	}
	/**
	 * 
	 * @param i  item index
	 * @param dtype descriptor type as defined in IColumnTypeSelection
	 */
	public void setDescriptorDType(int i,int dtype) {
		((Descriptor) getItem(i)).getTypeInModel().setId(dtype); 
	}
	
	public int matchTo(DescriptorsList list) {
		boolean[] used = new boolean[list.size()];
		for (int j = 0; j < list.size(); j++) used[j] = false; 
		int r = 0;	
		
		for (int i = 0; i < size(); i++) {
			Descriptor d = getDescriptor(i);
			for (int j = 0; j < list.size(); j++) {
				Descriptor g = list.getDescriptor(j);
				if ((g.getName()).equals(d.getName())) {
					d.setOrderInModel(g.getOrderInModel());
					used[j] = true;
					r++;
					break;
				}
			}
		}
		if (size() < list.size())   
			for (int j = 0; j < list.size(); j++) if (!used[j]) {
				Descriptor g = list.getDescriptor(j);
				try {
					addItem((Descriptor) g.clone());
					r++;
				} catch (CloneNotSupportedException x) {
					x.printStackTrace();
				}
				
			}
		return r;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < size(); i++) {
			if (i>0) buf.append(',');
			buf.append(getItem(i).toString());
		}	
		return buf.toString();
	}

	/**
	 * 
	 * @return int[] of descriptor types as defined in IColumnTypeSelection
	 */
	public int[] getDescriptorDTypes() {
		int[] s = new int[size()];
		for (int i = 0; i < size(); i ++) {
			s[i] =  ((Descriptor) getItem(i)).getTypeInModel().getId();  
		}	
		return s;
	}
	public AmbitObject createNewItem() {
		return DescriptorFactory.createEmptyDescriptor();
	}	
	/* (non-Javadoc)
     * @see ambit.data.AmbitObject#editor()
     */
    public IAmbitEditor editor() {
        return new DescriptorsPanel(this);
    }
	
}
