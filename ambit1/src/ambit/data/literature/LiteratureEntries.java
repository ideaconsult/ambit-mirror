/**
 * Created on 2005-3-21
 *
 */
package ambit.data.literature;

import java.util.Collection;

import ambit.data.AmbitList;
import ambit.data.AmbitObject;


/**
 * A List of {@link ambit.data.literature.LiteratureEntry}s 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class LiteratureEntries extends AmbitList {

	/**
	 * 
	 */
	public LiteratureEntries() {
		super();

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
