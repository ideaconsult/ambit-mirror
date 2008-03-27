package ambit2.ui.actions.dbadmin;

import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit2.database.DbConnection;
import ambit2.ui.editors.IdentifiersProcessor;
import ambit2.data.AmbitObject;
import ambit2.data.ISharedData;
import ambit2.data.descriptors.DescriptorsHashtable;
import ambit2.data.literature.ReferenceFactory;
import ambit2.data.molecule.SourceDataset;
import ambit2.data.molecule.SourceDatasetList;
import ambit2.database.core.DbSrcDataset;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.ISharedDbData;
import ambit2.database.processors.CASSmilesLookup;
import ambit2.database.processors.FindUniqueProcessor;
import ambit2.database.writers.DbSubstanceWriter;
import ambit2.exceptions.AmbitException;
import ambit2.io.AmbitSettingsListener;
import ambit2.io.FileInputState;
import ambit2.io.IteratingFileReader;
import ambit2.io.MolPropertiesIOSetting;
import ambit2.io.MyIOUtilities;
import ambit2.io.batch.DefaultBatchStatistics;
import ambit2.io.batch.IBatchStatistics;
import ambit2.processors.AromaticityProcessor;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ProcessorsChain;
import ambit2.processors.structure.SmilesGeneratorProcessor;
import ambit2.ui.DelimitersPanel;
import ambit2.ui.UITools;
import ambit2.ui.actions.BatchAction;

