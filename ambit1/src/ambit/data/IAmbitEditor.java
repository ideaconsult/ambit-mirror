package ambit.data;

import java.awt.Component;

import javax.swing.JComponent;

import ambit.exceptions.AmbitException;

/**
 * 
 * A class implementing this interface provides common means of visualisation.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public interface IAmbitEditor  {
	boolean view(Component parent,boolean editable, String title) throws AmbitException;
	JComponent getJComponent();
	void setEditable(boolean editable);
	boolean isEditable();

}
