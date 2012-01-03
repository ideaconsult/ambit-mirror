/**
 * Created on 2005-3-28
 * Modified 2012-2-2
 */
package ambit2.swing.events;

import java.util.EventObject;

import ambit2.swing.interfaces.AmbitObject;
import ambit2.swing.interfaces.IAmbitListListener;
import ambit2.swing.interfaces.IAmbitObjectListener;

/**
 * An {@link java.util.EventObject} for {@link AmbitObject} 
 * Used by {@link IAmbitObjectListener} and {@link IAmbitListListener} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitObjectChanged<T extends AmbitObject> extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -823554415691656711L;
	protected T ao = null;

	/**
	 * 
	 * @param source
	 */
	public AmbitObjectChanged(Object source) {
		super(source);
		if (source instanceof AmbitObject)
			this.ao = (T) ao;
		else this.ao = null;
	}	
	/**
	 * 
	 * @param source
	 * @param ao
	 */
	public AmbitObjectChanged(Object source, T ao) {
		super(source);
		this.ao = (T) ao;
	}
	/**
	 * 
	 * @return {@link AmbitObject}
	 */
	public T getObject() {
		return ao;
	}
}
