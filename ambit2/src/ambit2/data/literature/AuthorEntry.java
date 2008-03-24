/**
 * Created on 2005-3-21
 *
 */
package ambit2.data.literature;

import ambit2.data.AmbitObject;


/**
 * An author information. To be used in {@link LiteratureEntry} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AuthorEntry extends AmbitObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1014260911167263134L;
	/**
	 * empty constructor
	 */
	public AuthorEntry() {
	}
	/**
	 * AuthorEntry constructor
	 * @param name
	 */
	public AuthorEntry(String name) {
		super(name);
	}
	/**
	 * AuthorEntry constructor
	 * @param name
	 * @param id
	 */
	public AuthorEntry(String name, int id) {
		super(name,id);
	}	
}
