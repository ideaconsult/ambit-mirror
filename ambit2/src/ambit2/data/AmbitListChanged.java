/**
 * @author Nina Jeliazkova
 * Created on 2005-3-29
 *
 */
package ambit2.data;

/**
 * AmbitListChanged a descendant on {@link ambit2.data.AmbitObjectChanged}
 * stores informaton of the studyList which had fired the event. <br>
 * Use getList() to obtain the studyList
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-4-7
 */
public class AmbitListChanged extends AmbitObjectChanged {
	protected AmbitList aL = null;
	/**
	 * @param source
	 */
	public AmbitListChanged(Object source, AmbitList al, AmbitObject object) {
		super(source,object);
		this.aL = al;
		this.ao = object;
	}
	
	public AmbitList getList() {
		return aL;
	}
}
