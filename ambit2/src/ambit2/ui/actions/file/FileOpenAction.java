package ambit2.ui.actions.file;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit2.ui.editors.IdentifiersProcessor;
import ambit2.data.IDataContainers;
import ambit2.data.ISharedData;
import ambit2.data.descriptors.DescriptorsHashtable;
import ambit2.data.molecule.DataContainer;
import ambit2.data.molecule.PropertyTranslator;
import ambit2.io.AmbitSettingsListener;
import ambit2.io.FileInputState;
import ambit2.io.IteratingMolFolderReader;
import ambit2.io.ListOfMoleculesWriter;
import ambit2.io.MolPropertiesIOSetting;
import ambit2.io.MyIOUtilities;
import ambit2.io.batch.IBatchStatistics;
import ambit2.processors.IAmbitProcessor;
import ambit2.ui.DelimitersPanel;
import ambit2.ui.UITools;
import ambit2.ui.actions.BatchAction;

/**
 * File open.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class FileOpenAction extends BatchAction {
    protected IdentifiersProcessor identifiersProcessor = null;
    protected DescriptorsHashtable descriptorLookup = null;
    
	public FileOpenAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Open");
	}

	public FileOpenAction(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit2/ui/images/open_document_16.png"));
	}

	public FileOpenAction(Object userData, JFrame mainFrame, String name,
			Icon icon) {
		super(userData, mainFrame, name, icon);
		interactive = false;
	}
    public IIteratingChemObjectReader getFileReader(String defaultDir) {
        if (userData instanceof ISharedData) defaultDir = ((ISharedData) userData).getDefaultDir();
        DelimitersPanel accessory = new DelimitersPanel();
        File[] files = MyIOUtilities.selectFiles(mainFrame,null,
                defaultDir,
                FileInputState.extensions,FileInputState.extensionDescription,true,accessory);
        if (files == null) return null;
        if (files.length == 1)
        	return getFileReader(files[0],defaultDir,accessory.getFormat());
        else {
        	IIteratingChemObjectReader reader = new IteratingMolFolderReader(files);
			reader.addChemObjectIOListener(getReaderListener());
			if (userData instanceof ISharedData) ((ISharedData) userData).setDefaultDir(defaultDir);
        	return reader;
        }
    }
	public IIteratingChemObjectReader getReader() {
		return getFileReader("");
	}
	protected IReaderListener getReaderListener() {
		return new AmbitSettingsListener(mainFrame,IOSetting.LOW) {
	    	public void processIOSettingQuestion(IOSetting setting) {
	    		super.processIOSettingQuestion(setting);
	    		if (setting instanceof MolPropertiesIOSetting) {
	    			//descriptorLookup.putAll(((MolPropertiesIOSetting)setting).getProperties().getDescriptors());
	    			if (identifiersProcessor == null) identifiersProcessor = new IdentifiersProcessor();
	    			identifiersProcessor.addIdentifiers(((MolPropertiesIOSetting)setting).getProperties().getIdentifiers());
	    			if (userData instanceof IDataContainers) {
	    				IDataContainers dbaData = ((IDataContainers) userData);
	    				DataContainer list = (DataContainer) dbaData.getMolecules();
 
	    				list.getContainers().addAvailableProperties(((MolPropertiesIOSetting)setting).getProperties());
	    			}	
	    		}	
	    	}
	    };
	}
	/* (non-Javadoc)
     * @see ambit2.ui.actions.BatchAction#getProcessor()
     */
    public IAmbitProcessor getProcessor() {
		if (identifiersProcessor == null) identifiersProcessor = new IdentifiersProcessor();
        return identifiersProcessor;
    }
	public IChemObjectWriter getWriter() {
		if (userData instanceof IDataContainers) {
			IDataContainers dbaData = ((IDataContainers) userData);
			DataContainer list = (DataContainer) dbaData.getMolecules();
			list.clear();

			return new ListOfMoleculesWriter(list);
		} else return null;	
	}
    public IBatchStatistics getBatchStatistics() {
    	
    	IBatchStatistics bs = super.getBatchStatistics();
    	bs.setResultCaption("Loaded ");
    	return bs;
    }
}
