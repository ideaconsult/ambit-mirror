/**
 * Created on 2005-3-21
 *
 */
package ambit2.data.literature;

import ambit2.ui.data.AuthorEntryTableModel;
import ambit2.ui.editors.AbstractAmbitEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.data.AmbitObject;

/**
 * An author information. To be used in {@link LiteratureEntry} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AuthorEntry extends AmbitObject {
	/**
	 * empty constructor
	 */
	public AuthorEntry() {
		super();
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
	/*
	public IAmbitEditor editor() {
		return new AuthorEntryEditor(this);
	}
	*/
	public IAmbitEditor editor(boolean editable) {
		return new AbstractAmbitEditor("Author",this) {
			protected ambit2.ui.data.AbstractPropertyTableModel createTableModel(AmbitObject object) {
				 return new AuthorEntryTableModel((AuthorEntry)object);
			}
		};
	}
	
}