/**
 * Uses {@link ambit2.io.batch.IBatch} to import compounds from an user selected file into database. <br>
 * Example: creates two buttons, first uses {@link ambit2.ui.actions.dbadmin.DbOpenAction} to open a connection to database on click,
 * the second uses {@link DbBatchImportAction} to import user selected file into database.
 * The example makes use of {@link ambit2.database.data.AmbitDatabaseToolsData}, which encapsulates 
 * {@link ambit2.database.DbConnection} class and other information.  
 <pre>
 	
 	AmbitDatabaseToolsData dbadminData = new AmbitDatabaseToolsData(false);
 	DbOpenAction openAction = new DbOpenAction(dbadminData,null);
 	
 	JButton buttonOpen = new JButton(openAction);
 	
 	DbBatchImportAction importAction = new DbBatchImportAction(dbadminData,null,"Import",null);
 	JButton buttonImport = new JButton(importAction);
 	
 </pre>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbBatchImportAction extends BatchAction  {
	protected String defaultDir = "";
	protected SourceDataset dataset = null;
	protected DescriptorsHashtable descriptorLookup = null;
	protected String sourceFileName = "";
	protected IdentifiersProcessor identifiersProcessor = null;
	public DbBatchImportAction(Object userData, JFrame mainFrame) {
		this(userData,mainFrame,"Import compounds",UITools.createImageIcon("ambit2/ui/images/benzene_16.jpg"));
		
	}
	public DbBatchImportAction(Object userData, JFrame mainFrame, String name,Icon icon) {
		super(userData,mainFrame,name,icon);
		putValue(AbstractAction.SHORT_DESCRIPTION,"Imports compounds from user selected file.");
		descriptorLookup = new DescriptorsHashtable();
	}	
	public IIteratingChemObjectReader getReader() {
		if (userData instanceof ISharedData) defaultDir = ((ISharedData) userData).getDefaultDir();

        DelimitersPanel accessory = new DelimitersPanel();
        
		File file = MyIOUtilities.selectFile(mainFrame,null,
		        defaultDir,
		        FileInputState.extensions,FileInputState.extensionDescription,true,accessory);
		if (file != null) {
			String s = file.getAbsolutePath();
			int p = s.lastIndexOf(File.separator);
			if (p > 0)
			    defaultDir = s.substring(0,p);
			else defaultDir = s;
			try {
				sourceFileName = file.getName();
				dataset = getDataset(file.getName());
				IIteratingChemObjectReader reader = new IteratingFileReader(file,accessory.getFormat());
	    	    reader.addChemObjectIOListener(getReaderListener());

				if (userData instanceof ISharedData) ((ISharedData) userData).setDefaultDir(defaultDir);
				return reader;
			} catch (Exception x) {
				return null;
			}
		} else return null;
	}
    protected SourceDataset getDataset(final String filename) {
        if (userData instanceof AmbitDatabaseToolsData) {
            AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);
            SourceDataset dataset = dbaData.selectDataset(new SourceDatasetList() {
            		@Override
            		public AmbitObject createNewItem() {
    					return new SourceDataset(filename,ReferenceFactory.createDatasetReference(filename,""));
            		}
            },false, mainFrame,false);
            if (dataset == null)
                return new SourceDataset(filename,ReferenceFactory.createDatasetReference(sourceFileName, ""));
            else return dataset;
        } else     
            return new SourceDataset(filename,ReferenceFactory.createDatasetReference(sourceFileName, ""));
    }
	protected IReaderListener getReaderListener() {
		return new AmbitSettingsListener(mainFrame,IOSetting.LOW) {
	    	public void processIOSettingQuestion(IOSetting setting) {
	    		super.processIOSettingQuestion(setting);
	    		if (setting instanceof MolPropertiesIOSetting) {
	    			descriptorLookup.putAll(((MolPropertiesIOSetting)setting).getProperties().getDescriptors());
	    			if (identifiersProcessor == null) identifiersProcessor = new IdentifiersProcessor();
	    			identifiersProcessor.addIdentifiers(((MolPropertiesIOSetting)setting).getProperties().getIdentifiers());
	    		}	
	    	}
	    };
	}
	public IChemObjectWriter getWriter() {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
		    if ((dbaData.getDbConnection()==null) || dbaData.getDbConnection().isClosed()) {
		    	JOptionPane.showMessageDialog(mainFrame,"Not connected to database!");
		    	return null;
		    }
			ArrayList<String> aliases = new ArrayList<String>();
			aliases.add("ChemName_IUPAC");
			aliases.add("INChI");
			aliases.add("NSC");
			aliases.add("ID");
			aliases.add("Code");
			aliases.add("KEGG");
			DbSubstanceWriter writer =  new DbSubstanceWriter(dbaData.getDbConnection()
					,dataset,aliases,descriptorLookup);
			return writer;
		} else return null;
	}
	public IAmbitProcessor getProcessor() {
		ProcessorsChain processors = new ProcessorsChain();
		if (identifiersProcessor == null) identifiersProcessor = new IdentifiersProcessor();
		identifiersProcessor = new IdentifiersProcessor();
		processors.add(identifiersProcessor);
		
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
		    try {
		        IAmbitProcessor p = new CASSmilesLookup(dbaData.getDbConnection().getConn(),false);
		        p.setEnabled(false);
		        processors.add(p);
		        processors.add(new AromaticityProcessor());
		        SmilesGeneratorProcessor smigen = new SmilesGeneratorProcessor(5*60*1000);
				processors.add(smigen);
		    	processors.add(new FindUniqueProcessor(dbaData.getDbConnection().getConn()));
		    } catch (Exception x) {
		    	
		    }
		}    
		return processors;
		
	}

    public IBatchStatistics getBatchStatistics() {
    	IBatchStatistics bs = null;
    	if (userData instanceof AmbitDatabaseToolsData) 
			bs = ((AmbitDatabaseToolsData) userData).getBatchStatistics();
		else bs = new DefaultBatchStatistics();
    	bs.setResultCaption("Imported ");
    	return bs;
    }
    /* (non-Javadoc)
     * @see ambit2.ui.actions.AmbitAction#done()
     */
    public void done() {
        super.done();
        DbConnection dbc = ((ISharedDbData) userData).getDbConnection();
        if (dbc != null)
        ((AmbitDatabaseToolsData) userData).initDescriptors(dbc);
    }
    public synchronized SourceDataset getDataset() {
        return dataset;
    }
    public synchronized void setDataset(SourceDataset dataset) {
        this.dataset = dataset;
    }
    public synchronized DescriptorsHashtable getDescriptorLookup() {
        return descriptorLookup;
    }
    public synchronized void setDescriptorLookup(
            DescriptorsHashtable descriptorLookup) {
        this.descriptorLookup = descriptorLookup;
    }
}
