package ambit.ui.actions.file;

import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit.database.aquire.DbAquireProcessor;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.database.processors.BatchFactory;
import ambit.database.processors.ReadAliasProcessor;
import ambit.database.processors.ReadCASProcessor;
import ambit.database.processors.ReadNameProcessor;
import ambit.database.processors.ReadSMILESProcessor;
import ambit.io.AmbitSettingsListener;
import ambit.io.MolPropertiesIOSetting;
import ambit.io.batch.DefaultBatchStatistics;
import ambit.io.batch.IBatchStatistics;
import ambit.processors.Builder3DProcessor;
import ambit.processors.IAmbitProcessor;
import ambit.processors.IdentifiersProcessor;
import ambit.processors.ProcessorsChain;
import ambit.ui.UITools;
import ambit.ui.actions.BatchAction;

/**
 * Batch processing of an user selected file. The results are stored in another file.
 * Example:
 * <pre>
 * AmbitDatabaseToolsData dbadminData = new AmbitDatabaseToolsData(true);
   dbadminData.loadData();
 * FileBatchProcessingAction batch = new FileBatchProcessingAction();
 * JButton button = new JButton(batch);
 * 
 * JOptionPane.showMessageDialog(null,button,"Click on button");
 * 
 * </pre>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class FileBatchProcessingAction extends BatchAction {
	protected String defaultDir = "";
	protected IdentifiersProcessor identifiersProcessor = null;
	
	public FileBatchProcessingAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Batch processing");
	}

	public FileBatchProcessingAction(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/batch.png"));
	}

	public FileBatchProcessingAction(Object userData, JFrame mainFrame, String name,
			Icon icon) {
		super(userData, mainFrame, name, icon);
		putValue(AbstractAction.SHORT_DESCRIPTION, "Reads compounds from a file,looks up for CAS and SMILES in the database and performs various calculations. Writes the result into an user specified file.");
	}

	public IIteratingChemObjectReader getReader() {
		return getFileReader(defaultDir);
	}

	public IChemObjectWriter getWriter() {
		return getFileWriter(defaultDir);
	}

    public IBatchStatistics getBatchStatistics() {
    	IBatchStatistics bs = null;
    	if (userData instanceof AmbitDatabaseToolsData) 
			bs = ((AmbitDatabaseToolsData) userData).getBatchStatistics();
		else bs = new DefaultBatchStatistics();
    	bs.setResultCaption("Processed ");
    	return bs;
    }
	public IAmbitProcessor getProcessor() {
		ProcessorsChain processors = new ProcessorsChain();
		
		if (userData instanceof ISharedDbData) {
		    
			Connection connection = ((ISharedDbData) userData).getDbConnection().getConn();
		    BatchFactory.getIdentifiersProcessors(connection,processors);
		    
		    identifiersProcessor = (IdentifiersProcessor) processors.getProcessor(0); 
		    IAmbitProcessor p;
		    
		    if (userData instanceof AmbitDatabaseToolsData) {
			    p = new Builder3DProcessor(((AmbitDatabaseToolsData)userData).getTemplateHandler());
				p.setEnabled(false);
			    processors.addProcessor(p);
		    }
		    
		    BatchFactory.getCalculationProcessors(connection,
		            ((AmbitDatabaseToolsData) userData).getDescriptors(),
		            processors);
		    try {
		    	p = new ReadSMILESProcessor(connection);
		    	p.setEnabled(false);
			    processors.addProcessor(p);
			    p = new ReadCASProcessor(connection);
			    p.setEnabled(false);
			    processors.addProcessor(p);
			    p = new ReadNameProcessor(connection);
			    p.setEnabled(false);			    
			    processors.addProcessor(p);
			    p = new ReadAliasProcessor(connection);
			    p.setEnabled(false);			    
			    processors.addProcessor(p);
			    
			    if (userData instanceof AmbitDatabaseToolsData) {
			    	
			    	AmbitDatabaseToolsData dba = ((AmbitDatabaseToolsData) userData);
				    p = new DbAquireProcessor(connection,
							dba.getHost(),	
							dba.getPort(),
							dba.getUser().getName(),
							dba.getUser().getPassword(),
				    		dba.getAquire_endpoint(),
				    		dba.getAquire_species(),
				    		dba.isAquire_simpletemplate());
				    p.setEnabled(false);			    
				    processors.addProcessor(p);			    
			    }
		    } catch (Exception x) {
		    	logger.error(x);
		    }
		    
		}
		return processors;
	}
	protected IReaderListener getReaderListener() {
		return new AmbitSettingsListener(mainFrame,IOSetting.LOW) {
	    	public void processIOSettingQuestion(IOSetting setting) {
	    		super.processIOSettingQuestion(setting);
	    		if (setting instanceof MolPropertiesIOSetting) {
	    			//descriptorLookup.putAll(((MolPropertiesIOSetting)setting).getProperties().getDescriptors());
	    			if (identifiersProcessor == null) identifiersProcessor = new IdentifiersProcessor();
	    			identifiersProcessor.addIdentifiers(((MolPropertiesIOSetting)setting).getProperties().getIdentifiers());
	    		}	
	    	}
	    };
	}
}
