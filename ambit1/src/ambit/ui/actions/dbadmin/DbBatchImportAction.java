package ambit.ui.actions.dbadmin;

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

import ambit.data.AmbitObject;
import ambit.data.ISharedData;
import ambit.data.descriptors.DescriptorsHashtable;
import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.SourceDataset;
import ambit.data.molecule.SourceDatasetList;
import ambit.database.DbConnection;
import ambit.database.core.DbSrcDataset;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.database.exception.DbAmbitException;
import ambit.database.processors.CASSmilesLookup;
import ambit.database.processors.FindUniqueProcessor;
import ambit.database.writers.DbSubstanceWriter;
import ambit.exceptions.AmbitException;
import ambit.io.AmbitSettingsListener;
import ambit.io.FileInputState;
import ambit.io.IteratingFileReader;
import ambit.io.MolPropertiesIOSetting;
import ambit.io.MyIOUtilities;
import ambit.io.batch.DefaultBatchStatistics;
import ambit.io.batch.IBatchStatistics;
import ambit.processors.AromaticityProcessor;
import ambit.processors.IAmbitProcessor;
import ambit.processors.IdentifiersProcessor;
import ambit.processors.ProcessorsChain;
import ambit.processors.structure.SmilesGeneratorProcessor;
import ambit.ui.DelimitersPanel;
import ambit.ui.UITools;
import ambit.ui.actions.BatchAction;

/**
 * Uses {@link ambit.io.batch.IBatch} to import compounds from an user selected file into database. <br>
 * Example: creates two buttons, first uses {@link ambit.ui.actions.dbadmin.DbOpenAction} to open a connection to database on click,
 * the second uses {@link DbBatchImportAction} to import user selected file into database.
 * The example makes use of {@link ambit.database.data.AmbitDatabaseToolsData}, which encapsulates 
 * {@link ambit.database.DbConnection} class and other information.  
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
		this(userData,mainFrame,"Import compounds",UITools.createImageIcon("ambit/ui/images/benzene_16.jpg"));
		
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
    protected SourceDataset getDataset(final String filename) throws AmbitException {
        if (userData instanceof AmbitDatabaseToolsData) {
            AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);
            SourceDataset dataset = null;
            dataset = dbaData.selectDataset(new SourceDatasetList() {
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
		    	dbaData.getJobStatus().setError(new DbAmbitException(null,"Not connected to database!"));
		    	//JOptionPane.showMessageDialog(mainFrame,"Not connected to database!");
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
     * @see ambit.ui.actions.AmbitAction#done()
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
