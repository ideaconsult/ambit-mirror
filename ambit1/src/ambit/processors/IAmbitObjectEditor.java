package ambit.processors;

import ambit.data.AmbitObject;
import ambit.data.IAmbitEditor;

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
