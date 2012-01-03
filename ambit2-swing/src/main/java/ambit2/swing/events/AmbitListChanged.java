/**
 * @author Nina Jeliazkova
 * Created on 2005-3-29
 * Modified 2012-1-2
 */
package ambit2.swing.events;

import ambit2.swing.interfaces.AmbitList;
import ambit2.swing.interfaces.AmbitObject;


/**
 * AmbitListChanged a descendant on {@link ambit.data.AmbitObjectChanged}
 * stores informaton of the List which had fired the event. <br>
 * Use getList() to obtain the studyList
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-4-7
 */
public class AmbitListChanged<T extends AmbitObject> extends AmbitObjectChanged<T>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2238157904990620982L;
	protected AmbitList<T> aL = null;
	/**
	 * @param source
	 */
	public AmbitListChanged(Object source, AmbitList<T> al, T object) {
		super(source,object);
		this.aL = al;
		this.ao = object;
	}
	
	public AmbitList<T> getList() {
		return aL;
	}
}
