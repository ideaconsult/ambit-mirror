package ambit2.base.interfaces;

import javax.swing.JComponent;

/**
 * 
 * A class implementing this interface provides common means of visualisation.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Apr 16,2008
 */
public interface IAmbitEditor<T>  {
	
	void setObject(T object);
	T getObject();
	//boolean view(Component parent,boolean editable, String title) throws AmbitException;
	JComponent getJComponent();
	void setEditable(boolean editable);
	boolean isEditable();
	boolean confirm();

}
