/**
 * Created on 2005-3-21
 *
 */
package ambit2.data.literature;

import java.util.Collection;

import ambit2.data.AmbitList;
import ambit2.data.AmbitObject;


/**
 * A List of {@link ambit2.data.literature.LiteratureEntry}s 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class LiteratureEntries extends AmbitList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7497360726970754932L;
	/**
	 * 
	 */
	public LiteratureEntries() {

	}

	/**
	 * @param initialCapacity
	 */
	public LiteratureEntries(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * @param c
	 */
	public LiteratureEntries(Collection c) {
		super(c);
	}
	/* (non-Javadoc)
	 * @see ambit.data.AmbitList#createNewItem()
	 */
	public AmbitObject createNewItem() {
		return ReferenceFactory.createEmptyReference();
	}	
}
