/**
 * Created on 2005-3-23
 *
 */
package ambit.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Format;

import javax.swing.JFormattedTextField;

import ambit.data.AmbitObject;


/**
 * Used in {@link ambit.ui.data.AmbitObjectPanel} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitTextField extends JFormattedTextField {
	protected Method getMethod = null;
	protected Method setMethod = null;
	protected Class[] param = null;
	protected AmbitObject object = null;
	/**
	 * 
	 */
	public AmbitTextField() {
		super();
	}

	/**
	 * @param value
	 */
	public AmbitTextField(Object value) {
		super(value);
		
	}

	/**
	 * @param format
	 */
	public AmbitTextField(Format format) {
		super(format);
		
	}

	/**
	 * @param formatter
	 */
	public AmbitTextField(AbstractFormatter formatter) {
		super(formatter);
		
	}

	/**
	 * @param factory
	 */
	public AmbitTextField(AbstractFormatterFactory factory) {
		super(factory);
		
	}

	/**
	 * @param factory
	 * @param currentValue
	 */
	public AmbitTextField(AbstractFormatterFactory factory, Object currentValue) {
		super(factory, currentValue);
		
	}
	public void setMethods(AmbitObject object,
			Method getMethod, Method setMethod, Class[] param) {
		this.object = object;
		this.getMethod = getMethod;
		this.setMethod = setMethod;
		this.param = param;
	}
	public void updateValue(Object value) {
		try {
			if (param[0].getName().equals("int"))
				setMethod.invoke(object,new Object[] {new Integer(value.toString())});
			else
			if (param[0].getName().equals("double"))
				setMethod.invoke(object,new Object[] {new Double(value.toString())});			
			else
				setMethod.invoke(object,new Object[] {value});
//			String s = getMethod.invoke(object,null).toString();
		} catch (IllegalAccessException x) { x.printStackTrace(); 
		} catch (InvocationTargetException x) { x.printStackTrace();  
		} catch (NumberFormatException x ) {
			
		}
		
	}
	public Object getAmbitValue() {
		try {
			return getMethod.invoke(object,null);
		} catch (IllegalAccessException x) { x.printStackTrace(); 
			return null;
		} catch (InvocationTargetException x) { x.printStackTrace();
			return null;
		}		
	}
	public AmbitObject getObject() {
		return object;
	}
}
