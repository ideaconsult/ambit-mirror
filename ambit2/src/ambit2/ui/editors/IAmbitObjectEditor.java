package ambit2.ui.editors;

import ambit2.data.AmbitObject;


/**
 * 
 * Provides common means for visualization of {@link ambit.data.AmbitObject}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public interface IAmbitObjectEditor extends IAmbitEditor {
	void setAO(AmbitObject ao);
	AmbitObject getAO();
}
