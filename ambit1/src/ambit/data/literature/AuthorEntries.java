/**
 * Created on 2005-3-21
 *
 */
package ambit.data.literature;

import java.awt.Dimension;
import java.util.Collection;

import ambit.data.AmbitList;
import ambit.data.AmbitObject;
import ambit.data.IAmbitEditor;
import ambit.ui.data.AmbitListOneItemEditor;

/**
 * A list of {@link ambit.data.literature.AuthorEntry}s 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AuthorEntries extends AmbitList {

	/**
	 * 
	 */
	public AuthorEntries() {
		super();
	}

	/**
	 * @param initialCapacity
	 */
	public AuthorEntries(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * @param c
	 */
	public AuthorEntries(Collection c) {
		super(c);

	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < size(); i++) {
			if (i>0) buf.append(',');
			buf.append(getItem(i).toString());
		}	
		return buf.toString();
	}
 	 public AmbitObject createNewItem() {
		return new AuthorEntry();
 	 }	

     @Override
     public IAmbitEditor editor(boolean editable) {
     	AmbitListOneItemEditor e = new AmbitListOneItemEditor(this,editable,"Authors") {
     		
     		protected java.awt.Dimension getItemEditorDimension() {
     			return new Dimension(200,32);
     		};
     		
     	};
         e.setNoDataText("Click on list to see item details");
         e.setEditable(editable);
         return e;
     }	
 	
}
