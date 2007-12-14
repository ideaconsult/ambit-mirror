/*
 * Created on 2005-12-18
 *
 */
package ambit.ui.actions.file;

import javax.swing.Icon;
import javax.swing.JFrame;

import ambit.data.molecule.CompoundsList;

/**
 * Exports queries results to a file.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2005-12-18
 */
public class FileExportQueryAction extends FileExportAction {

    /**
     * @param userData
     * @param mainFrame
     */
    public FileExportQueryAction(Object userData, JFrame mainFrame) {
        super(userData, mainFrame);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     */
    public FileExportQueryAction(Object userData, JFrame mainFrame, String arg0) {
        super(userData, mainFrame, arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     * @param arg1
     */
    public FileExportQueryAction(Object userData, JFrame mainFrame, String arg0,
            Icon arg1) {
        super(userData, mainFrame, arg0, arg1);
        // TODO Auto-generated constructor stub
    }
	public CompoundsList getCompoundsList() {
		/*
		if (userData instanceof DbAdminData) {
			DbAdminData dbaData = ((DbAdminData) userData);
			AmbitObject o = dbaData.getQuery();
			if (o instanceof CompoundsList) 
				return ((CompoundsList)dbaData.getQuery());
			else {
				CompoundsList c = new CompoundsList();
				c.addItem(o);
				return c;
				
			}
		} else return null;
		*/
		return null;
	}
}
