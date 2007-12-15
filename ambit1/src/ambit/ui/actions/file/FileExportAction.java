package ambit.ui.actions.file;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit.data.IDataContainers;
import ambit.data.ISharedData;
import ambit.io.batch.IBatchStatistics;
import ambit.ui.UITools;
import ambit.ui.actions.BatchAction;

/**
 * Exports query results to a file. See example at {@link ambit.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class FileExportAction extends BatchAction {

	public FileExportAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Save");
	}

	public FileExportAction(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/save_16.png"));
		
	}

	public FileExportAction(Object userData, JFrame mainFrame, String name,
			Icon icon) {
		super(userData, mainFrame, name, icon);
		interactive = false;
		putValue(AbstractAction.SHORT_DESCRIPTION, "Saves molecules from \"Molecule Browser\" to a file.");
	}
	public void completed() {
		if (userData instanceof ISharedData)  
			((IDataContainers) userData).getMolecules().setEnabled(true);
		super.completed();
	}
	public IIteratingChemObjectReader getReader() {
    	if (userData instanceof ISharedData) { 
    		((IDataContainers) userData).getMolecules().setEnabled(false);
			return ((IDataContainers) userData).getMolecules().getReader();
    	} else	return null;
	}

	public IChemObjectWriter getWriter() {
		IChemObjectWriter writer = getFileWriter("");
		writer.addChemObjectIOListener(new IChemObjectIOListener() {
			public void processIOSettingQuestion(IOSetting arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		return writer;
	}
	   public IBatchStatistics getBatchStatistics() {
	    	
	    	IBatchStatistics bs = super.getBatchStatistics();
	    	bs.setResultCaption("Saved ");
	    	return bs;
	    }

}
