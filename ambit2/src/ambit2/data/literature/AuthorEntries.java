/**
 * Created on 2005-3-21
 *
 */
package ambit2.data.literature;

import java.util.Collection;

import ambit2.data.AmbitList;
import ambit2.data.AmbitObject;

/**
 * A list of {@link ambit2.data.literature.AuthorEntry}s 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AuthorEntries extends AmbitList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3491694457677477971L;

	/**
	 * 
	 */
	public AuthorEntries() {
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
			if (i>0) { buf.append(','); }
			buf.append(getItem(i).toString());
		}	
		return buf.toString();
	}
 	 public AmbitObject createNewItem() {
		return new AuthorEntry();
 	 }	
}
