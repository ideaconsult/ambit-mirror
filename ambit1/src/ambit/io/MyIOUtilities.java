/**
 * Created on 2005-1-25
 *
 */
package ambit.io;

import java.awt.Component;
import java.awt.Frame;
import java.beans.PropertyChangeListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.xml.sax.XMLReader;

import ambit.domain.AllData;
import ambit.log.AmbitLogger;

/**
 * @deprecated 
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
	/**
	 * Invokes OpenFile dialog and load the content of the selected file (if any) into the data<br>
	 * @param frame {@link java.awt.Frame} a frame to be used  as a parent for the OpenFile dialog
	 * @param ext file extension
	 * @param data {@link IReadWriteStream} the data to be saved
	 * @param match TODO to be removed
	 * @return true if succesful
	 * @deprecated use {@link org.openscience.cdk.io.IChemObjectReader}
	 */
	public static boolean loadFile(Frame frame,String defaultDir,  String[] ext, String[] extDescription,
			IReadWriteStream data , AllData match) {
		File file = selectFile(frame, null, defaultDir, ext,extDescription, true);
		boolean result = false;
		if (file != null)
		try {
			FileInputStream in = new FileInputStream(file);
			data.setType(AmbitFileFilter.getSuffix(file));
			
			result = data.load(in);
			data.setStreamName(file.getName());
			in.close();
		} catch (Exception e ) {
			//TODO FileNotFoundException, IOException handling
            e.printStackTrace(); 
            result = false;
		}
		return result;
	}
	
	/**
	 * Invokes SaveFile dialog and saves the content of the data to the selected file (if any)<br> 
	 * @param frame {@link java.awt.Frame} a frame to be used  as a parent for the OpenFile dialog
	 * @param ext file extension
	 * @param data the data to be saved
	 * @return true if successful
	 * @deprecated use {@link org.openscience.cdk.io.IChemObjectWriter} and {@link ambit.ui.actions.file.FileExportAction}
	 */
	public static boolean saveFile(Frame frame, String defaultDir, String[] ext, String[] extDescription, IReadWriteStream data ) {
		File file = selectFile(frame, null, defaultDir,ext,extDescription,false);
		boolean result = false;
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
			result = data.save(out);
			out.close();
		} catch (Exception e ) {
			//TODO FileNotFoundException, IOException handling
            e.printStackTrace(); 
            result = false;
		}
		return result;
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
            FileFilter ff = fc.getFileFilter();
            if (ff instanceof AmbitFileFilter)
            	file = ((AmbitFileFilter)ff).changeExtension(file);
            
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
    public static XMLReader initParser() {
        AmbitLogger logger = new AmbitLogger(MyIOUtilities.class);
        XMLReader parser = null;

        boolean success = false;
        // If JAXP is prefered (comes with Sun JVM 1.4.0 and higher)
        if (!success) {
            try {
                javax.xml.parsers.SAXParserFactory spf = javax.xml.parsers.SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);
                javax.xml.parsers.SAXParser saxParser = spf.newSAXParser();
                parser = saxParser.getXMLReader();
                logger.info("Using JAXP/SAX XML parser.");
                success = true;
            } catch (Exception e) {
                logger.warn("Could not instantiate JAXP/SAX XML reader: "+
                		e.getMessage());
                logger.debug(e);
            }
        }
        // Aelfred is first alternative.
        if (!success) {
            try {
                parser = (XMLReader)MyIOUtilities.class.getClassLoader().
                        loadClass("gnu.xml.aelfred2.XmlReader").
                        newInstance();
                logger.info("Using Aelfred2 XML parser.");
                success = true;
            } catch (Exception e) {
                logger.warn("Could not instantiate Aelfred2 XML reader!");
                logger.debug(e);
            }
        }
        // Xerces is second alternative
        if (!success) {
            try {
                parser = (XMLReader)MyIOUtilities.class.getClassLoader().
                        loadClass("org.apache.xerces.parsers.SAXParser").
                        newInstance();
                logger.info("Using Xerces XML parser.");
                success = true;
            } catch (Exception e) {
                logger.warn("Could not instantiate Xerces XML reader!");
                logger.debug(e);
            }
        }
        if (!success) {
            logger.error("Could not instantiate any XML parser!");
        }
        return parser;
    }	

}


