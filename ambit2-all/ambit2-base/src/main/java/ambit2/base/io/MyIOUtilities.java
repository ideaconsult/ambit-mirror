/**
 * Created on 2005-1-25
 *
 */
package ambit2.base.io;

import java.awt.Component;
import java.awt.Frame;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


/**
 * This class is to simplify OpenFile / SaveFile dialogs calls<br> 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class MyIOUtilities {

	/**
	 * 
	 */
	public MyIOUtilities() {
		super();
	}
	public static File openFile(Frame frame,String defaultDir, String[] ext,String[] extDescription) {
		return selectFile(frame, null, defaultDir,ext,extDescription, true);	
	}


	public static File selectFile(Component frame, String caption,
	        String currentDirectory,
	        String[] ext,String[] extDescription, boolean open) {
		return selectFile(frame, caption, currentDirectory, ext, extDescription, open,null);
	}	
	/**
	 * Invokes OpenFile or SaveFile dialog
	 * @param frame {@link java.awt.Frame} a frame to be used  as a parent for the file dialog
	 * @param ext file extension
	 * @param open if true invokes OpenFile dialog, otherwise invokes SaveFile
	 * @return the selected {@link java.io.File}
	 */

	public static File selectFile(Component frame, String caption,
	        String currentDirectory,
	        String[] ext,String[] extDescription, boolean open, JComponent accessoryPanel) {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		for (int i=0; i < ext.length; i++)
			fc.addChoosableFileFilter(new AmbitFileFilter(ext[i],extDescription[i]));
		fc.setFileFilter(fc.getChoosableFileFilters()[1]);
		fc.setCurrentDirectory(new File(currentDirectory));
		
		if (accessoryPanel != null) {
			fc.setAccessory(accessoryPanel);
			if (accessoryPanel instanceof PropertyChangeListener)
				fc.addPropertyChangeListener((PropertyChangeListener)accessoryPanel);
		}
        int returnVal;
        if (open) returnVal = fc.showOpenDialog(frame);
		else  returnVal =  fc.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (!file.isDirectory()) {
	            FileFilter ff = fc.getFileFilter();
	            if (ff instanceof AmbitFileFilter)
	            	file = ((AmbitFileFilter)ff).changeExtension(file);
            }
            fc = null;
            
            return file;
        } 
        fc = null;
        return null;
	}
	public static File[] selectFiles(Component frame, String caption,
	        String currentDirectory,
	        String[] ext,String[] extDescription, boolean open, JComponent accessoryPanel) {
		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		
		for (int i=0; i < ext.length; i++)
			fc.addChoosableFileFilter(new AmbitFileFilter(ext[i],extDescription[i]));
		fc.setFileFilter(fc.getChoosableFileFilters()[1]);
		fc.setCurrentDirectory(new File(currentDirectory));
		
		if (accessoryPanel != null) {
			fc.setAccessory(accessoryPanel);
			if (accessoryPanel instanceof PropertyChangeListener)
				fc.addPropertyChangeListener((PropertyChangeListener)accessoryPanel);
		}
        int returnVal;
        if (open) returnVal = fc.showOpenDialog(frame);
		else  returnVal =  fc.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] file = fc.getSelectedFiles();
            FileFilter ff = fc.getFileFilter();
            if (ff instanceof AmbitFileFilter)
            	for (int i=0; i < file.length;i++)
            		file[i] = ((AmbitFileFilter)ff).changeExtension(file[i]);
            
            fc = null;
            
            return file;
        } 
        fc = null;
        return null;
	}


}


