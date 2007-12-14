/**
 * Created on 2005-3-28
 *
 */
package ambit.data;

import java.util.EventObject;

/**
 * An {@link java.util.EventObject} for {@link AmbitObject} 
 * Used by {@link IAmbitObjectListener} and {@link IAmbitListListener} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitObjectChanged extends EventObject {
	protected AmbitObject ao = null;

	/**
	 * 
	 * @param source
	 */
	public AmbitObjectChanged(Object source) {
		super(source);
		if (source instanceof AmbitObject)
			this.ao = (AmbitObject) ao;
		else this.ao = null;
	}	
	/**
	 * 
	 * @param source
	 * @param ao
	 */
	public AmbitObjectChanged(Object source, AmbitObject ao) {
		super(source);
		this.ao = (AmbitObject) ao;
	}
	/**
	 * 
	 * @return {@link AmbitObject}
	 */
	public AmbitObject getObject() {
		return ao;
	}
}
