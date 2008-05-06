/**
 * Created on 2005-1-18
 *
 */
package ambit2.ui.domain;

import java.awt.Color;

import ambit2.data.AmbitObjectChanged;
import ambit2.data.IAmbitObjectListener;
import ambit2.ui.CorePanel;

/**
 * a GUI panel for {@link ambit2.applications.discovery.AmbitDiscoveryApp} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public abstract class ADCorePanel extends CorePanel implements IAmbitObjectListener {
	
	public ADCorePanel(String title) {
		super(title);
	}
	
	public ADCorePanel(String title,  Color  bClr, Color fClr) {
		super(title,bClr,fClr);
	}
	
	/**
	 * 
	 */
	/* (non-Javadoc)
	 * @see ambit2.data.IAmbitObjectListener#ambitObjectChanged(ambit2.data.AmbitObjectChanged)
	 */
	public void ambitObjectChanged(AmbitObjectChanged event) {
		// TODO ambitObjectChanged
		//System.err.println("TODO ambitObjectChanged");
	}
	
}