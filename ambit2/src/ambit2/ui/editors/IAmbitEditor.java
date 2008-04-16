package ambit2.ui.editors;

import java.awt.Component;

import javax.swing.JComponent;

import ambit2.exceptions.AmbitException;

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
	//void setEditable(boolean editable);
	//boolean isEditable();

}
